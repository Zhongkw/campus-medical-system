package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalSickNote;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.MedicalSickNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电子病假条控制器
 */
@RestController
@RequestMapping("/api/medical/sick-note")
public class MedicalSickNoteController extends BaseController<MedicalSickNote> {

    @Autowired
    private MedicalSickNoteService medicalSickNoteService;

    @Override
    protected IBaseService<MedicalSickNote> getService() {
        return medicalSickNoteService;
    }

    /**
     * 根据病假条编号查询病假条
     */
    @GetMapping("/no/{sickNoteNo}")
    public Result<MedicalSickNote> getBySickNoteNo(@PathVariable String sickNoteNo) {
        return ResultUtils.success(medicalSickNoteService.getBySickNoteNo(sickNoteNo));
    }

    /**
     * 根据用户ID查询病假条列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<MedicalSickNote>> getByUserId(@PathVariable Long userId) {
        return ResultUtils.success(medicalSickNoteService.getByUserId(userId));
    }

    /**
     * 根据医生ID查询病假条列表
     */
    @GetMapping("/doctor/{doctorId}")
    public Result<List<MedicalSickNote>> getByDoctorId(@PathVariable Long doctorId) {
        return ResultUtils.success(medicalSickNoteService.getByDoctorId(doctorId));
    }
}
