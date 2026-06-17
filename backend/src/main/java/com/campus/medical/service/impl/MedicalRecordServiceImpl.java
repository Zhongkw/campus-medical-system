package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.mapper.MedicalRecordMapper;
import com.campus.medical.service.MedicalRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 诊疗记录服务实现类
 */
@Service
public class MedicalRecordServiceImpl extends BaseServiceImpl<MedicalRecordMapper, MedicalRecord> implements MedicalRecordService {

    /**
     * 根据患者ID查询诊疗记录列表
     */
    @Override
    public List<MedicalRecord> getByPatientId(Long patientId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getUserId, patientId);
        wrapper.orderByDesc(MedicalRecord::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据医生ID查询诊疗记录列表
     */
    @Override
    public List<MedicalRecord> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalRecord::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据病历号查询诊疗记录
     */
    @Override
    public MedicalRecord getByRecordNo(String recordNo) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalRecord::getRecordNo, recordNo);
        return baseMapper.selectOne(wrapper);
    }
}
