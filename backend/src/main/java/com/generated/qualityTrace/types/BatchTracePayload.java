package com.generated.qualityTrace.types;

import java.util.List;
import java.util.Map;

/**
 * GET /api/trace/{batchNo} 批次全链路追溯树。
 * 放行区块展示放行结论、限制说明、放行人和时间及对应依据。
 */
public record BatchTracePayload(
    Map<String, Object> batch,
    Map<String, Object> workOrder,
    List<Map<String, Object>> inspections,
    List<Map<String, Object>> defects,
    Map<String, Object> release) {
}
