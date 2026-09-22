package com.generated.qualityTrace.constructors;

import java.util.*;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;

/** 不良记录响应对象构造器：service/controller 不得散写默认结构。 */
public final class DefectRecordDtoFactory {

  private DefectRecordDtoFactory() {}

  public static Map<String, Object> create() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", null);
    dto.put("batchId", null);
    dto.put("defectType", null);
    dto.put("defectQty", null);
    dto.put("severity", null);
    dto.put("severityText", "");
    dto.put("rootCause", null);
    dto.put("dispositionStatus", DefectDispositionStatus.OPEN.name());
    dto.put("createdAt", null);
    return dto;
  }

  public static Map<String, Object> toDto(DefectRecord defect) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", defect.id);
    dto.put("batchId", defect.batchId);
    dto.put("defectType", defect.defectType);
    dto.put("defectQty", defect.defectQty);
    dto.put("severity", defect.severity == null ? null : defect.severity.name());
    dto.put("severityText", severityText(defect.severity));
    dto.put("rootCause", defect.rootCause);
    dto.put("dispositionStatus",
        defect.dispositionStatus == null ? null : defect.dispositionStatus.name());
    dto.put("createdAt", defect.createdAt);
    return dto;
  }

  public static String severityText(DefectSeverity severity) {
    if (severity == null) {
      return "";
    }
    return switch (severity) {
      case MINOR -> "一般不良";
      case MAJOR -> "严重不良";
      case CRITICAL -> "重大不良";
    };
  }
}
