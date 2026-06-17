package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.mapper.MedicalScheduleMapper;
import com.campus.medical.service.MedicalScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 排班服务实现类
 */
@Service
public class MedicalScheduleServiceImpl extends BaseServiceImpl<MedicalScheduleMapper, MedicalSchedule> implements MedicalScheduleService {

    /**
     * 根据医生ID查询排班列表
     */
    @Override
    public List<MedicalSchedule> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSchedule::getDoctorId, doctorId);
        wrapper.orderByAsc(MedicalSchedule::getScheduleDate);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据日期查询排班列表
     */
    @Override
    public List<MedicalSchedule> getByScheduleDate(String scheduleDate) {
        LambdaQueryWrapper<MedicalSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSchedule::getScheduleDate, scheduleDate);
        return baseMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicalSchedule createSchedule(MedicalSchedule schedule) {
        if (schedule.getDoctorId() == null) {
            throw new RuntimeException("医生ID不能为空");
        }
        if (schedule.getDeptId() == null) {
            throw new RuntimeException("科室ID不能为空");
        }
        if (schedule.getScheduleDate() == null) {
            throw new RuntimeException("排班日期不能为空");
        }
        if (schedule.getTimeSlot() == null || schedule.getTimeSlot().isBlank()) {
            throw new RuntimeException("时段不能为空");
        }
        if (schedule.getMaxNum() == null || schedule.getMaxNum() <= 0) {
            throw new RuntimeException("总号源必须大于0");
        }
        LambdaQueryWrapper<MedicalSchedule> dupWrapper = new LambdaQueryWrapper<>();
        dupWrapper.eq(MedicalSchedule::getDoctorId, schedule.getDoctorId())
                .eq(MedicalSchedule::getScheduleDate, schedule.getScheduleDate())
                .eq(MedicalSchedule::getTimeSlot, schedule.getTimeSlot());
        if (baseMapper.selectCount(dupWrapper) > 0) {
            throw new RuntimeException("该时段排班已存在");
        }
        if (schedule.getRemainNum() == null) {
            schedule.setRemainNum(schedule.getMaxNum());
        }
        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }
        if (schedule.getScheduleType() == null) {
            schedule.setScheduleType("普通");
        }
        baseMapper.insert(schedule);
        return schedule;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MedicalSchedule updateScheduleSlots(MedicalSchedule schedule) {
        if (schedule.getId() == null) {
            throw new RuntimeException("排班ID不能为空");
        }
        MedicalSchedule existing = baseMapper.selectById(schedule.getId());
        if (existing == null) {
            throw new RuntimeException("排班不存在");
        }
        int used = Math.max(0, existing.getMaxNum() - existing.getRemainNum());
        int newMax = schedule.getMaxNum() != null ? schedule.getMaxNum() : existing.getMaxNum();
        if (newMax < used) {
            throw new RuntimeException("总号源不能小于已用号源(" + used + ")");
        }
        existing.setMaxNum(newMax);
        existing.setRemainNum(newMax - used);
        if (schedule.getScheduleDate() != null) {
            existing.setScheduleDate(schedule.getScheduleDate());
        }
        if (schedule.getTimeSlot() != null && !schedule.getTimeSlot().isBlank()) {
            existing.setTimeSlot(schedule.getTimeSlot());
        }
        baseMapper.updateById(existing);
        return existing;
    }
}
