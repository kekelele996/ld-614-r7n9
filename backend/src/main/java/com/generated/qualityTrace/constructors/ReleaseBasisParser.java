package com.generated.qualityTrace.constructors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.generated.qualityTrace.types.ReleaseBasisItemPayload;

/**
 * 放行依据快照解析器：把 BatchRelease.basis 中存储的 JSON 文本还原为结构化依据条目。
 * 与 BatchReleaseService#serializeBasis 成对维护（字段变更必须同步两处）。
 */
public final class ReleaseBasisParser {

  private static final Pattern OBJECT_PATTERN = Pattern.compile("\\{([^}]*)}");
  private static final Pattern FIELD_PATTERN =
      Pattern.compile("\"(kind|code|message|refType|refId|severity|resultStatus)\"\\s*:\\s*(null|\"(?:[^\"\\\\]|\\\\.)*\")");

  private ReleaseBasisParser() {}

  public static List<ReleaseBasisItemPayload> parse(String json) {
    List<ReleaseBasisItemPayload> result = new ArrayList<>();
    if (json == null || json.isBlank()) {
      return result;
    }
    Matcher objectMatcher = OBJECT_PATTERN.matcher(json);
    while (objectMatcher.find()) {
      String body = objectMatcher.group(1);
      String kind = null;
      String code = null;
      String message = null;
      String refType = null;
      String refId = null;
      String severity = null;
      String resultStatus = null;

      Matcher fieldMatcher = FIELD_PATTERN.matcher(body);
      while (fieldMatcher.find()) {
        String name = fieldMatcher.group(1);
        String raw = fieldMatcher.group(2);
        String value = "null".equals(raw) ? null
            : raw.substring(1, raw.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
        switch (name) {
          case "kind" -> kind = value;
          case "code" -> code = value;
          case "message" -> message = value;
          case "refType" -> refType = value;
          case "refId" -> refId = value;
          case "severity" -> severity = value;
          case "resultStatus" -> resultStatus = value;
          default -> { }
        }
      }
      result.add(new ReleaseBasisItemPayload(kind, code, message, refType, refId,
          severity, resultStatus));
    }
    return result;
  }
}
