package com.generated.qualityTrace.models;

import com.generated.qualityTrace.constants.InspectionResultStatus;

/**
 * 质量检验。同一批次以 inspectedAt 最大（最近一次）检验的 resultStatus 作为放行判定依据。
 */
public class QualityInspection {
  public Long id;
  public Long batchId;
  public String inspectorId;
  public String inspectionType;
  public String standardVersion;
  public InspectionResultStatus resultStatus;
  public String inspectedAt;

  public QualityInspection() {}

  public QualityInspection(Long id, Long batchId, String inspectorId, String inspectionType,
      String standardVersion, InspectionResultStatus resultStatus, String inspectedAt) {
    this.id = id;
    this.batchId = batchId;
    this.inspectorId = inspectorId;
    this.inspectionType = inspectionType;
    this.standardVersion = standardVersion;
    this.resultStatus = resultStatus;
    this.inspectedAt = inspectedAt;
  }
}
