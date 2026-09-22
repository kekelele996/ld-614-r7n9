package com.generated.qualityTrace.middlewares;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.BatchNotFoundException;
import com.generated.qualityTrace.exceptions.BatchReleaseException;

/**
 * 全局异常处理（service/controller 之外的兜底包装，禁止在此吞掉业务异常：
 * 放行相关异常已在 controller 单独包装并返回阻塞项，这里仅兜底未覆盖路径）。
 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(BatchNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleNotFound(BatchNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(BatchReleaseException.class)
  public ResponseEntity<Map<String, Object>> handleRelease(BatchReleaseException ex) {
    log.warn("release exception escaped controller: {} {}", ex.getCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(body(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest()
        .body(body(ErrorCodes.RELEASE_PAYLOAD_INVALID, ErrorMessages.RELEASE_PAYLOAD_INVALID));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
    log.error("unhandled exception", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body(ErrorCodes.INTERNAL_ERROR, ex.getMessage()));
  }

  private Map<String, Object> body(String code, String message) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("code", code);
    map.put("message", message);
    return map;
  }
}
