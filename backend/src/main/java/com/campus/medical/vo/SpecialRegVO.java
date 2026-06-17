package com.campus.medical.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 特殊挂号申请列表VO
 */
@Data
public class SpecialRegVO {
    /**
     * 申请ID
     */
    private Long id;
    
    /**
     * 学生姓名
     */
    private String studentName;
    
    /**
     * 医生姓名
     */
    private String doctorName;
    
    /**
     * 就诊时段
     */
    private String timeSlot;
    
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
