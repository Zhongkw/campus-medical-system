package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生表实体类
 */
@Data
@TableName("medical_doctor")
public class MedicalDoctor {

    /**
     * 医生ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联系统用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属科室ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 职称
     */
    @TableField("title")
    private String title;

    /**
     * 专业特长
     */
    @TableField("specialty")
    private String specialty;

    /**
     * 医生简介
     */
    @TableField("introduction")
    private String introduction;

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