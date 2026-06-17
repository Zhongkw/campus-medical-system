package com.campus.medical.controller;

import com.campus.medical.entity.MedicalInstockItem;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalInstockItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 入库单明细控制器
 */
@RestController
@RequestMapping("/api/medical/instock-item")
public class MedicalInstockItemController extends BaseController<MedicalInstockItem> {

    @Autowired
    private MedicalInstockItemService medicalInstockItemService;

    @Override
    protected IBaseService<MedicalInstockItem> getService() {
        return medicalInstockItemService;
    }

    /**
     * 根据入库单ID查询入库单明细列表
     */
    @GetMapping("/instock/{instockId}")
    public List<MedicalInstockItem> getByInstockId(@PathVariable Long instockId) {
        return medicalInstockItemService.getByInstockId(instockId);
    }
}
