package com.campus.medical.service;

import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 排班服务接口
 */
public interface MedicalScheduleService extends IBaseService<MedicalSchedule> {

    /**
     * 根据医生ID查询排班列表
     */
    List<MedicalSchedule> getByDoctorId(Long doctorId);

    /**
     * 根据日期查询排班列表
     */
    List<MedicalSchedule> getByScheduleDate(String scheduleDate);

    MedicalSchedule createSchedule(MedicalSchedule schedule);

    MedicalSchedule updateScheduleSlots(MedicalSchedule schedule);
}
