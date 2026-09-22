package com.generated.qualityTrace.exceptions;

public class BizException extends RuntimeException {
  private final String code;
  private final int status;

  public BizException(String code, String message, int status) {
    super(message);
    this.code = code;
    this.status = status;
  }

  public String getCode() { return code; }
  public int getStatus() { return status; }
}
