package com.campus.medical.service;

import com.campus.medical.entity.MedicalSickNote;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 电子病假条服务接口
 */
public interface MedicalSickNoteService extends IBaseService<MedicalSickNote> {

    /**
     * 根据病假条编号查询病假条
     */
    MedicalSickNote getBySickNoteNo(String sickNoteNo);

    /**
     * 根据用户ID查询病假条列表
     */
    List<MedicalSickNote> getByUserId(Long userId);

    /**
     * 根据医生ID查询病假条列表
     */
    List<MedicalSickNote> getByDoctorId(Long doctorId);
}
