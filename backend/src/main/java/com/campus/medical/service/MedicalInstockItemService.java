package com.campus.medical.service;

import com.campus.medical.entity.MedicalInstockItem;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 入库单明细服务接口
 */
public interface MedicalInstockItemService extends IBaseService<MedicalInstockItem> {

    /**
     * 根据入库单ID查询入库单明细列表
     */
    List<MedicalInstockItem> getByInstockId(Long instockId);
}
