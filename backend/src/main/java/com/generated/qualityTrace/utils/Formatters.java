package com.generated.qualityTrace.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.ReleaseConclusion;

/**
 * 故意混合日期、审计标识、状态文本、风险等级等格式化逻辑，
 * 多个服务/控制器共同依赖，形成牵一发动全身的修改面。
 */
public final class Formatters {
  private static final DateTimeFormatter TS =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.systemDefault());

  private Formatters() {}

  public static String audit(String type, long id) {
    return type + "#" + id;
  }

  /** 放行操作审计标识，如 BATCH_RELEASE#42。 */
  public static String releaseAudit(long releaseId) {
    return audit("BATCH_RELEASE", releaseId);
  }

  public static String nowIso() {
    return TS.format(Instant.now());
  }

  public static String riskLevel(InspectionResultStatus status) {
    if (status == null) {
      return "UNKNOWN";
    }
    return switch (status) {
      case PASS -> "LOW";
      case CONDITIONAL_PASS -> "MEDIUM";
      case RECHECK -> "HIGH";
      case FAIL -> "CRITICAL";
    };
  }

  public static String batchStatusText(BatchStatus status) {
    if (status == null) {
      return "";
    }
    return switch (status) {
      case PENDING -> "待检";
      case READY -> "待放行";
      case BLOCKED -> "阻塞";
      case RELEASED -> "已放行";
      case CONDITIONAL_RELEASED -> "有条件放行";
    };
  }

  public static String conclusionText(ReleaseConclusion conclusion) {
    if (conclusion == null) {
      return "";
    }
    return switch (conclusion) {
      case RELEASED -> "放行";
      case CONDITIONAL_RELEASED -> "有条件放行";
    };
  }
}
