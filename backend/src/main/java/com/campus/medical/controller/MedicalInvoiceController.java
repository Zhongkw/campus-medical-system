package com.campus.medical.controller;

import com.campus.medical.entity.MedicalInvoice;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发票控制器
 */
@RestController
@RequestMapping("/api/medical/invoice")
public class MedicalInvoiceController extends BaseController<MedicalInvoice> {

    @Autowired
    private MedicalInvoiceService medicalInvoiceService;

    @Override
    protected IBaseService<MedicalInvoice> getService() {
        return medicalInvoiceService;
    }

    /**
     * 根据发票编号查询发票
     */
    @GetMapping("/no/{invoiceNo}")
    public MedicalInvoice getByInvoiceNo(@PathVariable String invoiceNo) {
        return medicalInvoiceService.getByInvoiceNo(invoiceNo);
    }

    /**
     * 根据用户ID查询发票列表
     */
    @GetMapping("/user/{userId}")
    public List<MedicalInvoice> getByUserId(@PathVariable Long userId) {
        return medicalInvoiceService.getByUserId(userId);
    }

    /**
     * 根据订单ID查询发票
     */
    @GetMapping("/order/{orderId}")
    public MedicalInvoice getByOrderId(@PathVariable Long orderId) {
        return medicalInvoiceService.getByOrderId(orderId);
    }
}
