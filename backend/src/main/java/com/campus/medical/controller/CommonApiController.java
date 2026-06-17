package com.campus.medical.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalDoctor;
import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.entity.SysDictData;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalDoctorMapper;
import com.campus.medical.mapper.MedicalScheduleMapper;
import com.campus.medical.mapper.SysDictDataMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.vo.ScheduleDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局公用接口控制器
 */
@RestController
@RequestMapping("/api/common")
public class CommonApiController {

    private final SysUserMapper userMapper;
    private final SysDictDataMapper dictDataMapper;
    private final MedicalDepartmentMapper departmentMapper;
    private final MedicalDoctorMapper doctorMapper;
    private final MedicalScheduleMapper scheduleMapper;

    @Autowired
    public CommonApiController(SysUserMapper userMapper,
                               SysDictDataMapper dictDataMapper,
                               MedicalDepartmentMapper departmentMapper,
                               MedicalDoctorMapper doctorMapper,
                               MedicalScheduleMapper scheduleMapper) {
        this.userMapper = userMapper;
        this.dictDataMapper = dictDataMapper;
        this.departmentMapper = departmentMapper;
        this.doctorMapper = doctorMapper;
        this.scheduleMapper = scheduleMapper;
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/getUserInfo")
    public Result<Map<String, Object>> getUserInfo(@RequestParam Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            return ResultUtils.error("用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("roleId", user.getRoleId());
        userInfo.put("department", user.getDepartment());
        userInfo.put("className", user.getClassName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("status", user.getStatus());

        return ResultUtils.success(userInfo);
    }

    /**
     * 获取数据字典列表（按字典类型）
     */
    @GetMapping("/getDictData")
    public Result<List<SysDictData>> getDictData(@RequestParam String dictType) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType);
        wrapper.eq(SysDictData::getStatus, 1);
        wrapper.orderByAsc(SysDictData::getSort);
        List<SysDictData> dictDataList = dictDataMapper.selectList(wrapper);
        return ResultUtils.success(dictDataList);
    }

    /**
     * 获取科室列表
     */
    @GetMapping("/getDepartmentList")
    public Result<List<MedicalDepartment>> getDepartmentList(
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<MedicalDepartment> wrapper = new LambdaQueryWrapper<>();

        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalDepartment::getStatus, status);
        }

