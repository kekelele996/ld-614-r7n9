package com.generated.qualityTrace.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;

/**
 * 批次数据访问层（本地内存存储，种子数据见 database/init.sql 的同步演示数据）。
 */
@Repository
public class ProductBatchRepository {
  private final Map<Long, ProductBatch> store = new ConcurrentHashMap<>();

  public ProductBatchRepository() {
    seed(1L, "B-2026-0001", 10L, 120, "MAT-A7", "2026-09-10T08:30:00", BatchStatus.READY);
    seed(2L, "B-2026-0002", 10L, 200, "MAT-A8", "2026-09-11T09:00:00", BatchStatus.BLOCKED);
    seed(3L, "B-2026-0003", 11L, 80, "MAT-B2", "2026-09-12T10:15:00", BatchStatus.READY);
    seed(4L, "B-2026-0004", 11L, 60, "MAT-B3", "2026-09-12T14:20:00", BatchStatus.READY);
    seed(5L, "B-2026-0005", 12L, 300, "MAT-C1", "2026-09-13T07:45:00", BatchStatus.PENDING);
  }

  private void seed(Long id, String batchNo, Long workOrderId, Integer quantity,
      String materialLotNo, String producedAt, BatchStatus status) {
    store.put(id, new ProductBatch(id, batchNo, workOrderId, quantity, materialLotNo,
        producedAt, status));
  }

  public List<ProductBatch> findAll() {
    return store.values().stream().sorted(Comparator.comparing(b -> b.id)).toList();
  }

  public Optional<ProductBatch> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }

  public Optional<ProductBatch> findByBatchNo(String batchNo) {
    return store.values().stream().filter(b -> Objects.equals(b.batchNo, batchNo)).findFirst();
  }

  public void updateStatus(Long id, BatchStatus status) {
    ProductBatch batch = store.get(id);
    if (batch != null) {
      batch.batchStatus = status;
    }
  }
}
