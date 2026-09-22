package com.generated.qualityTrace.constructors;

import java.util.*;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.models.QualityInspection;

/** 质量检验响应对象构造器。 */
public final class QualityInspectionDtoFactory {

  private QualityInspectionDtoFactory() {}

  public static Map<String, Object> create() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", null);
    dto.put("batchId", null);
    dto.put("inspectorId", null);
    dto.put("inspectionType", null);
    dto.put("standardVersion", null);
    dto.put("resultStatus", InspectionResultStatus.PASS.name());
    dto.put("resultStatusText", resultStatusText(InspectionResultStatus.PASS));
    dto.put("inspectedAt", null);
    return dto;
  }

  public static Map<String, Object> toDto(QualityInspection inspection) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", inspection.id);
    dto.put("batchId", inspection.batchId);
    dto.put("inspectorId", inspection.inspectorId);
    dto.put("inspectionType", inspection.inspectionType);
    dto.put("standardVersion", inspection.standardVersion);
    dto.put("resultStatus",
        inspection.resultStatus == null ? null : inspection.resultStatus.name());
    dto.put("resultStatusText", resultStatusText(inspection.resultStatus));
    dto.put("inspectedAt", inspection.inspectedAt);
    return dto;
  }

  public static String resultStatusText(InspectionResultStatus status) {
    if (status == null) {
      return "";
    }
    return switch (status) {
      case PASS -> "合格";
      case FAIL -> "不合格";
      case CONDITIONAL_PASS -> "让步接收";
      case RECHECK -> "待复检";
    };
  }
}
