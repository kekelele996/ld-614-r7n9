package com.generated.qualityTrace.constants;

/**
 * 操作/追溯事件日志模板集中存放，所有写操作必须记录日志。
 * 字段变更时必须同步修改模板与所有调用处（services / middlewares）。
 */
public final class LogTemplates {
  // 通用模板（既有）
  public static final String CREATE = "create";
  public static final String UPDATE = "update";
  public static final String STATUS = "status";
  public static final String EXPORT = "export";

  // ---- 批次放行：每个写/查询动作至少 4 条模板（与 ld-614 要求一致） ----
  public static final String BATCH_RELEASE_SUBMIT =
      "BATCH_RELEASE_SUBMIT actor={0} role={1} batchId={2} batchNo={3} conclusion={4}";
  public static final String BATCH_RELEASE_SUCCESS =
      "BATCH_RELEASE_SUCCESS batchId={0} batchNo={1} releaseId={2} conclusion={3} releasedBy={4} releasedAt={5}";
  public static final String BATCH_RELEASE_REJECTED =
      "BATCH_RELEASE_REJECTED batchId={0} batchNo={1} actor={2} conclusion={3} blockers={4}";
  public static final String BATCH_RELEASE_DUPLICATE =
      "BATCH_RELEASE_DUPLICATE batchId={0} batchNo={1} actor={2} existingReleaseId={3} existingReleasedAt={4}";
  public static final String BATCH_RELEASE_VALIDATION_FAILED =
      "BATCH_RELEASE_VALIDATION_FAILED batchId={0} actor={1} code={2} detail={3}";
  public static final String BATCH_TRACE_QUERY =
      "BATCH_TRACE_QUERY batchNo={0} actor={1} released={2}";

  // ---- 追溯/审计动作标识（audit_log.action） ----
  public static final String ACTION_BATCH_RELEASE = "BATCH_RELEASE";
  public static final String ACTION_BATCH_RELEASE_BLOCKED = "BATCH_RELEASE_BLOCKED";
  public static final String ACTION_BATCH_TRACE = "BATCH_TRACE_QUERY";
}
