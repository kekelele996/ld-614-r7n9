package com.generated.qualityTrace.exceptions;

import java.util.List;
import com.generated.qualityTrace.types.ReleaseBlocker;

/** 放行被拒绝：携带全部阻塞项 */
public class ReleaseBlockedException extends RuntimeException {
  private final transient List<ReleaseBlocker> blockers;

  public ReleaseBlockedException(String message, List<ReleaseBlocker> blockers) {
    super(message);
    this.blockers = blockers;
  }

  public List<ReleaseBlocker> getBlockers() { return blockers; }
}
