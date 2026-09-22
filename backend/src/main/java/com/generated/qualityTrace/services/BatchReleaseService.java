package com.generated.qualityTrace.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.ReleaseBasisAssembler;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.exceptions.ReleaseAlreadyExistsException;
import com.generated.qualityTrace.exceptions.ReleaseBlockedException;
import com.generated.qualityTrace.middlewares.AuditLogMiddleware;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.BatchReleaseRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.BatchReleaseResultPayload;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;
import com.generated.qualityTrace.types.ReleaseDecisionPayload;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.validators.BatchReleaseValidator;
import com.generated.qualityTrace.validators.BatchReleaseValidator.ValidatedDecision;

/**
 * 批次质量放行领域服务。
 *
 * <p>规则：
 * <ol>
 *   <li>仅质量经理可提交（RBAC）；</li>
 *   <li>存在未关闭的重大（CRITICAL）/严重（MAJOR）不良，或最近检验为不合格（FAIL）/待复检（RECHECK），
 *       直接拒绝并返回全部阻塞项；</li>
 *   <li>一般不良（MINOR）未关闭，或最近检验为让步接收（CONDITIONAL_PASS）时，
 *       只允许有条件放行（CONDITIONAL_RELEASED），且限制说明必填；</li>
 *   <li>成功后记录放行人和时间、批次状态更新、写审计；</li>
 *   <li>重复/并发提交只能生效一次（每批一锁 + 仓库原子插入）；</li>
 *   <li>任何失败路径：批次、不良、放行记录均保持原样。</li>
 * </ol>
 */
@Service
public class BatchReleaseService {
  private static final Logger log = LoggerFactory.getLogger(BatchReleaseService.class);

  private final ProductBatchRepository batchRepository;
  private final DefectRecordRepository defectRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final BatchReleaseRepository releaseRepository;
  private final BatchReleaseValidator validator;
  private final RbacMiddleware rbacMiddleware;
  private final AuditLogMiddleware auditLogMiddleware;

  /** 每批次一把锁，串行化同一批次的并发提交；不同批次互不阻塞。 */
  private final ConcurrentHashMap<Long, Object> batchLocks = new ConcurrentHashMap<>();

  public BatchReleaseService(ProductBatchRepository batchRepository,
      DefectRecordRepository defectRepository,
      QualityInspectionRepository inspectionRepository,
      BatchReleaseRepository releaseRepository,
      BatchReleaseValidator validator,
      RbacMiddleware rbacMiddleware,
      AuditLogMiddleware auditLogMiddleware) {
    this.batchRepository = batchRepository;
    this.defectRepository = defectRepository;
    this.inspectionRepository = inspectionRepository;
    this.releaseRepository = releaseRepository;
    this.validator = validator;
    this.rbacMiddleware = rbacMiddleware;
    this.auditLogMiddleware = auditLogMiddleware;
  }

