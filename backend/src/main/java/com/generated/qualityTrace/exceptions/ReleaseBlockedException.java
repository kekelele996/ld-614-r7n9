package com.generated.qualityTrace.exceptions;

import java.util.List;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/**
 * 放行被拒绝：返回全部阻塞项（未关闭重大/严重不良、最近检验不合格/待复检等）。
 * 抛出时批次、不良、放行记录均保持原样。
 */
public class ReleaseBlockedException extends BatchReleaseException {
  private final transient List<ReleaseBasisItemPayload> blockers;

  public ReleaseBlockedException(String message, List<ReleaseBasisItemPayload> blockers) {
    super(com.generated.qualityTrace.constants.ErrorCodes.RELEASE_BLOCKED, message);
    this.blockers = List.copyOf(blockers);
  }

  @Override
  public List<ReleaseBasisItemPayload> getDetails() {
    return blockers;
  }
}
