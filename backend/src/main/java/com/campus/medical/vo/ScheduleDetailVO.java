package com.campus.medical.vo;

import lombok.Data;

/**
 * 排班详情VO（含医生、科室信息）
 */
@Data
public class ScheduleDetailVO {

    private Long id;
    private Long doctorId;
    private Long deptId;
    private String doctorName;
    private String department;
    private String title;
    private String visitDate;
    private String visitTime;
    private Integer totalNumber;
    private Integer remainNumber;
    private Integer registerFee;
    private String scheduleType;
    private Integer status;
}
