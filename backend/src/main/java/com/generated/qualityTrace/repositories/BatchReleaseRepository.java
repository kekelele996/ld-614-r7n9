package com.generated.qualityTrace.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.BatchRelease;

/**
 * 批次放行记录数据访问层。
 * 每个 batchId 至多一条记录：insertIfAbsent 原子地保证重复/并发提交只能生效一次。
 */
@Repository
public class BatchReleaseRepository {
  private final Map<Long, BatchRelease> byBatchId = new ConcurrentHashMap<>();
  private final Map<Long, BatchRelease> byId = new ConcurrentHashMap<>();
  private final AtomicLong idSequence = new AtomicLong(0L);

  public Optional<BatchRelease> findByBatchId(Long batchId) {
    return Optional.ofNullable(byBatchId.get(batchId));
  }

  public Optional<BatchRelease> findById(Long id) {
    return Optional.ofNullable(byId.get(id));
  }

  public List<BatchRelease> findAll() {
    return byId.values().stream().sorted(Comparator.comparing(r -> r.id)).toList();
  }

  public long nextId() {
    return idSequence.incrementAndGet();
  }

  /**
   * 原子插入：同一批次只有第一次插入成功（返回 true）；
   * 重复或并发提交拿到 false，且本方法不会修改既有任何记录。
   */
  public boolean insertIfAbsent(BatchRelease release) {
    AtomicBoolean won = new AtomicBoolean(false);
    byBatchId.computeIfAbsent(release.batchId, key -> {
      won.set(true);
      byId.put(release.id, release);
      return release;
    });
    return won.get();
  }
}
