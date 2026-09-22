package com.generated.qualityTrace.types;

/**
 * 质量经理提交批次放行结论的入参。
 *
 * @param batchId    批次ID（与 batchNo 至少提供一个）
 * @param batchNo    批次号
 * @param conclusion RELEASED 或 CONDITIONAL_RELEASED
 * @param limitNote  限制说明；有条件放行必填，正常放行必须为空
 * @param managerId  放行人工号（质量经理）
 * @param managerRole 提交人角色，必须为 QUALITY_MANAGER
 */
public record ReleaseDecisionPayload(
    Long batchId,
    String batchNo,
    String conclusion,
    String limitNote,
    String managerId,
    String managerRole) {
}
