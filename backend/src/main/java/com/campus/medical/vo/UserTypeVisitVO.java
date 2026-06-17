package com.campus.medical.vo;

import lombok.Data;

/**
 * 人群分布
 */
@Data
public class UserTypeVisitVO {
    /**
     * 用户类型（学生/教职工）
     */
    private String userType;
    
    /**
     * 人数
     */
    private Long count;
}
