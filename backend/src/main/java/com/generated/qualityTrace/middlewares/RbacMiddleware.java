package com.generated.qualityTrace.middlewares;

import org.springframework.stereotype.Component;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.exceptions.ReleaseForbiddenException;

/**
 * RBAC 中间件/守卫：批次质量放行仅质量经理可执行。
 * service 层显式调用本守卫，保证跨层触达（route/controller/service 都经过角色约束）。
 */
@Component
public class RbacMiddleware {

  public void assertCanReleaseBatch(String role) {
    UserRole parsed = parseRole(role);
    if (parsed != UserRole.QUALITY_MANAGER) {
      throw new ReleaseForbiddenException(ErrorMessages.RBAC_DENIED + ": " + role);
    }
  }

  /** 审计员可查询追溯，其余已认证角色读放行记录。 */
  public void assertCanViewTrace(String role) {
    parseRole(role);
  }

  private UserRole parseRole(String role) {
    if (role == null || role.isBlank()) {
      throw new ReleaseForbiddenException(ErrorMessages.AUTH_REQUIRED);
    }
    try {
      return UserRole.valueOf(role.trim());
    } catch (IllegalArgumentException ex) {
      throw new ReleaseForbiddenException(ErrorMessages.RBAC_DENIED + ": " + role);
    }
  }
}
