package com.generated.qualityTrace.repositories;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;

@Repository
public class ProductBatchRepository {
  private final ConcurrentHashMap<Long, ProductBatch> byId = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Long> idByBatchNo = new ConcurrentHashMap<>();

  public ProductBatchRepository() {
    save(new ProductBatch(1L, "BATCH-OK-001", 1L, 500, "MAT-20260901-A", "2026-09-19T08:00:00", BatchStatus.IN_INSPECTION.name()));
    save(new ProductBatch(2L, "BATCH-COND-001", 1L, 300, "MAT-20260901-B", "2026-09-19T09:00:00", BatchStatus.IN_INSPECTION.name()));
    save(new ProductBatch(3L, "BATCH-BLOCK-001", 2L, 200, "MAT-20260902-A", "2026-09-20T08:00:00", BatchStatus.ON_HOLD.name()));
    save(new ProductBatch(4L, "BATCH-RECHECK-001", 2L, 150, "MAT-20260902-B", "2026-09-20T10:00:00", BatchStatus.IN_INSPECTION.name()));
    save(new ProductBatch(5L, "BATCH-CRIT-001", 3L, 100, "MAT-20260903-A", "2026-09-21T08:00:00", BatchStatus.ON_HOLD.name()));
  }

  private void save(ProductBatch batch) {
    byId.put(batch.id, batch);
    idByBatchNo.put(batch.batchNo, batch.id);
  }

  public ProductBatch findByBatchNo(String batchNo) {
    Long id = idByBatchNo.get(batchNo);
    return id == null ? null : byId.get(id);
  }

  public void updateStatus(Long id, String batchStatus) {
    ProductBatch batch = byId.get(id);
    if (batch != null) {
      batch.batchStatus = batchStatus;
    }
  }

  public List<Map<String,Object>> findAll(){ return List.of(Map.of("id",1,"name","产品批次","status","READY")); }
}
