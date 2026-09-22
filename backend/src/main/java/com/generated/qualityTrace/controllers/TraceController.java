package com.generated.qualityTrace.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.generated.qualityTrace.routes.TraceRoutes;
import com.generated.qualityTrace.services.TraceService;

@RestController
@RequestMapping(TraceRoutes.PATH)
public class TraceController {
  private final TraceService service;

  public TraceController(TraceService service) {
    this.service = service;
  }

  /** 批次全链路追溯树 */
  @GetMapping("/{batchNo}")
  public Map<String, Object> trace(@PathVariable String batchNo) {
    return service.trace(batchNo);
  }
}
