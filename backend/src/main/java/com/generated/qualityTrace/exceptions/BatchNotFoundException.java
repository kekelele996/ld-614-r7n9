package com.generated.qualityTrace.exceptions;

/** 批次不存在。 */
public class BatchNotFoundException extends BatchReleaseException {
  public BatchNotFoundException(String message) {
    super(com.generated.qualityTrace.constants.ErrorCodes.BATCH_NOT_FOUND, message);
  }
}
