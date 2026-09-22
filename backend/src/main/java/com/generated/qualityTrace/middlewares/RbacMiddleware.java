package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.exceptions.BizException;

public class RbacMiddleware {
  /** 校验请求角色，不匹配时拒绝访问 */
  public static void requireRole(String actualRole, UserRole expected) {
    if (actualRole == null || actualRole.isBlank()) {
      throw new BizException(ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED, 401);
    }
    if (!expected.name().equals(actualRole.trim())) {
      throw new BizException(ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED, 403);
    }
  }
}
