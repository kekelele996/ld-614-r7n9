package com.generated.qualityTrace.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.ReleaseDecision;
import com.generated.qualityTrace.constructors.BatchReleaseDtoFactory;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.exceptions.ReleaseBlockedException;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.repositories.BatchReleaseRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.types.BatchReleasePayload;
import com.generated.qualityTrace.types.ReleaseBlocker;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.validators.BatchReleaseValidator;

@Service
public class BatchReleaseService {
  private static final Logger log = LoggerFactory.getLogger(BatchReleaseService.class);

  private final ProductBatchRepository batchRepo;
  private final QualityInspectionRepository inspectionRepo;
  private final DefectRecordRepository defectRepo;
  private final BatchReleaseRepository releaseRepo;
  /** 按批次加锁，保证并发提交只有一笔生效 */
  private final ConcurrentHashMap<Long, Object> locks = new ConcurrentHashMap<>();

  public BatchReleaseService(ProductBatchRepository batchRepo,
                             QualityInspectionRepository inspectionRepo,
                             DefectRecordRepository defectRepo,
                             BatchReleaseRepository releaseRepo) {
    this.batchRepo = batchRepo;
    this.inspectionRepo = inspectionRepo;
    this.defectRepo = defectRepo;
    this.releaseRepo = releaseRepo;
  }

  public Map<String, Object> release(String batchNo, BatchReleasePayload payload, String operator) {
    log.info(LogTemplates.RELEASE_SUBMIT, batchNo, operator, payload == null ? null : payload.decision());
    ProductBatch batch = requireBatch(batchNo);
    Object lock = locks.computeIfAbsent(batch.id, k -> new Object());
    synchronized (lock) {
      BatchRelease existing = releaseRepo.findByBatchId(batch.id);
      if (existing != null) {
        log.info(LogTemplates.RELEASE_DUPLICATE, batchNo, operator);
        throw new BizException(ErrorCodes.RELEASE_ALREADY_EXISTS, ErrorMessages.RELEASE_ALREADY_EXISTS, 409);
      }

      List<DefectRecord> defects = defectRepo.findByBatchId(batch.id);
      QualityInspection latest = inspectionRepo.findLatestByBatchId(batch.id);

      List<ReleaseBlocker> blockers = collectBlockers(defects, latest);
      if (!blockers.isEmpty()) {
        log.info(LogTemplates.RELEASE_BLOCKED, batchNo, blockers.size());
        throw new ReleaseBlockedException(ErrorMessages.RELEASE_BLOCKED, blockers);
      }

      boolean conditionalOnly = isConditionalOnly(defects, latest);
      ReleaseDecision decision = BatchReleaseValidator.validate(payload, conditionalOnly);

      BatchRelease record = new BatchRelease(null, batch.id, batch.batchNo, decision.name(),
          payload.restrictionNote(), operator, OffsetDateTime.now().toString(),
          buildBasis(defects, latest, conditionalOnly));

      try {
        BatchRelease saved = releaseRepo.saveIfAbsent(record);
        if (saved == null) {
          // 并发安全网：同一批次只生效一次
          log.info(LogTemplates.RELEASE_DUPLICATE, batchNo, operator);
          throw new BizException(ErrorCodes.RELEASE_ALREADY_EXISTS, ErrorMessages.RELEASE_ALREADY_EXISTS, 409);
        }
        BatchStatus newStatus = decision == ReleaseDecision.CONDITIONAL_RELEASED
            ? BatchStatus.CONDITIONAL_RELEASED : BatchStatus.RELEASED;
        batchRepo.updateStatus(batch.id, newStatus.name());
        log.info(LogTemplates.RELEASE_SUCCESS, batchNo, decision.name(), operator);
        return BatchReleaseDtoFactory.releaseView(saved);
      } catch (RuntimeException e) {
        // 失败回滚：批次、不良、放行记录全部保持原样
        releaseRepo.deleteByBatchId(batch.id);
        throw e;
      }
    }
  }

  public Map<String, Object> getByBatchNo(String batchNo) {
    ProductBatch batch = requireBatch(batchNo);
    BatchRelease record = releaseRepo.findByBatchId(batch.id);
    if (record == null) {
      throw new BizException(ErrorCodes.RELEASE_NOT_FOUND, ErrorMessages.RELEASE_NOT_FOUND, 404);
    }
    return BatchReleaseDtoFactory.releaseView(record);
  }

