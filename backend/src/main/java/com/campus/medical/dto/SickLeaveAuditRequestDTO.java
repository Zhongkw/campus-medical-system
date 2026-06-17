package com.campus.medical.dto;

import lombok.Data;

/**
 * 病假审批请求DTO
 */
@Data
public class SickLeaveAuditRequestDTO {

    /**
     * 病假申请ID
     */
    private Long applyId;

    /**
     * 审核状态(PASS/REJECT)
     */
    private String auditStatus;

    /**
     * 审核意见
     */
    private String auditContent;

    /**
     * 审核人ID
     */
    private Long auditorId;
}
