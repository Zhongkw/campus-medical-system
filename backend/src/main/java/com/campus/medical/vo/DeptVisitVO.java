package com.campus.medical.vo;

import lombok.Data;

/**
 * 科室就诊排行
 */
@Data
public class DeptVisitVO {
    /**
     * 科室名称
     */
    private String deptName;
    
    /**
     * 就诊人数
     */
    private Long visitCount;
}
