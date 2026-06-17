package com.campus.medical.controller;

import com.campus.medical.entity.MedicalPhysicalExam;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalPhysicalExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 体检记录控制器
 */
@RestController
@RequestMapping("/api/medical/physical-exam")
public class MedicalPhysicalExamController extends BaseController<MedicalPhysicalExam> {

    @Autowired
    private MedicalPhysicalExamService medicalPhysicalExamService;

    @Override
    protected IBaseService<MedicalPhysicalExam> getService() {
        return medicalPhysicalExamService;
    }

    /**
     * 根据用户ID查询体检记录列表
     */
    @GetMapping("/user/{userId}")
    public List<MedicalPhysicalExam> getByUserId(@PathVariable Long userId) {
        return medicalPhysicalExamService.getByUserId(userId);
    }

    /**
     * 根据体检类型查询体检记录列表
     */
    @GetMapping("/type/{examType}")
    public List<MedicalPhysicalExam> getByExamType(@PathVariable String examType) {
        return medicalPhysicalExamService.getByExamType(examType);
    }
}
