package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalSickNote;
import com.campus.medical.mapper.MedicalSickNoteMapper;
import com.campus.medical.service.MedicalSickNoteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 电子病假条服务实现类
 */
@Service
public class MedicalSickNoteServiceImpl extends BaseServiceImpl<MedicalSickNoteMapper, MedicalSickNote> implements MedicalSickNoteService {

    /**
     * 根据病假条编号查询病假条
     */
    @Override
    public MedicalSickNote getBySickNoteNo(String sickNoteNo) {
        LambdaQueryWrapper<MedicalSickNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSickNote::getSickNoteNo, sickNoteNo);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据用户ID查询病假条列表
     */
    @Override
    public List<MedicalSickNote> getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalSickNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSickNote::getUserId, userId);
        wrapper.orderByDesc(MedicalSickNote::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据医生ID查询病假条列表
     */
    @Override
    public List<MedicalSickNote> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalSickNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSickNote::getDoctorId, doctorId);
        wrapper.orderByDesc(MedicalSickNote::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}
