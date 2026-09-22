package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.BatchRelease;

@Repository
public class BatchReleaseRepository {
  private final AtomicLong seq = new AtomicLong(0);
  private final ConcurrentHashMap<Long, BatchRelease> byBatchId = new ConcurrentHashMap<>();

  public BatchRelease findByBatchId(Long batchId) {
    return byBatchId.get(batchId);
  }

  public List<BatchRelease> findAll() {
    return List.copyOf(byBatchId.values());
  }

  /** 原子写入：同一批次仅允许一条放行记录，重复或并发提交只有第一个生效 */
  public BatchRelease saveIfAbsent(BatchRelease record) {
    record.id = seq.incrementAndGet();
    BatchRelease previous = byBatchId.putIfAbsent(record.batchId, record);
    return previous == null ? record : null;
  }

  /** 补偿删除：放行后续步骤失败时回滚，保证批次、不良、放行记录保持原样 */
  public void deleteByBatchId(Long batchId) {
    byBatchId.remove(batchId);
  }
}
