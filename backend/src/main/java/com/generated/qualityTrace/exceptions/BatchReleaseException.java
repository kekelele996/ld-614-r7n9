package com.generated.qualityTrace.exceptions;

import java.util.Collections;
import java.util.List;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/** 批次放行领域异常基类：携带错误码，供 controller/全局异常处理分别包装。 */
public class BatchReleaseException extends RuntimeException {
  private final String code;

  public BatchReleaseException(String code, String message) {
    super(message);
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  /** 异常附带的结构化明细（如全部阻塞项），默认空。 */
  public List<ReleaseBasisItemPayload> getDetails() {
    return Collections.emptyList();
  }
}
