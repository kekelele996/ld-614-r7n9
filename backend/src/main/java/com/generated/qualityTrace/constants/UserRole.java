package com.generated.qualityTrace.constants;

/**
 * RBAC 角色。批次放行仅允许质量经理（QUALITY_MANAGER）提交。
 * 出现位置：middlewares/RbacMiddleware、validators/BatchReleaseValidator、
 * services/BatchReleaseService、constants/ErrorMessages、constants/LogTemplates。
 */
public enum UserRole {
  QUALITY_INSPECTOR,
  LINE_SUPERVISOR,
  QUALITY_MANAGER,
  AUDITOR
}
