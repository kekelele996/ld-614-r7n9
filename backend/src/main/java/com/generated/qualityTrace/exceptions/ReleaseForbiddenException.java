package com.generated.qualityTrace.exceptions;

/** RBAC：非质量经理提交放行。 */
public class ReleaseForbiddenException extends BatchReleaseException {
  public ReleaseForbiddenException(String message) {
    super(com.generated.qualityTrace.constants.ErrorCodes.RBAC_DENIED, message);
  }
}