        wrapper.orderByAsc(MedicalDepartment::getSort);
        List<MedicalDepartment> departmentList = departmentMapper.selectList(wrapper);
        return ResultUtils.success(departmentList);
    }

    /**
     * 获取医生列表
     */
    @GetMapping("/getDoctorList")
    public Result<List<MedicalDoctor>> getDoctorList(
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String status) {
        LambdaQueryWrapper<MedicalDoctor> wrapper = new LambdaQueryWrapper<>();

        if (deptId != null) {
            wrapper.eq(MedicalDoctor::getDeptId, deptId);
        }

        wrapper.orderByAsc(MedicalDoctor::getTitle);
        List<MedicalDoctor> doctorList = doctorMapper.selectList(wrapper);
        return ResultUtils.success(doctorList);
    }

    /**
     * 获取医生排班列表
     */
    @GetMapping("/getScheduleList")
    public Result<List<MedicalSchedule>> getScheduleList(
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "deptName", required = false) String deptName,
            @RequestParam(value = "scheduleDate", required = false) String scheduleDate,
            @RequestParam(value = "visitDate", required = false) String visitDate) {
        LambdaQueryWrapper<MedicalSchedule> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(doctorId != null, MedicalSchedule::getDoctorId, doctorId);
        wrapper.eq(deptId != null, MedicalSchedule::getDeptId, deptId);

        if (deptName != null && !deptName.isEmpty()) {
            LambdaQueryWrapper<MedicalDepartment> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(MedicalDepartment::getDeptName, deptName);
            MedicalDepartment department = departmentMapper.selectOne(deptWrapper);
            if (department != null) {
                wrapper.eq(MedicalSchedule::getDeptId, department.getId());
            } else {
                return ResultUtils.success(new ArrayList<>());
            }
        }

        String finalDate = scheduleDate != null ? scheduleDate : visitDate;
        if (finalDate != null && !finalDate.isEmpty()) {
            if (!isValidDate(finalDate)) {
                return ResultUtils.error("日期格式错误，应为 yyyy-MM-dd");
            }
            wrapper.eq(MedicalSchedule::getScheduleDate, finalDate);
        }

        wrapper.eq(MedicalSchedule::getStatus, 1);
        wrapper.orderByAsc(MedicalSchedule::getScheduleDate, MedicalSchedule::getTimeSlot);

        List<MedicalSchedule> scheduleList = scheduleMapper.selectList(wrapper);
        return ResultUtils.success(scheduleList);
    }

    /**
     * 获取排班详情列表（含医生、科室信息，供学生预约挂号使用）
     */
    @GetMapping("/getScheduleDetailList")
    public Result<List<ScheduleDetailVO>> getScheduleDetailList(
            @RequestParam(value = "visitDate", required = false) String visitDate,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "deptName", required = false) String deptName,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "doctorId", required = false) Long doctorId,
            @RequestParam(value = "doctorName", required = false) String doctorName) {
        LambdaQueryWrapper<MedicalSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSchedule::getStatus, 1);

        if (visitDate != null && !visitDate.isEmpty()) {
            if (!isValidDate(visitDate)) {
                return ResultUtils.error("日期格式错误，应为 yyyy-MM-dd");
            }
            wrapper.eq(MedicalSchedule::getScheduleDate, visitDate);
        }
        if (startDate != null && !startDate.isEmpty()) {
            if (!isValidDate(startDate)) {
                return ResultUtils.error("开始日期格式错误，应为 yyyy-MM-dd");
            }
            wrapper.ge(MedicalSchedule::getScheduleDate, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            if (!isValidDate(endDate)) {
                return ResultUtils.error("结束日期格式错误，应为 yyyy-MM-dd");
            }
            wrapper.le(MedicalSchedule::getScheduleDate, endDate);
        }

        if (deptId != null) {
            wrapper.eq(MedicalSchedule::getDeptId, deptId);
        } else if (deptName != null && !deptName.isEmpty()) {
            LambdaQueryWrapper<MedicalDepartment> deptWrapper = new LambdaQueryWrapper<>();
            deptWrapper.eq(MedicalDepartment::getDeptName, deptName);
            MedicalDepartment department = departmentMapper.selectOne(deptWrapper);
            if (department != null) {
                wrapper.eq(MedicalSchedule::getDeptId, department.getId());
            } else {
                return ResultUtils.success(new ArrayList<>());
            }
        }

        if (doctorId != null) {
            wrapper.eq(MedicalSchedule::getDoctorId, doctorId);
        }

        wrapper.orderByAsc(MedicalSchedule::getScheduleDate, MedicalSchedule::getTimeSlot);
        List<MedicalSchedule> scheduleList = scheduleMapper.selectList(wrapper);

        List<ScheduleDetailVO> result = new ArrayList<>();
        for (MedicalSchedule schedule : scheduleList) {
            ScheduleDetailVO vo = buildScheduleDetailVO(schedule);
            if (doctorName != null && !doctorName.isEmpty()) {
                if (vo.getDoctorName() == null || !vo.getDoctorName().contains(doctorName)) {
                    continue;
                }
            }
            result.add(vo);
        }
        return ResultUtils.success(result);
    }

    private ScheduleDetailVO buildScheduleDetailVO(MedicalSchedule schedule) {
        ScheduleDetailVO vo = new ScheduleDetailVO();
        vo.setId(schedule.getId());
        vo.setDoctorId(schedule.getDoctorId());
        vo.setDeptId(schedule.getDeptId());
        vo.setVisitDate(schedule.getScheduleDate().toString());
        vo.setVisitTime(schedule.getTimeSlot());
        vo.setTotalNumber(schedule.getMaxNum());
        vo.setRemainNumber(schedule.getRemainNum());
        vo.setScheduleType(schedule.getScheduleType());
        vo.setStatus(schedule.getStatus());
        vo.setRegisterFee(10);

        MedicalDoctor doctor = doctorMapper.selectById(schedule.getDoctorId());
        if (doctor != null) {
            vo.setTitle(doctor.getTitle());
            SysUser doctorUser = userMapper.selectById(doctor.getUserId());
            if (doctorUser != null) {
                vo.setDoctorName(doctorUser.getRealName());
            }
        }

        MedicalDepartment dept = departmentMapper.selectById(schedule.getDeptId());
        if (dept != null) {
            vo.setDepartment(dept.getDeptName());
        }
        return vo;
    }

    /**
     * 根据系统用户ID获取医生档案ID
     */
    @GetMapping("/getDoctorIdByUserId")
    public Result<Long> getDoctorIdByUserId(@RequestParam Long userId) {
        LambdaQueryWrapper<MedicalDoctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalDoctor::getUserId, userId);
        MedicalDoctor doctor = doctorMapper.selectOne(wrapper);
        if (doctor == null) {
            return ResultUtils.error("医生档案不存在");
        }
        return ResultUtils.success(doctor.getId());
    }

    @GetMapping("/getDoctorProfile")
    public Result<Map<String, Object>> getDoctorProfile(@RequestParam Long userId) {
        LambdaQueryWrapper<MedicalDoctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalDoctor::getUserId, userId);
        MedicalDoctor doctor = doctorMapper.selectOne(wrapper);
        if (doctor == null) {
            return ResultUtils.error("医生档案不存在");
        }
        Map<String, Object> profile = new HashMap<>();
        profile.put("doctorId", doctor.getId());
        profile.put("deptId", doctor.getDeptId());
        profile.put("title", doctor.getTitle());
        SysUser user = userMapper.selectById(doctor.getUserId());
        if (user != null) {
            profile.put("doctorName", user.getRealName());
        }
        return ResultUtils.success(profile);
    }

    /**
     * 验证日期格式是否合法
     */
    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * 保存业务表单数据（通用接口 - 预留）
     * TODO: 根据具体业务需求实现
     */
    @PostMapping("/saveFormData")
    public Result<Boolean> saveFormData(@RequestBody Map<String, Object> formData) {
        // TODO: 实现通用表单数据保存逻辑
        return ResultUtils.success(true);
    }

    /**
     * 获取业务表单数据（通用接口 - 预留）
     * TODO: 根据具体业务需求实现
     */
    @GetMapping("/getFormData")
    public Result<Map<String, Object>> getFormData(
            @RequestParam String formType,
            @RequestParam Long formId) {
        // TODO: 实现通用表单数据获取逻辑
        return ResultUtils.success(new HashMap<>());
    }
}
