package com.campus.medical.dto;

import lombok.Data;

/**
 * 系统用户DTO
 */
@Data
public class SysUserDTO {
    /**
     * 用户ID（编辑时使用）
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（新增时使用）
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 部门
     */
    private String department;

    /**
     * 科室ID（医生角色使用）
     */
    private Long deptId;
    
    /**
     * 班级
     */
    private String className;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
}
