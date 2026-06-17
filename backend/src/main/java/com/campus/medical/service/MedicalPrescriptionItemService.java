package com.campus.medical.service;

import com.campus.medical.entity.MedicalPrescriptionItem;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 处方明细服务接口
 */
public interface MedicalPrescriptionItemService extends IBaseService<MedicalPrescriptionItem> {

    /**
     * 根据处方ID查询处方明细列表
     */
    List<MedicalPrescriptionItem> getByPrescriptionId(Long prescriptionId);
}
