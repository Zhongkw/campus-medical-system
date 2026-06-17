package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalHealthProfile;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalHealthProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人健康档案控制器
 */
@RestController
@RequestMapping("/api/medical/health-profile")
public class MedicalHealthProfileController extends BaseController<MedicalHealthProfile> {

    @Autowired
    private MedicalHealthProfileService medicalHealthProfileService;

    @Override
    protected IBaseService<MedicalHealthProfile> getService() {
        return medicalHealthProfileService;
    }

    /**
     * 根据用户ID查询健康档案
     */
    @GetMapping("/user/{userId}")
    public Result<MedicalHealthProfile> getByUserId(@PathVariable Long userId) {
        return ResultUtils.success(medicalHealthProfileService.getByUserId(userId));
    }
}
