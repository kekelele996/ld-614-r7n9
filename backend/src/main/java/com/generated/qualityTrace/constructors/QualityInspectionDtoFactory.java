package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.utils.Formatters;

public final class QualityInspectionDtoFactory {
  private QualityInspectionDtoFactory() {}

  public static Map<String,Object> create(){ return Map.of("id",1,"name","质量检验"); }

  /** 检验响应对象 */
  public static Map<String, Object> view(QualityInspection inspection) {
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("id", inspection.id);
    view.put("batchId", inspection.batchId);
    view.put("inspectorId", inspection.inspectorId);
    view.put("inspectionType", inspection.inspectionType);
    view.put("standardVersion", inspection.standardVersion);
    view.put("resultStatus", inspection.resultStatus);
    view.put("resultText", Formatters.inspectionResultText(inspection.resultStatus));
    view.put("inspectedAt", inspection.inspectedAt);
    return view;
  }
}
