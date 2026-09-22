package com.generated.qualityTrace.services;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.BatchReleaseDtoFactory;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.constructors.ReleaseBasisParser;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.middlewares.AuditLogMiddleware;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.BatchReleaseRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.BatchTracePayload;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/**
 * 批次全链路追溯查询服务（GET /api/trace/{batchNo}）。
 * 展示：批次、工单、检验、不良，以及放行结论 / 限制说明 / 放行人和时间 / 对应依据。
 */
@Service
public class QualityTraceQueryService {
  private static final Logger log = LoggerFactory.getLogger(QualityTraceQueryService.class);

  private final ProductBatchRepository batchRepository;
  private final WorkOrderRepository workOrderRepository;
  private final QualityInspectionRepository inspectionRepository;
  private final DefectRecordRepository defectRepository;
  private final BatchReleaseRepository releaseRepository;
  private final AuditLogMiddleware auditLogMiddleware;

  public QualityTraceQueryService(ProductBatchRepository batchRepository,
      WorkOrderRepository workOrderRepository,
      QualityInspectionRepository inspectionRepository,
      DefectRecordRepository defectRepository,
      BatchReleaseRepository releaseRepository,
      AuditLogMiddleware auditLogMiddleware) {
    this.batchRepository = batchRepository;
    this.workOrderRepository = workOrderRepository;
    this.inspectionRepository = inspectionRepository;
    this.defectRepository = defectRepository;
    this.releaseRepository = releaseRepository;
    this.auditLogMiddleware = auditLogMiddleware;
  }

  public BatchTracePayload trace(String batchNo, String actor) {
    ProductBatch batch = batchRepository.findByBatchNo(batchNo)
        .orElseThrow(() -> new BatchNotFoundException(
            String.format(ErrorMessages.BATCH_NOT_FOUND, "batchNo=" + batchNo)));

    Map<String, Object> workOrder = workOrderRepository.findById(batch.workOrderId)
        .orElse(Map.of());

    List<Map<String, Object>> inspections = inspectionRepository.findByBatchId(batch.id)
        .stream().map(QualityInspectionDtoFactory::toDto).toList();

    List<Map<String, Object>> defects = defectRepository.findByBatchId(batch.id)
        .stream().map(DefectRecordDtoFactory::toDto).toList();

    Optional<BatchRelease> releaseOpt = releaseRepository.findByBatchId(batch.id);
    Map<String, Object> releaseDto;
    if (releaseOpt.isPresent()) {
      BatchRelease release = releaseOpt.get();
      List<ReleaseBasisItemPayload> basis = ReleaseBasisParser.parse(release.basis);
      releaseDto = BatchReleaseDtoFactory.toDto(release, basis);
    } else {
      releaseDto = BatchReleaseDtoFactory.create();
      releaseDto.put("batchId", batch.id);
      releaseDto.put("batchNo", batch.batchNo);
      releaseDto.put("released", false);
    }
    if (releaseOpt.isPresent()) {
      releaseDto.put("released", true);
    }

    auditLogMiddleware.record(actor, LogTemplates.ACTION_BATCH_TRACE,
        "ProductBatch", batch.id,
        MessageFormat.format(LogTemplates.BATCH_TRACE_QUERY, batchNo, actor,
            String.valueOf(releaseOpt.isPresent())));
    log.info(MessageFormat.format(LogTemplates.BATCH_TRACE_QUERY, batchNo, actor,
        String.valueOf(releaseOpt.isPresent())));

    return new BatchTracePayload(
        ProductBatchDtoFactory.toDto(batch), workOrder, inspections, defects, releaseDto);
  }
}
