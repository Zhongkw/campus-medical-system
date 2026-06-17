package com.campus.medical.service;

import com.campus.medical.entity.MedicalVaccine;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 疫苗接种记录服务接口
 */
public interface MedicalVaccineService extends IBaseService<MedicalVaccine> {

    /**
     * 根据用户ID查询疫苗接种记录列表
     */
    List<MedicalVaccine> getByUserId(Long userId);
}
