package com.generated.qualityTrace.middlewares;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.generated.qualityTrace.models.AuditLog;
import com.generated.qualityTrace.repositories.AuditLogRepository;
import com.generated.qualityTrace.utils.Formatters;

/**
 * 操作日志 + 追溯事件日志中间件：所有放行写操作及追溯查询经此落审计记录。
 * 与 constants/LogTemplates 强耦合：新增动作时必须同步模板与调用处。
 */
@Component
public class AuditLogMiddleware {
  private static final Logger log = LoggerFactory.getLogger(AuditLogMiddleware.class);

  private final AuditLogRepository auditLogRepository;

  public AuditLogMiddleware(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public AuditLog record(String actor, String action, String targetType, Long targetId,
      String detail) {
    String targetIdText = targetId == null ? null : String.valueOf(targetId);
    String createdAt = Formatters.nowIso();
    AuditLog entry = auditLogRepository.append(actor, action, targetType, targetIdText,
        detail, createdAt);
    log.info("{} actor={} target={} detail={}", action, actor,
        Formatters.audit(targetType, targetId == null ? 0L : targetId), detail);
    return entry;
  }
}
