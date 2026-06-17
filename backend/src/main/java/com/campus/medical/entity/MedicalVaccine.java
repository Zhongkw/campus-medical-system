package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 疫苗接种记录表实体类
 */
@Data
@TableName("medical_vaccine")
public class MedicalVaccine {

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
     * 疫苗名称
     */
    @TableField("vaccine_name")
    private String vaccineName;

    /**
     * 接种日期
     */
    @TableField("vaccine_date")
    private LocalDate vaccineDate;

    /**
     * 剂次
     */
    @TableField("dose")
    private String dose;

    /**
     * 生产厂家
     */
    @TableField("manufacturer")
    private String manufacturer;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}