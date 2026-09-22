package com.generated.qualityTrace.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.AuditLog;

/**
 * 审计/追溯事件日志数据访问层。失败路径也写事件（BATCH_RELEASE_BLOCKED），
 * 但不修改批次、不良、放行记录本身。
 */
@Repository
public class AuditLogRepository {
  private final Map<Long, AuditLog> store = new ConcurrentHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(0L);

  public AuditLog append(String actor, String action, String targetType, String targetId,
      String detail, String createdAt) {
    long id = idSequence.incrementAndGet();
    AuditLog logEntry = new AuditLog(id, actor, action, targetType, targetId, detail, createdAt);
    store.put(id, logEntry);
    return logEntry;
  }

  public List<AuditLog> findByTarget(String targetType, String targetId) {
    return store.values().stream()
        .filter(l -> Objects.equals(l.targetType, targetType) && Objects.equals(l.targetId, targetId))
        .sorted(Comparator.comparing(l -> l.id))
        .toList();
  }

  public List<AuditLog> findAll() {
    return store.values().stream().sorted(Comparator.comparing(l -> l.id)).toList();
  }
}
