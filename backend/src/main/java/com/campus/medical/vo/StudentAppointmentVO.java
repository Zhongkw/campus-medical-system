package com.campus.medical.vo;

import lombok.Data;

/**
 * 学生预约列表VO
 */
@Data
public class StudentAppointmentVO {

    private Long id;

    private String appointmentNo;

    private String doctorName;

    private String deptName;

    private String appointmentDate;

    private String timeSlot;

    private String status;

    private String queueNo;

    /** 是否可直接退号（待确认） */
    private Boolean canDirectCancel;

    /** 是否可申请退号（已确认） */
    private Boolean canApplyCancel;

    /** 是否有待审核的退号申请 */
    private Boolean pendingCancelApproval;
}
