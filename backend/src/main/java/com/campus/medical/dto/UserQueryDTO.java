package com.campus.medical.dto;

import lombok.Data;

/**
 * 用户查询条件
 */
@Data
public class UserQueryDTO {
    /**
     * 登录账号
     */
    private String username;

    /**
     * 用户姓名
     */
    private String realName;
    
    /**
     * 角色ID
     */
    private Long roleId;
    
    /**
     * 状态（0-禁用 1-启用）
     */
    private Integer status;
    
    /**
     * 页码
     */
    private Long pageNum = 1L;
    
    /**
     * 每页记录数
     */
    private Long pageSize = 10L;
}