  public BatchReleaseResultPayload submit(ReleaseDecisionPayload payload) {
    ValidatedDecision decision = validator.validate(payload);

    // 1. 解析批次（先按ID，再按批次号）
    ProductBatch batch = resolveBatch(decision.batchId(), decision.batchNo());

    log.info(MessageFormat.format(LogTemplates.BATCH_RELEASE_SUBMIT,
        decision.managerId(), String.valueOf(decision.managerRole()),
        String.valueOf(batch.id), batch.batchNo, decision.conclusion().name()));

    // 2. RBAC：仅质量经理
    rbacMiddleware.assertCanReleaseBatch(decision.managerRole());

    Object lock = batchLocks.computeIfAbsent(batch.id, key -> new Object());
    synchronized (lock) {
      // 3. 锁内二次确认重复提交（双检）
      Optional<BatchRelease> existing = releaseRepository.findByBatchId(batch.id);
      if (existing.isPresent()) {
        return rejectDuplicate(batch, existing.get(), decision);
      }

      // 4. 收集事实：阻塞项 + 条件项 + 支持项
      ReleaseFacts facts = collectFacts(batch);

      // 5. 硬阻塞：返回全部阻塞项，批次/不良/放行记录保持原样
      if (!facts.blockers().isEmpty()) {
        return rejectBlocked(batch, decision, facts);
      }

      // 6. 结论与限制说明校验（仅在无硬阻塞时；让步接收/未关闭一般不良只允许有条件放行）
      validator.validateConclusionAgainstFacts(batch.id, decision.conclusion(),
          decision.limitNote(), facts.blockers(), facts.conditions());

      // 7. 构造放行记录并原子落库（仅一次生效）
      BatchRelease release = buildRelease(batch, decision, facts);
      boolean inserted = releaseRepository.insertIfAbsent(release);
      if (!inserted) {
        // 并发竞争失败：本提交未产生任何写入，返回既有记录
        BatchRelease winner = releaseRepository.findByBatchId(batch.id).orElseThrow();
        log.warn(MessageFormat.format(LogTemplates.BATCH_RELEASE_DUPLICATE,
            String.valueOf(batch.id), batch.batchNo, decision.managerId(),
            String.valueOf(winner.id), winner.releasedAt));
        throw new ReleaseAlreadyExistsException(
            String.format(ErrorMessages.RELEASE_ALREADY_EXISTS, batch.batchNo,
                winner.releasedAt, winner.releasedBy));
      }

      // 8. 原子插入成功后才更新批次状态并记录审计（失败不会走到这里）
      BatchStatus newStatus = decision.conclusion() == com.generated.qualityTrace.constants.ReleaseConclusion.CONDITIONAL_RELEASED
          ? BatchStatus.CONDITIONAL_RELEASED
          : BatchStatus.RELEASED;
      batchRepository.updateStatus(batch.id, newStatus);

      auditLogMiddleware.record(decision.managerId(), LogTemplates.ACTION_BATCH_RELEASE,
          "ProductBatch", batch.id,
          MessageFormat.format(LogTemplates.BATCH_RELEASE_SUCCESS,
              String.valueOf(batch.id), batch.batchNo, String.valueOf(release.id),
              decision.conclusion().name(), decision.managerId(), release.releasedAt));

      log.info(MessageFormat.format(LogTemplates.BATCH_RELEASE_SUCCESS,
          String.valueOf(batch.id), batch.batchNo, String.valueOf(release.id),
          decision.conclusion().name(), decision.managerId(), release.releasedAt));

      return com.generated.qualityTrace.constructors.BatchReleaseDtoFactory.toResult(
          true, newStatus.name(), release, facts.basisSnapshot());
    }
  }

  private ProductBatch resolveBatch(Long batchId, String batchNo) {
    Optional<ProductBatch> found = batchId != null
        ? batchRepository.findById(batchId)
        : batchRepository.findByBatchNo(batchNo);
    return found.orElseThrow(() -> new BatchNotFoundException(
        String.format(ErrorMessages.BATCH_NOT_FOUND,
            batchId != null ? "id=" + batchId : "batchNo=" + batchNo)));
  }

  private BatchReleaseResultPayload rejectDuplicate(ProductBatch batch,
      BatchRelease existing, ValidatedDecision decision) {
    log.warn(MessageFormat.format(LogTemplates.BATCH_RELEASE_DUPLICATE,
        String.valueOf(batch.id), batch.batchNo, decision.managerId(),
        String.valueOf(existing.id), existing.releasedAt));
    auditLogMiddleware.record(decision.managerId(), LogTemplates.ACTION_BATCH_RELEASE_BLOCKED,
        "ProductBatch", batch.id,
        MessageFormat.format(LogTemplates.BATCH_RELEASE_DUPLICATE,
            String.valueOf(batch.id), batch.batchNo, decision.managerId(),
            String.valueOf(existing.id), existing.releasedAt));
    throw new ReleaseAlreadyExistsException(
        String.format(ErrorMessages.RELEASE_ALREADY_EXISTS, batch.batchNo,
            existing.releasedAt, existing.releasedBy));
  }

  private BatchReleaseResultPayload rejectBlocked(ProductBatch batch,
      ValidatedDecision decision, ReleaseFacts facts) {
    String detail = MessageFormat.format(LogTemplates.BATCH_RELEASE_REJECTED,
        String.valueOf(batch.id), batch.batchNo, decision.managerId(),
        decision.conclusion().name(), String.valueOf(facts.blockers().size()));
    auditLogMiddleware.record(decision.managerId(),
        LogTemplates.ACTION_BATCH_RELEASE_BLOCKED, "ProductBatch", batch.id, detail);
    log.warn(detail);
    throw new ReleaseBlockedException(
        String.format(ErrorMessages.RELEASE_BLOCKED, batch.batchNo, facts.blockers().size()),
        facts.blockers());
  }

