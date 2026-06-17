package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalSickLeave;
import com.campus.medical.mapper.MedicalSickLeaveMapper;
import com.campus.medical.service.MedicalSickLeaveService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 电子病假申请服务实现类
 */
@Service
public class MedicalSickLeaveServiceImpl extends BaseServiceImpl<MedicalSickLeaveMapper, MedicalSickLeave> implements MedicalSickLeaveService {

    /**
     * 根据病假申请编号查询病假申请
     */
    @Override
    public MedicalSickLeave getByLeaveNo(String leaveNo) {
        LambdaQueryWrapper<MedicalSickLeave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSickLeave::getLeaveNo, leaveNo);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据用户ID查询病假申请列表
     */
    @Override
    public List<MedicalSickLeave> getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalSickLeave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSickLeave::getUserId, userId);
        wrapper.orderByDesc(MedicalSickLeave::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据状态查询病假申请列表
     */
    @Override
    public List<MedicalSickLeave> getByStatus(String status) {
        LambdaQueryWrapper<MedicalSickLeave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSickLeave::getStatus, status);
        wrapper.orderByDesc(MedicalSickLeave::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}
