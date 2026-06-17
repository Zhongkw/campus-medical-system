package com.campus.medical.vo;

import lombok.Data;

/**
 * 候诊队列详情VO（含学生信息）
 */
@Data
public class QueueDetailVO {

    private Long id;
    private Long appointmentId;
    private Long deptId;
    private String queueNo;
    private Long userId;
    private String studentName;
    private String studentNo;
    private String department;
    private String symptom;
    private String status;
    private String waitTime;
    private String queueDate;
}
