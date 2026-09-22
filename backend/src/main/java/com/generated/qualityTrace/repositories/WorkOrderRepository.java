package com.generated.qualityTrace.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * 工单数据访问层。追溯树按 batch.workOrderId 关联工单概要信息。
 */
@Repository
public class WorkOrderRepository {
  private final Map<Long, Map<String, Object>> store = new ConcurrentHashMap<>();

  public WorkOrderRepository() {
    seed(10L, "WO-2026-0100", "P-AXLE", "驱动轴", 500, "LINE-1", "RUNNING");
    seed(11L, "WO-2026-0101", "P-BRACKET", "支架组件", 300, "LINE-2", "RUNNING");
    seed(12L, "WO-2026-0102", "P-HOUSING", "外壳", 800, "LINE-1", "PLANNED");
  }

  private void seed(Long id, String orderNo, String productCode, String productName,
      Integer plannedQty, String lineCode, String status) {
    Map<String, Object> row = new LinkedHashMap<>();
    row.put("id", id);
    row.put("orderNo", orderNo);
    row.put("productCode", productCode);
    row.put("productName", productName);
    row.put("plannedQty", plannedQty);
    row.put("lineCode", lineCode);
    row.put("status", status);
    store.put(id, row);
  }

  public List<Map<String, Object>> findAll() {
    return store.values().stream().sorted(Comparator.comparing(m -> (Long) m.get("id"))).toList();
  }

  public Optional<Map<String, Object>> findById(Long id) {
    return Optional.ofNullable(store.get(id));
  }
}
