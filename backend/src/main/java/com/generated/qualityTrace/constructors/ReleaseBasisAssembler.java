package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/**
 * 放行依据条目构造器：把不良/检验事实转换为可追溯的结构化依据。
 * kind=BLOCKER 阻塞项 / CONDITION 条件项 / SUPPORT 支持项。
 */
public final class ReleaseBasisAssembler {

  private ReleaseBasisAssembler() {}

  /** 未关闭的重大或严重不良 -> 阻塞项。 */
  public static ReleaseBasisItemPayload openMajorOrCriticalDefect(DefectRecord defect) {
    String message = String.format(ErrorMessages.OPEN_MAJOR_OR_CRITICAL_DEFECT,
        DefectRecordDtoFactory.severityText(defect.severity),
        defect.id, defect.defectType, defect.dispositionStatus.name());
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_BLOCKER,
        "OPEN_MAJOR_OR_CRITICAL_DEFECT",
        message,
        "DEFECT",
        String.valueOf(defect.id),
        defect.severity.name(),
        null);
  }

  /** 最近检验不合格 -> 阻塞项。 */
  public static ReleaseBasisItemPayload latestInspectionFail(QualityInspection inspection) {
    String message = String.format(ErrorMessages.LATEST_INSPECTION_FAIL,
        inspection.id, inspection.inspectionType, inspection.inspectedAt);
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_BLOCKER,
        "LATEST_INSPECTION_FAIL",
        message,
        "INSPECTION",
        String.valueOf(inspection.id),
        null,
        InspectionResultStatus.FAIL.name());
  }

  /** 最近检验待复检 -> 阻塞项。 */
  public static ReleaseBasisItemPayload latestInspectionRecheck(QualityInspection inspection) {
    String message = String.format(ErrorMessages.LATEST_INSPECTION_RECHECK,
        inspection.id, inspection.inspectionType, inspection.inspectedAt);
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_BLOCKER,
        "LATEST_INSPECTION_RECHECK",
        message,
        "INSPECTION",
        String.valueOf(inspection.id),
        null,
        inspection.resultStatus.name());
  }

  /** 最近检验让步接收 -> 条件项（只允许有条件放行）。 */
  public static ReleaseBasisItemPayload latestInspectionConditional(
      QualityInspection inspection) {
    String message = String.format(ErrorMessages.LATEST_INSPECTION_CONDITIONAL,
        inspection.id, inspection.inspectionType, inspection.inspectedAt);
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_CONDITION,
        "LATEST_INSPECTION_CONDITIONAL_PASS",
        message,
        "INSPECTION",
        String.valueOf(inspection.id),
        null,
        InspectionResultStatus.CONDITIONAL_PASS.name());
  }

  /** 未关闭的一般不良 -> 条件项（非硬阻塞，但限制放行方式）。 */
  public static ReleaseBasisItemPayload openMinorDefect(DefectRecord defect) {
    String message = String.format(ErrorMessages.OPEN_MINOR_DEFECT,
        defect.id, defect.defectType, defect.dispositionStatus.name());
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_CONDITION,
        "OPEN_MINOR_DEFECT",
        message,
        "DEFECT",
        String.valueOf(defect.id),
        DefectSeverity.MINOR.name(),
        null);
  }

  /** 最近检验合格 -> 支持项。 */
  public static ReleaseBasisItemPayload latestInspectionPass(QualityInspection inspection) {
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_SUPPORT,
        "LATEST_INSPECTION_PASS",
        "latest inspection passed: inspectionId=" + inspection.id
            + ", inspectedAt=" + inspection.inspectedAt,
        "INSPECTION",
        String.valueOf(inspection.id),
        null,
        InspectionResultStatus.PASS.name());
  }

  /** 已关闭不良 -> 支持项（一般不良已关闭正是有条件放行的合法情形之一）。 */
  public static ReleaseBasisItemPayload closedDefect(DefectRecord defect) {
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_SUPPORT,
        "CLOSED_DEFECT",
        "defect already closed: defectRecordId=" + defect.id
            + ", severity=" + defect.severity.name()
            + ", dispositionStatus=" + defect.dispositionStatus.name(),
        "DEFECT",
        String.valueOf(defect.id),
        defect.severity.name(),
        null);
  }

  public static ReleaseBasisItemPayload noInspection() {
    return new ReleaseBasisItemPayload(
        ReleaseBasisItemPayload.KIND_BLOCKER,
        "NO_INSPECTION",
        ErrorMessages.NO_INSPECTION,
        "BATCH",
        null,
        null,
        null);
  }
}
