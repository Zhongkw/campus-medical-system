package com.campus.medical.service;

import com.campus.medical.entity.MedicalPhysicalExam;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 体检记录服务接口
 */
public interface MedicalPhysicalExamService extends IBaseService<MedicalPhysicalExam> {

    /**
     * 根据用户ID查询体检记录列表
     */
    List<MedicalPhysicalExam> getByUserId(Long userId);

    /**
     * 根据体检类型查询体检记录列表
     */
    List<MedicalPhysicalExam> getByExamType(String examType);
}
