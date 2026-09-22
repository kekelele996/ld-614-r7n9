package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 放行结果响应。成功/重复/阻塞共用同一结构，由 accepted 区分。
 */
public record BatchReleaseResultPayload(
    boolean accepted,
    Long releaseId,
    Long batchId,
    String batchNo,
    String batchStatus,
    String conclusion,
    String limitNote,
    String releasedBy,
    String releasedAt,
    List<ReleaseBasisItemPayload> basis) {
}
