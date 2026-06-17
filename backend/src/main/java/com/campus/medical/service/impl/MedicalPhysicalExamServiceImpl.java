package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalPhysicalExam;
import com.campus.medical.mapper.MedicalPhysicalExamMapper;
import com.campus.medical.service.MedicalPhysicalExamService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 体检记录服务实现类
 */
@Service
public class MedicalPhysicalExamServiceImpl extends BaseServiceImpl<MedicalPhysicalExamMapper, MedicalPhysicalExam> implements MedicalPhysicalExamService {

    /**
     * 根据用户ID查询体检记录列表
     */
    @Override
    public List<MedicalPhysicalExam> getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalPhysicalExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPhysicalExam::getUserId, userId);
        wrapper.orderByDesc(MedicalPhysicalExam::getExamDate);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据体检类型查询体检记录列表
     */
    @Override
    public List<MedicalPhysicalExam> getByExamType(String examType) {
        LambdaQueryWrapper<MedicalPhysicalExam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPhysicalExam::getExamType, examType);
        wrapper.orderByDesc(MedicalPhysicalExam::getExamDate);
        return baseMapper.selectList(wrapper);
    }
}
