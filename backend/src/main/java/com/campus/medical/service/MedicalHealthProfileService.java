package com.campus.medical.service;

import com.campus.medical.entity.MedicalHealthProfile;
import com.campus.medical.service.IBaseService;

/**
 * 个人健康档案服务接口
 */
public interface MedicalHealthProfileService extends IBaseService<MedicalHealthProfile> {

    /**
     * 根据用户ID查询健康档案
     */
    MedicalHealthProfile getByUserId(Long userId);
}
