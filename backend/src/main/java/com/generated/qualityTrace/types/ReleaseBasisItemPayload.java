package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 放行判定依据项（追溯查询中“对应依据”的结构化条目）。
 *
 * @param kind        BLOCKER 阻塞项 / CONDITION 条件项 / SUPPORT 支持项
 * @param code        依据编码（OPEN_MAJOR_OR_CRITICAL_DEFECT 等）
 * @param message     人可读说明
 * @param refType     关联实体类型 DEFECT / INSPECTION / BATCH
 * @param refId       关联实体ID
 * @param severity    不良严重度（可空）
 * @param resultStatus 检验结论（可空）
 */
public record ReleaseBasisItemPayload(
    String kind,
    String code,
    String message,
    String refType,
    String refId,
    String severity,
    String resultStatus) {

  public static final String KIND_BLOCKER = "BLOCKER";
  public static final String KIND_CONDITION = "CONDITION";
  public static final String KIND_SUPPORT = "SUPPORT";

  public static List<String> allKinds() {
    return List.of(KIND_BLOCKER, KIND_CONDITION, KIND_SUPPORT);
  }
}
