package com.generated.qualityTrace.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.generated.qualityTrace.constants.UserRole;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.BatchReleaseRoutes;
import com.generated.qualityTrace.services.BatchReleaseService;
import com.generated.qualityTrace.types.BatchReleasePayload;

@RestController
@RequestMapping(BatchReleaseRoutes.PATH)
public class BatchReleaseController {
  private final BatchReleaseService service;

  public BatchReleaseController(BatchReleaseService service) {
    this.service = service;
  }

  /** 质量经理按批次提交放行结论 */
  @PostMapping(BatchReleaseRoutes.RELEASE)
  public Map<String, Object> release(@PathVariable String batchNo,
                                     @RequestBody(required = false) BatchReleasePayload payload,
                                     @RequestHeader(name = "X-Role", required = false) String role,
                                     @RequestHeader(name = "X-User", required = false) String operator) {
    RbacMiddleware.requireRole(role, UserRole.QUALITY_MANAGER);
    String releasedBy = (operator == null || operator.isBlank()) ? "unknown" : operator;
    return service.release(batchNo, payload, releasedBy);
  }

  /** 查询批次当前放行记录 */
  @GetMapping(BatchReleaseRoutes.RELEASE)
  public Map<String, Object> getRelease(@PathVariable String batchNo) {
    return service.getByBatchNo(batchNo);
  }
}
