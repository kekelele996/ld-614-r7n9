package com.generated.qualityTrace.services;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.BatchReleaseDtoFactory;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.constructors.ProductBatchDtoFactory;
import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.BatchReleaseRepository;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;

@Service
public class TraceService {
  private static final Logger log = LoggerFactory.getLogger(TraceService.class);

  private final ProductBatchRepository batchRepo;
  private final QualityInspectionRepository inspectionRepo;
  private final DefectRecordRepository defectRepo;
  private final BatchReleaseRepository releaseRepo;

  public TraceService(ProductBatchRepository batchRepo,
                      QualityInspectionRepository inspectionRepo,
                      DefectRecordRepository defectRepo,
                      BatchReleaseRepository releaseRepo) {
    this.batchRepo = batchRepo;
    this.inspectionRepo = inspectionRepo;
    this.defectRepo = defectRepo;
    this.releaseRepo = releaseRepo;
  }

  /** 批次全链路追溯树：批次、检验、不良、放行结论（含限制说明与依据） */
  public Map<String, Object> trace(String batchNo) {
    log.info(LogTemplates.TRACE_QUERY, batchNo);
    ProductBatch batch = batchRepo.findByBatchNo(batchNo);
    if (batch == null) {
      throw new BizException(ErrorCodes.BATCH_NOT_FOUND, ErrorMessages.BATCH_NOT_FOUND, 404);
    }
    Map<String, Object> tree = new LinkedHashMap<>();
    tree.put("batch", ProductBatchDtoFactory.view(batch));
    tree.put("inspections", inspectionRepo.findByBatchId(batch.id).stream()
        .map(QualityInspectionDtoFactory::view).toList());
    tree.put("defects", defectRepo.findByBatchId(batch.id).stream()
        .map(DefectRecordDtoFactory::view).toList());
    BatchRelease release = releaseRepo.findByBatchId(batch.id);
    tree.put("release", release == null ? null : BatchReleaseDtoFactory.traceReleaseView(release));
    return tree;
  }
}
