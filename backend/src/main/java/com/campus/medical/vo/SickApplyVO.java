package com.campus.medical.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 病假申请列表VO
 */
@Data
public class SickApplyVO {
    /**
     * 申请ID
     */
    private Long id;
    
    /**
     * 病假单号
     */
    private String leaveNo;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 学院
     */
    private String college;
    
    /**
     * 请假天数
     */
    private Integer days;
    
    /**
     * 状态（0-待审核 1-通过 2-驳回）
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
