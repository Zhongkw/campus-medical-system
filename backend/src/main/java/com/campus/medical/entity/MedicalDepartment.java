package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 科室表实体类
 */
@Data
@TableName("medical_department")
public class MedicalDepartment {

    /**
     * 科室ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 科室名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 科室编码
     */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 科室简介
     */
    @TableField("description")
    private String description;

    /**
     * 排序
     */
    @TableField("sort")
    private Integer sort;

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