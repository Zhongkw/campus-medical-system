package com.campus.medical.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退号申请列表VO
 */
@Data
public class CancelRegVO {
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
     * 医生姓名
     */
    private String doctorName;
    
    /**
     * 原预约号
     */
    private String appointmentNo;
    
    /**
     * 退号金额
     */
    private BigDecimal amount;
    
    /**
     * 退号原因
     */
    private String reason;
    
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
