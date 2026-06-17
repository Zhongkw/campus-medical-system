package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalAppointment;
import com.campus.medical.mapper.MedicalAppointmentMapper;
import com.campus.medical.service.MedicalAppointmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预约服务实现类
 */
@Service
public class MedicalAppointmentServiceImpl extends BaseServiceImpl<MedicalAppointmentMapper, MedicalAppointment> implements MedicalAppointmentService {

    /**
     * 根据患者ID查询预约列表
     */
    @Override
    public List<MedicalAppointment> getByPatientId(Long patientId) {
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalAppointment::getUserId, patientId);
        wrapper.orderByDesc(MedicalAppointment::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据医生ID查询预约列表
     */
    @Override
    public List<MedicalAppointment> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalAppointment::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalAppointment::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据预约编号查询预约
     */
    @Override
    public MedicalAppointment getByAppointmentNo(String appointmentNo) {
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalAppointment::getAppointmentNo, appointmentNo);
        return baseMapper.selectOne(wrapper);
    }
}
