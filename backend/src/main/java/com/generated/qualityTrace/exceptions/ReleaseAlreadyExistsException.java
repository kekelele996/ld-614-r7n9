package com.generated.qualityTrace.exceptions;

/** 批次已存在生效放行记录：重复或并发提交只能生效一次。 */
public class ReleaseAlreadyExistsException extends BatchReleaseException {
  public ReleaseAlreadyExistsException(String message) {
    super(com.generated.qualityTrace.constants.ErrorCodes.RELEASE_ALREADY_EXISTS, message);
  }
}
