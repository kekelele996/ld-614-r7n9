package com.generated.qualityTrace.utils;

import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.DispositionStatus;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.ReleaseDecision;

public final class Formatters {
  public static String audit(String type, long id){ return type + "#" + id; }

  public static String releaseDecisionText(String decision) {
    if (ReleaseDecision.RELEASED.name().equals(decision)) return "放行";
    if (ReleaseDecision.CONDITIONAL_RELEASED.name().equals(decision)) return "有条件放行";
    return "未放行";
  }

  public static String inspectionResultText(String status) {
    if (InspectionResultStatus.PASS.name().equals(status)) return "合格";
    if (InspectionResultStatus.FAIL.name().equals(status)) return "不合格";
    if (InspectionResultStatus.CONDITIONAL_PASS.name().equals(status)) return "让步接收";
    if (InspectionResultStatus.RECHECK.name().equals(status)) return "待复检";
    return "未知";
  }

  public static String severityText(String severity) {
    if (DefectSeverity.MINOR.name().equals(severity)) return "一般";
    if (DefectSeverity.MAJOR.name().equals(severity)) return "重大";
    if (DefectSeverity.CRITICAL.name().equals(severity)) return "严重";
    return "未知";
  }

  public static String dispositionStatusText(String status) {
    if (DispositionStatus.OPEN.name().equals(status)) return "待处置";
    if (DispositionStatus.DISPOSED.name().equals(status)) return "已处置";
    if (DispositionStatus.CLOSED.name().equals(status)) return "已关闭";
    return "未知";
  }

  public static String batchStatusText(String status) {
    if (BatchStatus.CREATED.name().equals(status)) return "已创建";
    if (BatchStatus.IN_INSPECTION.name().equals(status)) return "检验中";
    if (BatchStatus.ON_HOLD.name().equals(status)) return "暂停";
    if (BatchStatus.RELEASED.name().equals(status)) return "已放行";
    if (BatchStatus.CONDITIONAL_RELEASED.name().equals(status)) return "有条件放行";
    return "未知";
  }
}
