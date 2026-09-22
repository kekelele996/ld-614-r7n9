package com.generated.qualityTrace.models;

import com.generated.qualityTrace.constants.ReleaseConclusion;

/**
 * 批次质量放行记录。
 * 每个 batchId 至多一条有效记录（重复/并发提交只能生效一次）。
 * basis 保存放行判定依据（阻塞项/条件项快照），供追溯查询展示“对应依据”。
 */
public class BatchRelease {
  public Long id;
  public Long batchId;
  public String batchNo;
  public ReleaseConclusion conclusion;
  public String limitNote;
  public String releasedBy;
  public String releasedAt;
  /** 放行依据快照：JSON 文本，来源于 ReleaseBasis。 */
  public String basis;

  public BatchRelease() {}

  public BatchRelease(Long id, Long batchId, String batchNo, ReleaseConclusion conclusion,
      String limitNote, String releasedBy, String releasedAt, String basis) {
    this.id = id;
    this.batchId = batchId;
    this.batchNo = batchNo;
    this.conclusion = conclusion;
    this.limitNote = limitNote;
    this.releasedBy = releasedBy;
    this.releasedAt = releasedAt;
    this.basis = basis;
  }
}
