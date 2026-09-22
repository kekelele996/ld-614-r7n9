package com.generated.qualityTrace.repositories;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;

@Repository
public class DefectRecordRepository {
  private final Map<Long, DefectRecord> store = new ConcurrentHashMap<>();

  public DefectRecordRepository() {
    // 批次1：一般不良已关闭 -> 配合让步接收只能有条件放行
    seed(1L, 1L, "SCRATCH", 3, DefectSeverity.MINOR, "包装划伤",
        DefectDispositionStatus.CLOSED, "2026-09-10T11:00:00");
    // 批次2：重大不良未关闭（硬阻塞）
    seed(2L, 2L, "DIMENSION_OVER", 12, DefectSeverity.CRITICAL, "模具偏移",
        DefectDispositionStatus.OPEN, "2026-09-11T13:30:00");
    // 批次3：严重不良已关闭、无其他未关闭 -> 检验合格可正常放行
    seed(3L, 3L, "LABEL_MISS", 2, DefectSeverity.MAJOR, "标签漏贴",
        DefectDispositionStatus.CLOSED, "2026-09-12T11:00:00");
    // 批次4：一般不良未关闭（条件项，非硬阻塞）
    seed(4L, 4L, "BURR", 5, DefectSeverity.MINOR, "毛刺超标",
        DefectDispositionStatus.OPEN, "2026-09-12T16:00:00");
  }

  private void seed(Long id, Long batchId, String defectType, Integer defectQty,
      DefectSeverity severity, String rootCause, DefectDispositionStatus dispositionStatus,
      String createdAt) {
    store.put(id, new DefectRecord(id, batchId, defectType, defectQty, severity, rootCause,
        dispositionStatus, createdAt));
  }

  public List<DefectRecord> findAll() {
    return store.values().stream().sorted(Comparator.comparing(d -> d.id)).toList();
  }

  public List<DefectRecord> findByBatchId(Long batchId) {
    return store.values().stream()
        .filter(d -> Objects.equals(d.batchId, batchId))
        .sorted(Comparator.comparing(d -> d.id))
        .toList();
  }
}
