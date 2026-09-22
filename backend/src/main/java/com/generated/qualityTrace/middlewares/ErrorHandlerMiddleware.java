package com.generated.qualityTrace.middlewares;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constructors.BatchReleaseDtoFactory;
import com.generated.qualityTrace.exceptions.BizException;
import com.generated.qualityTrace.exceptions.ReleaseBlockedException;

@RestControllerAdvice
public class ErrorHandlerMiddleware {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  /** 放行被拒绝：返回全部阻塞项 */
  @ExceptionHandler(ReleaseBlockedException.class)
  public ResponseEntity<Map<String, Object>> blocked(ReleaseBlockedException e) {
    List<Map<String, Object>> blockers = e.getBlockers().stream()
        .map(BatchReleaseDtoFactory::blockerView).toList();
    Map<String, Object> error = new LinkedHashMap<>();
    error.put("code", ErrorCodes.RELEASE_BLOCKED);
    error.put("message", e.getMessage());
    error.put("blockers", blockers);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("error", error));
  }

  @ExceptionHandler(BizException.class)
  public ResponseEntity<Map<String, Object>> biz(BizException e) {
    Map<String, Object> error = new LinkedHashMap<>();
    error.put("code", e.getCode());
    error.put("message", e.getMessage());
    return ResponseEntity.status(e.getStatus()).body(Map.of("error", error));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> unknown(Exception e) {
    log.error("[error] unhandled", e);
    Map<String, Object> error = new LinkedHashMap<>();
    error.put("code", ErrorCodes.INTERNAL_ERROR);
    error.put("message", ErrorMessages.INTERNAL_ERROR);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", error));
  }
}
