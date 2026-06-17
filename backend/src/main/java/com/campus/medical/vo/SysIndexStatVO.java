package com.campus.medical.vo;

import lombok.Data;

/**
 * 系统管理首页统计
 */
@Data
public class SysIndexStatVO {
    /**
     * 用户总数
     */
    private Long userCount;
    
    /**
     * 角色总数
     */
    private Long roleCount;
    
    /**
     * 今日登录人数
     */
    private Long todayLogin;
    
    /**
     * 操作日志数量
     */
    private Long logCount;
}