  private ProductBatch requireBatch(String batchNo) {
    ProductBatch batch = batchRepo.findByBatchNo(batchNo);
    if (batch == null) {
      throw new BizException(ErrorCodes.BATCH_NOT_FOUND, ErrorMessages.BATCH_NOT_FOUND, 404);
    }
    return batch;
  }

  /** 收集全部阻塞项：未关闭的重大/严重不良、最近检验不合格或待复检 */
  private List<ReleaseBlocker> collectBlockers(List<DefectRecord> defects, QualityInspection latest) {
    List<ReleaseBlocker> blockers = new ArrayList<>();
    for (DefectRecord defect : defects) {
      boolean unclosed = !DispositionStatus.CLOSED.name().equals(defect.dispositionStatus);
      if (unclosed && DefectSeverity.MAJOR.name().equals(defect.severity)) {
        blockers.add(new ReleaseBlocker("UNCLOSED_MAJOR_DEFECT",
            "存在未关闭的" + Formatters.severityText(defect.severity) + "不良：缺陷#" + defect.id
                + "（" + defect.defectType + "，数量=" + defect.defectQty + "）"));
      }
      if (unclosed && DefectSeverity.CRITICAL.name().equals(defect.severity)) {
        blockers.add(new ReleaseBlocker("UNCLOSED_CRITICAL_DEFECT",
            "存在未关闭的" + Formatters.severityText(defect.severity) + "不良：缺陷#" + defect.id
                + "（" + defect.defectType + "，数量=" + defect.defectQty + "）"));
      }
    }
    if (latest == null) {
      blockers.add(new ReleaseBlocker("NO_INSPECTION", "批次尚无检验记录"));
    } else if (InspectionResultStatus.FAIL.name().equals(latest.resultStatus)) {
      blockers.add(new ReleaseBlocker("LATEST_INSPECTION_FAIL",
          "最近检验结果为" + Formatters.inspectionResultText(latest.resultStatus) + "（检验单#" + latest.id + "）"));
    } else if (InspectionResultStatus.RECHECK.name().equals(latest.resultStatus)) {
      blockers.add(new ReleaseBlocker("LATEST_INSPECTION_RECHECK",
          "最近检验结果为" + Formatters.inspectionResultText(latest.resultStatus) + "（检验单#" + latest.id + "）"));
    }
    return blockers;
  }

  /** 一般不良已关闭且最近检验为让步接收时，仅允许有条件放行 */
  private boolean isConditionalOnly(List<DefectRecord> defects, QualityInspection latest) {
    boolean hasClosedMinor = defects.stream().anyMatch(d ->
        DefectSeverity.MINOR.name().equals(d.severity) && DispositionStatus.CLOSED.name().equals(d.dispositionStatus));
    boolean latestConditional = latest != null
        && InspectionResultStatus.CONDITIONAL_PASS.name().equals(latest.resultStatus);
    return hasClosedMinor && latestConditional;
  }

  /** 放行依据快照，供追溯查询展示 */
  private List<String> buildBasis(List<DefectRecord> defects, QualityInspection latest, boolean conditionalOnly) {
    List<String> basis = new ArrayList<>();
    basis.add("最近检验结果：" + Formatters.inspectionResultText(latest.resultStatus)
        + "（检验单#" + latest.id + "，" + latest.inspectedAt + "）");
    long unclosedMajorCritical = defects.stream().filter(d ->
        !DispositionStatus.CLOSED.name().equals(d.dispositionStatus)
            && (DefectSeverity.MAJOR.name().equals(d.severity) || DefectSeverity.CRITICAL.name().equals(d.severity)))
        .count();
    basis.add("未关闭重大/严重不良：" + unclosedMajorCritical + " 项");
    long closedMinor = defects.stream().filter(d ->
        DefectSeverity.MINOR.name().equals(d.severity) && DispositionStatus.CLOSED.name().equals(d.dispositionStatus))
        .count();
    basis.add("一般不良（已关闭）：" + closedMinor + " 项");
    basis.add(conditionalOnly
        ? "放行规则：一般不良已关闭且最近检验为让步接收，仅允许有条件放行"
        : "放行规则：无阻塞项，允许放行");
    return basis;
  }
}
