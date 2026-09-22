package com.generated.qualityTrace.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.generated.qualityTrace.middlewares.RateLimitMiddleware;
import com.generated.qualityTrace.routes.TraceRoutes;
import com.generated.qualityTrace.services.QualityTraceQueryService;
import com.generated.qualityTrace.types.BatchTracePayload;

/**
 * 批次全链路追溯查询：GET /api/trace/{batchNo}。
 * 追溯视图展示放行结论、限制说明、放行人和时间及对应依据。
 */
@RestController
@RequestMapping(TraceRoutes.PATH)
public class QualityTraceController {

  private final QualityTraceQueryService service;
  private final RateLimitMiddleware rateLimitMiddleware;

  public QualityTraceController(QualityTraceQueryService service,
      RateLimitMiddleware rateLimitMiddleware) {
    this.service = service;
    this.rateLimitMiddleware = rateLimitMiddleware;
  }

  @GetMapping("/{batchNo}")
  public ResponseEntity<BatchTracePayload> trace(@PathVariable String batchNo,
      @RequestHeader(value = "X-Actor-Id", required = false, defaultValue = "anonymous")
          String actorId) {
    rateLimitMiddleware.check("trace");
    return ResponseEntity.ok(service.trace(batchNo, actorId));
  }
}
