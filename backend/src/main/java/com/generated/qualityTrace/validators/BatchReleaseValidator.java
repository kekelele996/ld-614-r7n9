package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.ReleaseDecision;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.types.BatchReleasePayload;

public final class BatchReleaseValidator {
  private BatchReleaseValidator() {}

  /**
   * 校验放行请求：结论必须合法；仅允许有条件放行时不得提交放行；
   * 有条件放行必须填写限制说明。
   */
  public static ReleaseDecision validate(BatchReleasePayload payload, boolean conditionalOnly) {
    if (payload == null || payload.decision() == null || payload.decision().isBlank()) {
      throw new BizException(ErrorCodes.RELEASE_DECISION_INVALID, ErrorMessages.RELEASE_DECISION_INVALID, 400);
    }
    final ReleaseDecision decision;
    try {
      decision = ReleaseDecision.valueOf(payload.decision().trim());
    } catch (IllegalArgumentException e) {
      throw new BizException(ErrorCodes.RELEASE_DECISION_INVALID, ErrorMessages.RELEASE_DECISION_INVALID, 400);
    }
    if (conditionalOnly && decision == ReleaseDecision.RELEASED) {
      throw new BizException(ErrorCodes.RELEASE_DECISION_INVALID, ErrorMessages.RELEASE_DECISION_CONDITIONAL_ONLY, 422);
    }
    if (decision == ReleaseDecision.CONDITIONAL_RELEASED
        && (payload.restrictionNote() == null || payload.restrictionNote().isBlank())) {
      throw new BizException(ErrorCodes.RELEASE_NOTE_REQUIRED, ErrorMessages.RELEASE_NOTE_REQUIRED, 400);
    }
    return decision;
  }
}
