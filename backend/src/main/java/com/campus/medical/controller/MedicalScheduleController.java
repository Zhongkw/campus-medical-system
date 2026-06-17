package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 排班控制器
 */
@RestController
@RequestMapping("/api/medical/schedule")
public class MedicalScheduleController extends BaseController<MedicalSchedule> {

    @Autowired
    private MedicalScheduleService medicalScheduleService;

    @Override
    protected IBaseService<MedicalSchedule> getService() {
        return medicalScheduleService;
    }

    /**
     * 根据医生ID查询排班列表
     */
    @GetMapping("/doctor/{doctorId}")
    public List<MedicalSchedule> getByDoctorId(@PathVariable Long doctorId) {
        return medicalScheduleService.getByDoctorId(doctorId);
    }

    /**
     * 根据日期查询排班列表
     */
    @GetMapping("/date/{scheduleDate}")
    public List<MedicalSchedule> getByScheduleDate(@PathVariable String scheduleDate) {
        return medicalScheduleService.getByScheduleDate(scheduleDate);
    }

    @PostMapping("/create")
    public Result<MedicalSchedule> createSchedule(@RequestBody MedicalSchedule schedule) {
        return ResultUtils.success("创建成功", medicalScheduleService.createSchedule(schedule));
    }

    @PutMapping("/update-slots")
    public Result<MedicalSchedule> updateScheduleSlots(@RequestBody MedicalSchedule schedule) {
        return ResultUtils.success("更新成功", medicalScheduleService.updateScheduleSlots(schedule));
    }
}
