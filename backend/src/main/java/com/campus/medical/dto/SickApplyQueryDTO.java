package com.campus.medical.dto;

import lombok.Data;

/**
 * 病假查询条件
 */
@Data
public class SickApplyQueryDTO {
    /**
     * 学院
     */
    private String college;
    
    /**
     * 审核状态（0-待审核 1-通过 2-驳回）
     */
    private Integer auditStatus;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
    
    /**
     * 页码
     */
    private Long pageNum = 1L;
    
    /**
     * 每页记录数
     */
    private Long pageSize = 10L;
}
