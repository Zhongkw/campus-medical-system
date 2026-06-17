package com.campus.medical.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 特殊挂号申请详情VO
 */
@Data
public class SpecialRegDetailVO {
    /**
     * 申请ID
     */
    private Long id;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 学号
     */
    private String studentNo;
    
    /**
     * 联系方式
     */
    private String phone;
    
    /**
     * 医生姓名
     */
    private String doctorName;
    
    /**
     * 科室名称
     */
    private String deptName;
    
    /**
     * 就诊日期
     */
    private String visitDate;
    
    /**
     * 就诊时段
     */
    private String timeSlot;
    
    /**
     * 申请原因
     */
    private String applyReason;
    
    /**
     * 状态（0-待审核 1-通过 2-驳回）
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核人
     */
    private String auditorName;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
