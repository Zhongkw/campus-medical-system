package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 病历模板表实体类
 */
@Data
@TableName("medical_template")
public class MedicalTemplate {

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板名称
     */
    @TableField("template_name")
    private String templateName;

    /**
     * 模板内容
     */
    @TableField("template_content")
    private String templateContent;

    /**
     * 模板类型(发热/腹痛/外伤等)
     */
    @TableField("template_type")
    private String templateType;

    /**
     * 创建医生ID(0表示系统通用模板)
     */
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 状态(0-禁用,1-启用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}