package com.campus.medical.service;

import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 订单服务接口
 */
public interface MedicalOrderService extends IBaseService<MedicalOrder> {

    /**
     * 根据用户ID查询订单列表
     */
    List<MedicalOrder> getByUserId(Long userId);

    /**
     * 根据订单编号查询订单
     */
    MedicalOrder getByOrderNo(String orderNo);

    /**
     * 根据状态查询订单列表
     */
    List<MedicalOrder> getByStatus(String status);
}
