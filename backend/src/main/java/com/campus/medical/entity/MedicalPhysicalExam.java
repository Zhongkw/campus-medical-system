package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 体检记录表实体类
 */
@Data
@TableName("medical_physical_exam")
public class MedicalPhysicalExam {

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 体检日期
     */
    @TableField("exam_date")
    private LocalDate examDate;

    /**
     * 体检类型
     */
    @TableField("exam_type")
    private String examType;

    /**
     * 体检报告URL
     */
    @TableField("report_url")
    private String reportUrl;

    /**
     * 体检结论
     */
    @TableField("conclusion")
    private String conclusion;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}