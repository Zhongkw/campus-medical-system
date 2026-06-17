package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalPrescription;
import com.campus.medical.mapper.MedicalPrescriptionMapper;
import com.campus.medical.service.MedicalPrescriptionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 处方服务实现类
 */
@Service
public class MedicalPrescriptionServiceImpl extends BaseServiceImpl<MedicalPrescriptionMapper, MedicalPrescription> implements MedicalPrescriptionService {

    /**
     * 根据患者ID查询处方列表
     */
    @Override
    public List<MedicalPrescription> getByPatientId(Long patientId) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescription::getUserId, patientId);
        wrapper.orderByDesc(MedicalPrescription::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据医生ID查询处方列表
     */
    @Override
    public List<MedicalPrescription> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescription::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalPrescription::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据处方编号查询处方
     */
    @Override
    public MedicalPrescription getByPrescriptionNo(String prescriptionNo) {
        LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalPrescription::getPrescriptionNo, prescriptionNo);
        return baseMapper.selectOne(wrapper);
    }
}
