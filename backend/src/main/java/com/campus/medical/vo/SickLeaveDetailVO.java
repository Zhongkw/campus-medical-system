package com.campus.medical.vo;

import lombok.Data;

/**
 * 病假申请详情VO
 */
@Data
public class SickLeaveDetailVO {

    /**
     * 病假申请ID
     */
    private Long id;

    /**
     * 病假申请编号
     */
    private String leaveNo;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学号
     */
    private String studentId;

    /**
     * 所属学院
     */
    private String college;

    /**
     * 就诊日期
     */
    private String visitDate;

    /**
     * 就诊科室
     */
    private String department;

    /**
     * 诊断结果
     */
    private String diagnosis;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 请假天数
     */
    private Integer leaveDays;

    /**
     * 诊断证明URL
     */
    private String diagnosisProofUrl;

    /**
     * 状态
     */
    private String status;

    /**
     * 审核意见
     */
    private String auditContent;

    /**
     * 审核时间
     */
    private String auditTime;

    /**
     * 创建时间
     */
    private String createTime;
}
