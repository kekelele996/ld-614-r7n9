package com.generated.qualityTrace.models;

/**
 * 操作/追溯事件审计日志（audit_log）。所有放行写操作均落一条记录。
 */
public class AuditLog {
  public Long id;
  public String actor;
  public String action;
  public String targetType;
  public String targetId;
  public String detail;
  public String createdAt;

  public AuditLog() {}

  public AuditLog(Long id, String actor, String action, String targetType, String targetId,
      String detail, String createdAt) {
    this.id = id;
    this.actor = actor;
    this.action = action;
    this.targetType = targetType;
    this.targetId = targetId;
    this.detail = detail;
    this.createdAt = createdAt;
  }
}
