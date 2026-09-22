package com.generated.qualityTrace.constants;

public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "missing token";
  public static final String RBAC_DENIED = "role denied";

  public static final String BATCH_NOT_FOUND = "product batch not found: %s";
  public static final String RELEASE_ALREADY_EXISTS =
      "batch %s already released at %s by %s; repeated or concurrent submission can only take effect once";
  public static final String RELEASE_BLOCKED =
      "batch %s release rejected: %d blocking item(s) must be resolved first";
  public static final String RELEASE_CONDITIONAL_REQUIRED =
      "batch %s: latest inspection is %s and/or general defects remain unclosed, "
          + "only conditional release is allowed";
  public static final String RELEASE_FULL_NOT_ALLOWED =
      "batch %s: full release is not allowed under current conditions, use CONDITIONAL_RELEASED";
  public static final String RELEASE_LIMIT_NOTE_REQUIRED =
      "batch %s: restriction note (limitNote) is mandatory for conditional release";
  public static final String RELEASE_LIMIT_NOTE_FORBIDDEN =
      "batch %s: restriction note (limitNote) must be empty for full release";
  public static final String RELEASE_INVALID_CONCLUSION = "invalid release conclusion: %s";
  public static final String RELEASE_PAYLOAD_INVALID = "release decision payload is invalid";
  public static final String RELEASE_CONCURRENT =
      "concurrent release for batch %s detected, only one submission can take effect";
  public static final String RELEASE_NOT_FOUND = "no release record found for batch: %s";
  public static final String RATE_LIMITED = "too many requests";

  public static final String OPEN_MAJOR_OR_CRITICAL_DEFECT =
      "unclosed %s defect: defectRecordId=%d, defectType=%s, dispositionStatus=%s";
  public static final String LATEST_INSPECTION_FAIL =
      "latest inspection failed: inspectionId=%d, inspectionType=%s, resultStatus=FAIL, inspectedAt=%s";
  public static final String LATEST_INSPECTION_RECHECK =
      "latest inspection pending recheck: inspectionId=%d, inspectionType=%s, resultStatus=RECHECK, inspectedAt=%s";
  public static final String LATEST_INSPECTION_CONDITIONAL =
      "latest inspection conditional pass: inspectionId=%d, inspectionType=%s, resultStatus=CONDITIONAL_PASS, inspectedAt=%s";
  public static final String OPEN_MINOR_DEFECT =
      "unclosed general (MINOR) defect: defectRecordId=%d, defectType=%s, dispositionStatus=%s";
  public static final String NO_INSPECTION = "batch has no inspection record yet";
}
