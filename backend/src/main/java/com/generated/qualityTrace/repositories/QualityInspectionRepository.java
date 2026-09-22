package com.generated.qualityTrace.repositories;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.QualityInspection;

@Repository
public class QualityInspectionRepository {
  private final List<QualityInspection> store = new CopyOnWriteArrayList<>();

  public QualityInspectionRepository() {
    store.add(new QualityInspection(1L, 1L, "INSP-01", "FINAL", "STD-V2", "PASS", "2026-09-20T10:00:00"));
    store.add(new QualityInspection(2L, 2L, "INSP-02", "FINAL", "STD-V2", "CONDITIONAL_PASS", "2026-09-20T11:00:00"));
    store.add(new QualityInspection(3L, 3L, "INSP-03", "FIRST", "STD-V2", "PASS", "2026-09-20T15:00:00"));
    store.add(new QualityInspection(4L, 3L, "INSP-03", "FINAL", "STD-V2", "FAIL", "2026-09-21T09:00:00"));
    store.add(new QualityInspection(5L, 4L, "INSP-04", "FINAL", "STD-V2", "RECHECK", "2026-09-21T10:00:00"));
    store.add(new QualityInspection(6L, 5L, "INSP-05", "FINAL", "STD-V2", "PASS", "2026-09-21T11:00:00"));
  }

  public List<QualityInspection> findByBatchId(Long batchId) {
    List<QualityInspection> result = new ArrayList<>();
    for (QualityInspection inspection : store) {
      if (inspection.batchId.equals(batchId)) {
        result.add(inspection);
      }
    }
    return result;
  }

  /** 最近一次检验：按检验时间倒序，时间相同取 id 较大者 */
  public QualityInspection findLatestByBatchId(Long batchId) {
    return findByBatchId(batchId).stream()
        .max(Comparator.comparing((QualityInspection i) -> i.inspectedAt).thenComparing(i -> i.id))
        .orElse(null);
  }

  public List<Map<String,Object>> findAll(){ return List.of(Map.of("id",1,"name","质量检验","status","READY")); }
}
