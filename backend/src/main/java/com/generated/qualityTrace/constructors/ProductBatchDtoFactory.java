package com.generated.qualityTrace.constructors;

import java.util.*;
import com.generated.qualityTrace.constants.BatchStatus;
import com.generated.qualityTrace.models.ProductBatch;

/** 产品批次响应对象构造器。 */
public final class ProductBatchDtoFactory {

  private ProductBatchDtoFactory() {}

  public static Map<String, Object> create() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", null);
    dto.put("batchNo", null);
    dto.put("workOrderId", null);
    dto.put("quantity", null);
    dto.put("materialLotNo", null);
    dto.put("producedAt", null);
    dto.put("batchStatus", BatchStatus.PENDING.name());
    dto.put("batchStatusText", batchStatusText(BatchStatus.PENDING));
    return dto;
  }

  public static Map<String, Object> toDto(ProductBatch batch) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", batch.id);
    dto.put("batchNo", batch.batchNo);
    dto.put("workOrderId", batch.workOrderId);
    dto.put("quantity", batch.quantity);
    dto.put("materialLotNo", batch.materialLotNo);
    dto.put("producedAt", batch.producedAt);
    dto.put("batchStatus", batch.batchStatus == null ? null : batch.batchStatus.name());
    dto.put("batchStatusText", batchStatusText(batch.batchStatus));
    return dto;
  }

  public static String batchStatusText(BatchStatus status) {
    if (status == null) {
      return "";
    }
    return switch (status) {
      case PENDING -> "待检";
      case READY -> "待放行";
      case BLOCKED -> "阻塞";
      case RELEASED -> "已放行";
      case CONDITIONAL_RELEASED -> "有条件放行";
    };
  }
}
