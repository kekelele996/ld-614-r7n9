package com.generated.qualityTrace.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.DefectRecord;

@Repository
public class DefectRecordRepository {
  private final List<DefectRecord> store = new CopyOnWriteArrayList<>();

  public DefectRecordRepository() {
    store.add(new DefectRecord(10L, 2L, "外观划痕", 3, "MINOR", "模具磨损", "CLOSED"));
    store.add(new DefectRecord(11L, 3L, "尺寸超差", 5, "MAJOR", "夹具松动", "OPEN"));
    store.add(new DefectRecord(12L, 5L, "材质混料", 2, "CRITICAL", "来料异常", "OPEN"));
  }

  public List<DefectRecord> findByBatchId(Long batchId) {
    List<DefectRecord> result = new ArrayList<>();
    for (DefectRecord defect : store) {
      if (defect.batchId.equals(batchId)) {
        result.add(defect);
      }
    }
    return result;
  }

  public List<Map<String,Object>> findAll(){ return List.of(Map.of("id",1,"name","不良记录","status","READY")); }
}
