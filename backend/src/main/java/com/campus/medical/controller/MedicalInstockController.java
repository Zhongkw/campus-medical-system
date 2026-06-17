package com.campus.medical.controller;

import com.campus.medical.entity.MedicalInstock;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalInstockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 入库单控制器
 */
@RestController
@RequestMapping("/api/medical/instock")
public class MedicalInstockController extends BaseController<MedicalInstock> {

    @Autowired
    private MedicalInstockService medicalInstockService;

    @Override
    protected IBaseService<MedicalInstock> getService() {
        return medicalInstockService;
    }

    /**
     * 根据入库单号查询入库单
     */
    @GetMapping("/no/{instockNo}")
    public MedicalInstock getByInstockNo(@PathVariable String instockNo) {
        return medicalInstockService.getByInstockNo(instockNo);
    }

    /**
     * 根据状态查询入库单列表
     */
    @GetMapping("/status/{status}")
    public List<MedicalInstock> getByStatus(@PathVariable Integer status) {
        return medicalInstockService.getByStatus(status);
    }
}
