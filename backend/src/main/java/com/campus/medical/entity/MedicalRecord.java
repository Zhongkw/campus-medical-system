package com.campus.medical.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 电子病历表实体类
 */
@Data
@TableName("medical_record")
public class MedicalRecord {

    /**
     * 病历ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 病历编号
     */
    @TableField("record_no")
    private String recordNo;

    /**
     * 患者用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 接诊医生ID
     */
    @TableField("doctor_id")
    private Long doctorId;

    /**
     * 科室ID
     */
    @TableField("dept_id")
    private Long deptId;

    /**
     * 关联预约ID
     */
    @TableField("appointment_id")
    private Long appointmentId;

    /**
     * 主诉
     */
    @TableField("chief_complaint")
    private String chiefComplaint;

    /**
     * 现病史
     */
    @TableField("present_illness")
    private String presentIllness;

    /**
     * 既往史
     */
    @TableField("past_history")
    private String pastHistory;

    /**
     * 体格检查
     */
    @TableField("physical_examination")
    private String physicalExamination;

    /**
     * 诊断结果
     */
    @TableField("diagnosis")
    private String diagnosis;

    /**
     * 病种编码（关联基础数据-病种管理，非必填）
     */
    @TableField("disease_code")
    private String diseaseCode;

    /**
     * 病种名称（关联基础数据-病种管理，非必填）
     */
    @TableField("disease_name")
    private String diseaseName;

    /**
     * 医嘱
     */
    @TableField("advice")
    private String advice;
    /**
     * 学院（冗余字段）
     */
    @TableField("college")
    private String college;
    /**
     * 科室名称（冗余字段）
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 患者ID（冗余字段，与userId保持一致）
     */
    @TableField("patient_id")
    private Long patientId;

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