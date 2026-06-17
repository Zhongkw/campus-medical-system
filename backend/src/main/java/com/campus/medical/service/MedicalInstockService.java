package com.campus.medical.service;

import com.campus.medical.entity.MedicalInstock;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 入库单服务接口
 */
public interface MedicalInstockService extends IBaseService<MedicalInstock> {

    /**
     * 根据入库单号查询入库单
     */
    MedicalInstock getByInstockNo(String instockNo);

    /**
     * 根据状态查询入库单列表
     */
    List<MedicalInstock> getByStatus(Integer status);
}
