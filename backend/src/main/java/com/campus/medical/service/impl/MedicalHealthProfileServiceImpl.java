package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalHealthProfile;
import com.campus.medical.mapper.MedicalHealthProfileMapper;
import com.campus.medical.service.MedicalHealthProfileService;
import org.springframework.stereotype.Service;

/**
 * 个人健康档案服务实现类
 */
@Service
public class MedicalHealthProfileServiceImpl extends BaseServiceImpl<MedicalHealthProfileMapper, MedicalHealthProfile> implements MedicalHealthProfileService {

    /**
     * 根据用户ID查询健康档案
     */
    @Override
    public MedicalHealthProfile getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalHealthProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalHealthProfile::getUserId, userId);
        return baseMapper.selectOne(wrapper);
    }
}
