package com.generated.qualityTrace.models;

import java.util.List;

public class BatchRelease {
  public Long id;
  public Long batchId;
  public String batchNo;
  public String decision;
  public String restrictionNote;
  public String releasedBy;
  public String releasedAt;
  public List<String> basis;

  public BatchRelease() {}

  public BatchRelease(Long id, Long batchId, String batchNo, String decision, String restrictionNote, String releasedBy, String releasedAt, List<String> basis) {
    this.id = id; this.batchId = batchId; this.batchNo = batchNo; this.decision = decision;
    this.restrictionNote = restrictionNote; this.releasedBy = releasedBy; this.releasedAt = releasedAt; this.basis = basis;
  }
}
