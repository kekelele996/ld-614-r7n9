package com.generated.qualityTrace.constructors;

import java.util.*;
import com.generated.qualityTrace.constants.ReleaseConclusion;
import com.generated.qualityTrace.models.BatchRelease;
import com.generated.qualityTrace.types.BatchReleaseResultPayload;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/**
 * 批次放行响应对象构造器：放行结论、限制说明、放行人和时间及判定依据统一在此组装，
 * service/controller 不得散写默认结构。
 */
public final class BatchReleaseDtoFactory {

  private BatchReleaseDtoFactory() {}

  public static Map<String, Object> create() {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("releaseId", null);
    dto.put("batchId", null);
    dto.put("batchNo", null);
    dto.put("conclusion", ReleaseConclusion.RELEASED.name());
    dto.put("conclusionText", conclusionText(ReleaseConclusion.RELEASED));
    dto.put("limitNote", null);
    dto.put("releasedBy", null);
    dto.put("releasedAt", null);
    dto.put("basis", List.of());
    return dto;
  }

  public static Map<String, Object> toDto(BatchRelease release,
      List<ReleaseBasisItemPayload> basis) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("releaseId", release.id);
    dto.put("batchId", release.batchId);
    dto.put("batchNo", release.batchNo);
    dto.put("conclusion", release.conclusion == null ? null : release.conclusion.name());
    dto.put("conclusionText", conclusionText(release.conclusion));
    dto.put("limitNote", release.limitNote);
    dto.put("releasedBy", release.releasedBy);
    dto.put("releasedAt", release.releasedAt);
    dto.put("basis", basis == null ? List.of() : basis);
    return dto;
  }

  public static BatchReleaseResultPayload toResult(boolean accepted, String batchStatus,
      BatchRelease release, List<ReleaseBasisItemPayload> basis) {
    return new BatchReleaseResultPayload(
        accepted,
        release == null ? null : release.id,
        release == null ? null : release.batchId,
        release == null ? null : release.batchNo,
        batchStatus,
        release == null || release.conclusion == null ? null : release.conclusion.name(),
        release == null ? null : release.limitNote,
        release == null ? null : release.releasedBy,
        release == null ? null : release.releasedAt,
        basis == null ? List.of() : basis);
  }

  public static String conclusionText(ReleaseConclusion conclusion) {
    if (conclusion == null) {
      return "";
    }
    return switch (conclusion) {
      case RELEASED -> "放行";
      case CONDITIONAL_RELEASED -> "有条件放行";
    };
  }
}
