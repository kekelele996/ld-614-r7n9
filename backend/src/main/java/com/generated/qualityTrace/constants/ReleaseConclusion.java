package com.generated.qualityTrace.constants;

/**
 * 批次质量放行结论。
 * RELEASED：放行；CONDITIONAL_RELEASED：有条件放行（必须填写限制说明）。
 * 出现位置：models/BatchRelease、constants/ReleaseConclusion、types/ReleaseDecisionPayload、
 * constructors/BatchReleaseDtoFactory、validators/BatchReleaseValidator、
 * constants/LogTemplates、constants/ErrorMessages、utils/Formatters、
 * services/BatchReleaseService、controllers/BatchReleaseController、repositories/BatchReleaseRepository。
 */
public enum ReleaseConclusion {
  RELEASED,
  CONDITIONAL_RELEASED
}
