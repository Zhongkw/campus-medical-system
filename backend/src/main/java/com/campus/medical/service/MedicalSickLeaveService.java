package com.campus.medical.service;

import com.campus.medical.entity.MedicalSickLeave;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 电子病假申请服务接口
 */
public interface MedicalSickLeaveService extends IBaseService<MedicalSickLeave> {

    /**
     * 根据病假申请编号查询病假申请
     */
    MedicalSickLeave getByLeaveNo(String leaveNo);

    /**
     * 根据用户ID查询病假申请列表
     */
    List<MedicalSickLeave> getByUserId(Long userId);

    /**
     * 根据状态查询病假申请列表
     */
    List<MedicalSickLeave> getByStatus(String status);
}
