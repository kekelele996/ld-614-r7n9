package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.utils.Formatters;

public final class ProductBatchDtoFactory {
  private ProductBatchDtoFactory() {}

  public static Map<String,Object> create(){ return Map.of("id",1,"name","产品批次"); }

  /** 批次响应对象 */
  public static Map<String, Object> view(ProductBatch batch) {
    Map<String, Object> view = new LinkedHashMap<>();
    view.put("id", batch.id);
    view.put("batchNo", batch.batchNo);
    view.put("workOrderId", batch.workOrderId);
    view.put("quantity", batch.quantity);
    view.put("materialLotNo", batch.materialLotNo);
    view.put("producedAt", batch.producedAt);
    view.put("batchStatus", batch.batchStatus);
    view.put("batchStatusText", Formatters.batchStatusText(batch.batchStatus));
    return view;
  }
}
