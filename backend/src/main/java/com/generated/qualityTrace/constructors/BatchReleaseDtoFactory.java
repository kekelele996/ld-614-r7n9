package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.types.ReleaseBlocker;
import com.generated.qualityTrace.utils.Formatters;

public final class BatchReleaseDtoFactory {
  private BatchReleaseDtoFactory() {}

  /** 放行记录响应对象 */
  public static Map<String, Object> releaseView(BatchRelease record) {
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("id", record.id);
    view.put("batchId", record.batchId);
    view.put("batchNo", record.batchNo);
    view.put("decision", record.decision);
    view.put("decisionText", Formatters.releaseDecisionText(record.decision));
    view.put("restrictionNote", record.restrictionNote);
    view.put("releasedBy", record.releasedBy);
    view.put("releasedAt", record.releasedAt);
    view.put("basis", record.basis);
    return view;
  }

  /** 阻塞项响应对象 */
  public static Map<String, Object> blockerView(ReleaseBlocker blocker) {
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("code", blocker.code());
    view.put("message", blocker.message());
    return view;
  }

  /** 追溯树中的放行节点：放行结论、限制说明及对应依据 */
  public static Map<String, Object> traceReleaseView(BatchRelease record) {
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("decision", record.decision);
    view.put("decisionText", Formatters.releaseDecisionText(record.decision));
    view.put("restrictionNote", record.restrictionNote);
    view.put("releasedBy", record.releasedBy);
    view.put("releasedAt", record.releasedAt);
    view.put("basis", record.basis);
    return view;
  }
}
