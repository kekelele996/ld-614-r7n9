package com.generated.qualityTrace.constants;

/**
 * 检验结论：PASS 合格 / FAIL 不合格 / CONDITIONAL_PASS 让步接收 / RECHECK 待复检。
 * 放行规则：最近检验 FAIL 或 RECHECK 拒绝；CONDITIONAL_PASS 只允许有条件放行。
 * 出现位置：models/QualityInspection、constants/InspectionResultStatus、types/QualityInspectionPayload、
 * constructors/QualityInspectionDtoFactory、constructors/BatchReleaseDtoFactory、
 * repositories/QualityInspectionRepository、services/BatchReleaseService、
 * services/QualityTraceQueryService、validators/BatchReleaseValidator、
 * constants/LogTemplates、constants/ErrorMessages、utils/Formatters。
 */
public enum InspectionResultStatus {
  PASS,
  FAIL,
  CONDITIONAL_PASS,
  RECHECK;

  public boolean isRejectingRelease() {
    return this == FAIL || this == RECHECK;
  }

  public boolean isConditionalPass() {
    return this == CONDITIONAL_PASS;
  }
}
