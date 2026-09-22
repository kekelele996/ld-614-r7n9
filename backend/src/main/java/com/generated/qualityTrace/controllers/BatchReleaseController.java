package com.generated.qualityTrace.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.exceptions.BatchReleaseException;
import com.generated.qualityTrace.exceptions.ReleaseAlreadyExistsException;
import com.generated.qualityTrace.exceptions.ReleaseBlockedException;
import com.generated.qualityTrace.exceptions.ReleaseForbiddenException;
import com.generated.qualityTrace.middlewares.RateLimitMiddleware;
import com.generated.qualityTrace.types.BatchReleaseResultPayload;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;
import com.generated.qualityTrace.types.ReleaseDecisionPayload;
import com.generated.qualityTrace.routes.BatchReleaseRoutes;
import com.generated.qualityTrace.services.BatchReleaseService;

/**
 * 批次质量放行控制器。controller 层负责 HTTP 语义包装（状态码、阻塞项回传），
 * 业务判定在 service；异常不允许只在全局处理器中被吞掉。
 */
@RestController
@RequestMapping(BatchReleaseRoutes.PATH)
public class BatchReleaseController {

  private final BatchReleaseService service;
  private final RateLimitMiddleware rateLimitMiddleware;
  private final com.generated.qualityTrace.repositories.ProductBatchRepository batchRepository;

  public BatchReleaseController(BatchReleaseService service,
      RateLimitMiddleware rateLimitMiddleware,
      com.generated.qualityTrace.repositories.ProductBatchRepository batchRepository) {
    this.service = service;
    this.rateLimitMiddleware = rateLimitMiddleware;
    this.batchRepository = batchRepository;
  }

  /** 质量经理提交批次放行结论。 */
  @PostMapping
  public ResponseEntity<?> submit(@RequestBody(required = false) ReleaseDecisionPayload payload,
      @RequestHeader(value = "X-Actor-Role", required = false) String headerRole) {
    rateLimitMiddleware.check("batch-release");

    ReleaseDecisionPayload effective = withHeaderRole(payload, headerRole);
    try {
      BatchReleaseResultPayload result = service.submit(effective);
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
    } catch (ReleaseBlockedException ex) {
      // 拒绝：返回全部阻塞项
      return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(ex,
          ex.getCode(), ex.getMessage(), ex.getDetails()));
    } catch (ReleaseAlreadyExistsException ex) {
      // 重复/并发提交：只有第一次生效
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(errorBody(ex, ex.getCode(), ex.getMessage(), ex.getDetails()));
    } catch (ReleaseForbiddenException ex) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(errorBody(ex, ex.getCode(), ex.getMessage(), ex.getDetails()));
    } catch (BatchReleaseException ex) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(errorBody(ex, ex.getCode(), ex.getMessage(), ex.getDetails()));
    }
  }

  /** 查询单批次生效放行记录（追溯辅助接口）。 */
  @GetMapping("/by-batch/{batchId}")
  public ResponseEntity<?> getByBatch(@PathVariable Long batchId) {
    return service.findByBatchId(batchId)
        .<ResponseEntity<?>>map(release -> {
          java.util.List<ReleaseBasisItemPayload> basis =
              com.generated.qualityTrace.constructors.ReleaseBasisParser.parse(release.basis);
          String batchStatus = batchRepository.findById(batchId)
              .map(b -> b.batchStatus == null ? null : b.batchStatus.name()).orElse(null);
          return ResponseEntity.ok(com.generated.qualityTrace.constructors.BatchReleaseDtoFactory
              .toResult(true, batchStatus, release, basis));
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(java.util.Map.of("code", ErrorCodes.RELEASE_NOT_FOUND,
                "message", String.format(ErrorMessages.RELEASE_NOT_FOUND, batchId))));
  }

  private ReleaseDecisionPayload withHeaderRole(ReleaseDecisionPayload payload, String headerRole) {
    if (payload == null) {
      return null;
    }
    if ((payload.managerRole() == null || payload.managerRole().isBlank()) && headerRole != null) {
      return new ReleaseDecisionPayload(payload.batchId(), payload.batchNo(),
          payload.conclusion(), payload.limitNote(), payload.managerId(), headerRole);
    }
    return payload;
  }

  private java.util.Map<String, Object> errorBody(BatchReleaseException ex, String code,
      String message, java.util.List<ReleaseBasisItemPayload> details) {
    java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
    body.put("code", code);
    body.put("message", message);
    body.put("logTemplate", LogTemplates.BATCH_RELEASE_REJECTED);
    body.put("blockers", details);
    return body;
  }
}