  private ReleaseFacts collectFacts(ProductBatch batch) {
    List<ReleaseBasisItemPayload> blockers = new ArrayList<>();
    List<ReleaseBasisItemPayload> conditions = new ArrayList<>();
    List<ReleaseBasisItemPayload> supports = new ArrayList<>();

    // 不良事实
    List<DefectRecord> defects = defectRepository.findByBatchId(batch.id);
    for (DefectRecord defect : defects) {
      if (defect.dispositionStatus == DefectDispositionStatus.OPEN
          && defect.severity.isMajorOrCritical()) {
        blockers.add(ReleaseBasisAssembler.openMajorOrCriticalDefect(defect));
      } else if (defect.dispositionStatus == DefectDispositionStatus.OPEN
          && defect.severity == com.generated.qualityTrace.constants.DefectSeverity.MINOR) {
        conditions.add(ReleaseBasisAssembler.openMinorDefect(defect));
      } else if (defect.dispositionStatus == DefectDispositionStatus.CLOSED) {
        supports.add(ReleaseBasisAssembler.closedDefect(defect));
      }
    }

    // 最近检验事实
    Optional<QualityInspection> latest = inspectionRepository.findLatestByBatchId(batch.id);
    if (latest.isEmpty()) {
      blockers.add(ReleaseBasisAssembler.noInspection());
    } else {
      QualityInspection inspection = latest.get();
      if (inspection.resultStatus.isRejectingRelease()) {
        if (inspection.resultStatus
            == com.generated.qualityTrace.constants.InspectionResultStatus.FAIL) {
          blockers.add(ReleaseBasisAssembler.latestInspectionFail(inspection));
        } else {
          blockers.add(ReleaseBasisAssembler.latestInspectionRecheck(inspection));
        }
      } else if (inspection.resultStatus.isConditionalPass()) {
        conditions.add(ReleaseBasisAssembler.latestInspectionConditional(inspection));
      } else {
        supports.add(ReleaseBasisAssembler.latestInspectionPass(inspection));
      }
    }

    List<ReleaseBasisItemPayload> basis = new ArrayList<>();
    basis.addAll(blockers);
    basis.addAll(conditions);
    basis.addAll(supports);
    return new ReleaseFacts(blockers, conditions, supports, basis, defects, latest.orElse(null));
  }

  private BatchRelease buildRelease(ProductBatch batch, ValidatedDecision decision,
      ReleaseFacts facts) {
    long id = releaseRepository.nextId();
    String releasedAt = Formatters.nowIso();
    String limitNote =
        decision.conclusion() == com.generated.qualityTrace.constants.ReleaseConclusion.CONDITIONAL_RELEASED
            ? decision.limitNote()
            : "";
    String basisJson = serializeBasis(facts.basisSnapshot());
    return new BatchRelease(id, batch.id, batch.batchNo, decision.conclusion(),
        limitNote, decision.managerId(), releasedAt, basisJson);
  }

  /** 轻量 JSON 序列化依据快照，避免散落依赖；结构为数组对象。 */
  private String serializeBasis(List<ReleaseBasisItemPayload> basis) {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < basis.size(); i++) {
      ReleaseBasisItemPayload item = basis.get(i);
      if (i > 0) {
        sb.append(',');
      }
      sb.append("{\"kind\":").append(json(item.kind()))
          .append(",\"code\":").append(json(item.code()))
          .append(",\"message\":").append(json(item.message()))
          .append(",\"refType\":").append(json(item.refType()))
          .append(",\"refId\":").append(json(item.refId()))
          .append(",\"severity\":").append(json(item.severity()))
          .append(",\"resultStatus\":").append(json(item.resultStatus()))
          .append('}');
    }
    return sb.append(']').toString();
  }

  private static String json(String value) {
    if (value == null) {
      return "null";
    }
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }

  public Optional<BatchRelease> findByBatchId(Long batchId) {
    return releaseRepository.findByBatchId(batchId);
  }

  /** 放行事实集合（内部值对象）。 */
  private record ReleaseFacts(
      List<ReleaseBasisItemPayload> blockers,
      List<ReleaseBasisItemPayload> conditions,
      List<ReleaseBasisItemPayload> supports,
      List<ReleaseBasisItemPayload> basisSnapshot,
      List<DefectRecord> defects,
      QualityInspection latestInspection) {
  }
}
