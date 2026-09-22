package com.generated.qualityTrace.constants;

/**
 * 产品批次状态。
 * 出现位置：models/ProductBatch、constants/BatchStatus、constructors/ProductBatchDtoFactory、
 * constructors/BatchReleaseDtoFactory、constants/LogTemplates、constants/ErrorMessages、
 * utils/Formatters、services/BatchReleaseService、services/QualityTraceQueryService、
 * repositories/ProductBatchRepository、controllers/BatchReleaseController。
 */
public enum BatchStatus {
  PENDING,
  READY,
  BLOCKED,
  RELEASED,
  CONDITIONAL_RELEASED
}
