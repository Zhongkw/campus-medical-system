package com.campus.medical.service;

import com.campus.medical.entity.MedicalInvoice;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 发票服务接口
 */
public interface MedicalInvoiceService extends IBaseService<MedicalInvoice> {

    /**
     * 根据发票编号查询发票
     */
    MedicalInvoice getByInvoiceNo(String invoiceNo);

    /**
     * 根据用户ID查询发票列表
     */
    List<MedicalInvoice> getByUserId(Long userId);

    /**
     * 根据订单ID查询发票
     */
    MedicalInvoice getByOrderId(Long orderId);
}
