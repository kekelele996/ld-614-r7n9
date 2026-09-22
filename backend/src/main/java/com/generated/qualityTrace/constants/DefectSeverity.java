package com.generated.qualityTrace.constants;

/**
 * 不良严重度：MINOR 一般不良；MAJOR 严重不良；CRITICAL 重大不良。
 * 放行规则：存在未关闭的 MAJOR 或 CRITICAL 时拒绝放行。
 * 出现位置：models/DefectRecord、constants/DefectSeverity、types/DefectRecordPayload、
 * constructors/DefectRecordDtoFactory、constructors/BatchReleaseDtoFactory、
 * repositories/DefectRecordRepository、services/BatchReleaseService、
 * validators/BatchReleaseValidator、constants/LogTemplates、constants/ErrorMessages、utils/Formatters。
 */
public enum DefectSeverity {
  MINOR,
  MAJOR,
  CRITICAL;

  /** 重大（CRITICAL）或严重（MAJOR）不良，属于放行硬阻塞项。 */
  public boolean isMajorOrCritical() {
    return this == MAJOR || this == CRITICAL;
  }
}
