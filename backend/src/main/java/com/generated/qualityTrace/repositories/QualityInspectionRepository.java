package com.generated.qualityTrace.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.models.QualityInspection;

@Repository
public class QualityInspectionRepository {
  private final Map<Long, QualityInspection> store = new ConcurrentHashMap<>();

  public QualityInspectionRepository() {
    // 批次1：最近检验让步接收
    seed(1L, 1L, "U-1001", "FIRST", "STD-v3.2", InspectionResultStatus.PASS,
        "2026-09-10T10:00:00");
    seed(2L, 1L, "U-1001", "FINAL", "STD-v3.2",
        InspectionResultStatus.CONDITIONAL_PASS, "2026-09-10T15:00:00");
    // 批次2：最近检验不合格（硬阻塞）
    seed(3L, 2L, "U-1002", "FINAL", "STD-v3.2", InspectionResultStatus.FAIL,
        "2026-09-11T16:30:00");
    // 批次3：最近检验合格
    seed(4L, 3L, "U-1003", "FINAL", "STD-v3.2", InspectionResultStatus.PASS,
        "2026-09-12T15:00:00");
    // 批次4：最近检验待复检（硬阻塞）
    seed(5L, 4L, "U-1004", "PATROL", "STD-v3.2", InspectionResultStatus.RECHECK,
        "2026-09-12T17:00:00");
    // 批次5：最近检验让步接收（无不良，仍只能有条件放行）
    seed(6L, 5L, "U-1005", "FINAL", "STD-v3.3",
        InspectionResultStatus.CONDITIONAL_PASS, "2026-09-13T09:30:00");
  }

  private void seed(Long id, Long batchId, String inspectorId, String inspectionType,
      String standardVersion, InspectionResultStatus resultStatus, String inspectedAt) {
    store.put(id, new QualityInspection(id, batchId, inspectorId, inspectionType,
        standardVersion, resultStatus, inspectedAt));
  }

  public List<QualityInspection> findAll() {
    return store.values().stream().sorted(Comparator.comparing(i -> i.id)).toList();
  }

  public List<QualityInspection> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(i -> Objects.equals(i.batchId, batchId))
        .sorted(Comparator.comparing(i -> i.inspectedAt))
        .toList();
  }

  /** 最近一次检验：inspectedAt 最大者。 */
  public Optional<QualityInspection> findLatestByBatchId(Long batchId) {
    return store.values().stream()
        .filter(i -> Objects.equals(i.batchId, batchId))
        .max(Comparator.comparing(i -> i.inspectedAt));
  }
}
