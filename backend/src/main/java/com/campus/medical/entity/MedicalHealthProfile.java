package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人健康档案表实体类
 */
@Data
@TableName("medical_health_profile")
public class MedicalHealthProfile {

    /**
     * 档案ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 血型
     */
    @TableField("blood_type")
    private String bloodType;

    /**
     * 过敏史
     */
    @TableField("allergy")
    private String allergy;

    /**
     * 既往病史
     */
    @TableField("past_history")
    private String pastHistory;

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