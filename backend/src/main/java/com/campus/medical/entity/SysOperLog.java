package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志表实体类
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块
     */
    @TableField("title")
    private String title;

    /**
     * 业务类型(0-新增,1-修改,2-删除,3-查询,4-导出,5-导入)
     */
    @TableField("business_type")
    private Integer businessType;

    /**
     * 请求方法
     */
    @TableField("method")
    private String method;

    /**
     * 请求URL
     */
    @TableField("request_url")
    private String requestUrl;

    /**
     * 请求参数
     */
    @TableField("request_param")
    private String requestParam;

    /**
     * 响应结果
     */
    @TableField("response_result")
    private String responseResult;

    /**
     * 操作人ID
     */
    @TableField("oper_user_id")
    private Long operUserId;

    /**
     * 操作人姓名
     */
    @TableField("oper_user_name")
    private String operUserName;

    /**
     * 操作IP
     */
    @TableField("oper_ip")
    private String operIp;

    /**
     * 操作时间
     */
    @TableField("oper_time")
    private LocalDateTime operTime;

    /**
     * 操作状态(0-成功,1-失败)
     */
    @TableField("status")
    private Integer status;

    /**
     * 错误信息
     */
    @TableField("error_msg")
    private String errorMsg;
}