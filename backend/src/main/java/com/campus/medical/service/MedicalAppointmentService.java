package com.campus.medical.service;

import com.campus.medical.entity.MedicalAppointment;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 预约服务接口
 */
public interface MedicalAppointmentService extends IBaseService<MedicalAppointment> {

    /**
     * 根据患者ID查询预约列表
     */
    List<MedicalAppointment> getByPatientId(Long patientId);

    /**
     * 根据医生ID查询预约列表
     */
    List<MedicalAppointment> getByDoctorId(Long doctorId);

    /**
     * 根据预约编号查询预约
     */
    MedicalAppointment getByAppointmentNo(String appointmentNo);
}
