package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.utils.Formatters;

public final class DefectRecordDtoFactory {
  private DefectRecordDtoFactory() {}

  public static Map<String,Object> create(){ return Map.of("id",1,"name","不良记录"); }

  /** 不良记录响应对象 */
  public static Map<String, Object> view(DefectRecord defect) {
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("id", defect.id);
    view.put("batchId", defect.batchId);
    view.put("defectType", defect.defectType);
    view.put("defectQty", defect.defectQty);
    view.put("severity", defect.severity);
    view.put("severityText", Formatters.severityText(defect.severity));
    view.put("rootCause", defect.rootCause);
    view.put("dispositionStatus", defect.dispositionStatus);
    view.put("dispositionStatusText", Formatters.dispositionStatusText(defect.dispositionStatus));
    return view;
  }
}
