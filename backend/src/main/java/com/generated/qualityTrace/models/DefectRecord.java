package com.generated.qualityTrace.models;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;

/**
 * 不良记录。severity=CRITICAL/MAJOR 且 dispositionStatus=OPEN 时构成放行硬阻塞；
 * MINOR 未关闭 + 最近检验让步接收时，仅允许有条件放行。
 */
public class DefectRecord {
  public Long id;
  public Long batchId;
  public String defectType;
  public Integer defectQty;
  public DefectSeverity severity;
  public String rootCause;
  public DefectDispositionStatus dispositionStatus;
  public String createdAt;

  public DefectRecord() {}

  public DefectRecord(Long id, Long batchId, String defectType, Integer defectQty,
      DefectSeverity severity, String rootCause, DefectDispositionStatus dispositionStatus,
      String createdAt) {
    this.id = id;
    this.batchId = batchId;
    this.defectType = defectType;
    this.defectQty = defectQty;
    this.severity = severity;
    this.rootCause = rootCause;
    this.dispositionStatus = dispositionStatus;
    this.createdAt = createdAt;
  }
}
