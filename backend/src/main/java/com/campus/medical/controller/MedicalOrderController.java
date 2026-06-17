package com.campus.medical.controller;

import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/medical/order")
public class MedicalOrderController extends BaseController<MedicalOrder> {

    @Autowired
    private MedicalOrderService medicalOrderService;

    @Override
    protected IBaseService<MedicalOrder> getService() {
        return medicalOrderService;
    }

    /**
     * 根据用户ID查询订单列表
     */
    @GetMapping("/user/{userId}")
    public List<MedicalOrder> getByUserId(@PathVariable Long userId) {
        return medicalOrderService.getByUserId(userId);
    }

    /**
     * 根据订单编号查询订单
     */
    @GetMapping("/no/{orderNo}")
    public MedicalOrder getByOrderNo(@PathVariable String orderNo) {
        return medicalOrderService.getByOrderNo(orderNo);
    }

    /**
     * 根据状态查询订单列表
     */
    @GetMapping("/status/{status}")
    public List<MedicalOrder> getByStatus(@PathVariable String status) {
        return medicalOrderService.getByStatus(status);
    }
}
