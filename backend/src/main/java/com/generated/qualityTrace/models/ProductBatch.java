package com.generated.qualityTrace.models;

import com.generated.qualityTrace.constants.BatchStatus;

/**
 * 产品批次。放行成功后 batchStatus 更新为 RELEASED / CONDITIONAL_RELEASED；
 * 放行失败时本对象必须保持原样（不产生任何状态写入）。
 */
public class ProductBatch {
  public Long id;
  public String batchNo;
  public Long workOrderId;
  public Integer quantity;
  public String materialLotNo;
  public String producedAt;
  public BatchStatus batchStatus;

  public ProductBatch() {}

  public ProductBatch(Long id, String batchNo, Long workOrderId, Integer quantity,
      String materialLotNo, String producedAt, BatchStatus batchStatus) {
    this.id = id;
    this.batchNo = batchNo;
    this.workOrderId = workOrderId;
    this.quantity = quantity;
    this.materialLotNo = materialLotNo;
    this.producedAt = producedAt;
    this.batchStatus = batchStatus;
  }
}
