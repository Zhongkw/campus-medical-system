package com.campus.medical.service;

import com.campus.medical.entity.MedicalOrderItem;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 订单明细服务接口
 */
public interface MedicalOrderItemService extends IBaseService<MedicalOrderItem> {

    /**
     * 根据订单ID查询订单明细列表
     */
    List<MedicalOrderItem> getByOrderId(Long orderId);
}
