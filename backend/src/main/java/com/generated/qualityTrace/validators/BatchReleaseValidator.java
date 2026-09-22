package com.generated.qualityTrace.validators;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.ReleaseConclusion;
import com.generated.qualityTrace.exceptions.BatchReleaseException;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;
import com.generated.qualityTrace.types.ReleaseDecisionPayload;

/**
 * 批次放行入参及结论校验器（独立文件，禁止并入 service）。
 * 仅做与事实无关的表单校验；阻塞/条件事实判定在 BatchReleaseService。
 */
@Component
public class BatchReleaseValidator {

  /** 解析并校验入参，非法时抛 BatchReleaseException（不触碰任何业务数据）。 */
  public ValidatedDecision validate(ReleaseDecisionPayload payload) {
    if (payload == null) {
      throw new BatchReleaseException(ErrorCodes.RELEASE_PAYLOAD_INVALID,
          ErrorMessages.RELEASE_PAYLOAD_INVALID);
    }

    boolean hasBatchId = payload.batchId() != null;
    boolean hasBatchNo = payload.batchNo() != null && !payload.batchNo().isBlank();
    if (!hasBatchId && !hasBatchNo) {
      throw new BatchReleaseException(ErrorCodes.RELEASE_PAYLOAD_INVALID,
          ErrorMessages.RELEASE_PAYLOAD_INVALID + ": batchId or batchNo is required");
    }
    if (payload.managerId() == null || payload.managerId().isBlank()) {
      throw new BatchReleaseException(ErrorCodes.RELEASE_PAYLOAD_INVALID,
          ErrorMessages.RELEASE_PAYLOAD_INVALID + ": managerId is required");
    }

    ReleaseConclusion conclusion;
    try {
      conclusion = ReleaseConclusion.valueOf(payload.conclusion() == null
          ? "" : payload.conclusion().trim());
    } catch (IllegalArgumentException ex) {
      throw new BatchReleaseException(ErrorCodes.RELEASE_INVALID_CONCLUSION,
          String.format(ErrorMessages.RELEASE_INVALID_CONCLUSION, payload.conclusion()));
    }

    String limitNote = payload.limitNote() == null ? "" : payload.limitNote().trim();

    return new ValidatedDecision(payload.batchId(),
        hasBatchNo ? payload.batchNo().trim() : null, conclusion, limitNote,
        payload.managerId().trim(), payload.managerRole());
  }

  /**
   * 依据收集到的事实（阻塞项 blockers、条件项 conditions）校验结论与限制说明。
   * 有阻塞项时由 service 直接拒绝；本方法处理“只允许有条件放行/限制说明必填”等规则。
   */
  public void validateConclusionAgainstFacts(Long batchId, ReleaseConclusion conclusion,
      String limitNote, List<ReleaseBasisItemPayload> blockers,
      List<ReleaseBasisItemPayload> conditions) {
    List<ReleaseBasisItemPayload> conditionsSafe =
        conditions == null ? List.of() : new ArrayList<>(conditions);

    if (conclusion == ReleaseConclusion.CONDITIONAL_RELEASED) {
      if (limitNote.isEmpty()) {
        throw new BatchReleaseException(ErrorCodes.RELEASE_LIMIT_NOTE_REQUIRED,
            String.format(ErrorMessages.RELEASE_LIMIT_NOTE_REQUIRED, batchId));
      }
      // 没有任何让步条件却提交有条件放行：事实不支持
      if (conditionsSafe.isEmpty()) {
        throw new BatchReleaseException(ErrorCodes.RELEASE_FULL_NOT_ALLOWED,
            String.format(ErrorMessages.RELEASE_CONDITIONAL_REQUIRED, batchId,
                "PASS") + " (no conditional basis found)");
      }
    } else if (conclusion == ReleaseConclusion.RELEASED) {
      if (!limitNote.isEmpty()) {
        throw new BatchReleaseException(ErrorCodes.RELEASE_LIMIT_NOTE_FORBIDDEN,
            String.format(ErrorMessages.RELEASE_LIMIT_NOTE_FORBIDDEN, batchId));
      }
      if (!conditionsSafe.isEmpty()) {
        throw new BatchReleaseException(ErrorCodes.RELEASE_FULL_NOT_ALLOWED,
            String.format(ErrorMessages.RELEASE_FULL_NOT_ALLOWED, batchId));
      }
    }
  }

  /** 校验后的放行决策（值对象）。 */
  public record ValidatedDecision(
      Long batchId,
      String batchNo,
      ReleaseConclusion conclusion,
      String limitNote,
      String managerId,
      String managerRole) {
  }
}
