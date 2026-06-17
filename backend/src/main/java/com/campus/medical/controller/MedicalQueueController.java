package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalQueue;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalQueueService;
import com.campus.medical.vo.QueueDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 队列控制器
 */
@RestController
@RequestMapping("/api/medical/queue")
public class MedicalQueueController extends BaseController<MedicalQueue> {

    @Autowired
    private MedicalQueueService medicalQueueService;

    @Override
    protected IBaseService<MedicalQueue> getService() {
        return medicalQueueService;
    }

    /**
     * 根据科室ID查询队列列表
     */
    @GetMapping("/dept/{deptId}")
    public Result<List<MedicalQueue>> getByDeptId(@PathVariable Long deptId) {
        return ResultUtils.success(medicalQueueService.getByDeptId(deptId));
    }

    /**
     * 根据医生ID查询队列列表
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalQueue>> getByDoctorId(@PathVariable Long doctorId) {
        return ResultUtils.success(medicalQueueService.getByDoctorId(doctorId));
    }

    /**
     * 查询医生候诊队列详情（含学生信息）
     */
    @GetMapping("/doctor/{doctorId}/detail")
    public Result<List<QueueDetailVO>> getQueueDetailByDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) String queueDate,
            @RequestParam(required = false) String status) {
        return ResultUtils.success(medicalQueueService.getQueueDetailByDoctor(doctorId, queueDate, status));
    }

    /**
     * 删除候诊队列记录（学生已删除等无效数据）
     */
    @DeleteMapping("/{queueId}")
    public Result<Void> deleteQueueRecord(
            @PathVariable Long queueId,
            @RequestParam Long doctorId) {
        medicalQueueService.deleteQueueRecord(queueId, doctorId);
        return ResultUtils.success();
    }
}
