package com.campus.medical.controller;

import com.campus.medical.entity.MedicalOrderItem;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单明细控制器
 */
@RestController
@RequestMapping("/api/medical/order-item")
public class MedicalOrderItemController extends BaseController<MedicalOrderItem> {

    @Autowired
    private MedicalOrderItemService medicalOrderItemService;

    @Override
    protected IBaseService<MedicalOrderItem> getService() {
        return medicalOrderItemService;
    }

    /**
     * 根据订单ID查询订单明细列表
     */
    @GetMapping("/order/{orderId}")
    public List<MedicalOrderItem> getByOrderId(@PathVariable Long orderId) {
        return medicalOrderItemService.getByOrderId(orderId);
    }
}
