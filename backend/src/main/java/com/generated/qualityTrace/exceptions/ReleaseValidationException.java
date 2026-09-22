package com.generated.qualityTrace.exceptions;

import java.util.List;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/**
 * 放行结论校验失败（只能有条件放行却要求正常放行、限制说明缺失/误填等）。
 * 条件项（如未关闭一般不良、最近让步接收）随 details 一并返回。
 */
public class ReleaseValidationException extends BatchReleaseException {
  private final transient List<ReleaseBasisItemPayload> conditions;

  public ReleaseValidationException(String code, String message,
      List<ReleaseBasisItemPayload> conditions) {
    super(code, message);
    this.conditions = List.copyOf(conditions);
  }

  @Override
  public List<ReleaseBasisItemPayload> getDetails() {
    return conditions;
  }
}
