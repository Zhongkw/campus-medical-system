package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalSickLeave;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalSickLeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电子病假申请控制器
 */
@RestController
@RequestMapping("/api/medical/sick-leave")
public class MedicalSickLeaveController extends BaseController<MedicalSickLeave> {

    @Autowired
    private MedicalSickLeaveService medicalSickLeaveService;

    @Override
    protected IBaseService<MedicalSickLeave> getService() {
        return medicalSickLeaveService;
    }

    /**
     * 根据病假申请编号查询病假申请
     */
    @GetMapping("/no/{leaveNo}")
    public Result<MedicalSickLeave> getByLeaveNo(@PathVariable String leaveNo) {
        return ResultUtils.success(medicalSickLeaveService.getByLeaveNo(leaveNo));
    }

    /**
     * 根据用户ID查询病假申请列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<MedicalSickLeave>> getByUserId(@PathVariable Long userId) {
        return ResultUtils.success(medicalSickLeaveService.getByUserId(userId));
    }

    /**
     * 根据状态查询病假申请列表
     */
    @GetMapping("/status/{status}")
    public Result<List<MedicalSickLeave>> getByStatus(@PathVariable String status) {
        return ResultUtils.success(medicalSickLeaveService.getByStatus(status));
    }
}
