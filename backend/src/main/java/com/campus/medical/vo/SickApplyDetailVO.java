package com.campus.medical.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 病假申请详情VO
 */
@Data
public class SickApplyDetailVO {
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
     * 学号
     */
    private String studentNo;
    
    /**
     * 学院
     */
    private String college;
    
    /**
     * 班级
     */
    private String className;
    
    /**
     * 联系方式
     */
    private String phone;
    
    /**
     * 请假开始日期
     */
    private String startDate;
    
    /**
     * 请假结束日期
     */
    private String endDate;
    
    /**
     * 请假天数
     */
    private Integer days;
    
    /**
     * 病因
     */
    private String reason;
    
    /**
     * 诊断证明URL
     */
    private String diagnosisProofUrl;
    
    /**
     * 状态（0-待审核 1-通过 2-驳回）
     */
    private Integer status;
    
    /**
     * 状态名称
     */
    private String statusName;
    
    /**
     * 审核内容
     */
    private String auditContent;
    
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
