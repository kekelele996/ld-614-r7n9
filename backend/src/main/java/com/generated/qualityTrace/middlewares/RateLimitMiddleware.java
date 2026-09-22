package com.generated.qualityTrace.middlewares;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;

/**
 * 极简接口限流中间件：按动作维度做固定窗口计数（演示用，本地内存）。
 * controller 在写操作入口显式调用 check(action)。
 */
@Component
public class RateLimitMiddleware {
  private static final int DEFAULT_LIMIT = 1000;

  private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();
  private final int limit;

  public RateLimitMiddleware() {
    this(DEFAULT_LIMIT);
  }

  public RateLimitMiddleware(int limit) {
    this.limit = limit;
  }

  public void check(String action) {
    AtomicInteger counter = counters.computeIfAbsent(action, key -> new AtomicInteger(0));
    int current = counter.incrementAndGet();
    if (current > limit) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
          ErrorCodes.RATE_LIMITED + ": " + ErrorMessages.RATE_LIMITED);
    }
  }

  void reset() {
    counters.clear();
  }
}
