package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalVaccine;
import com.campus.medical.mapper.MedicalVaccineMapper;
import com.campus.medical.service.MedicalVaccineService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 疫苗接种记录服务实现类
 */
@Service
public class MedicalVaccineServiceImpl extends BaseServiceImpl<MedicalVaccineMapper, MedicalVaccine> implements MedicalVaccineService {

    /**
     * 根据用户ID查询疫苗接种记录列表
     */
    @Override
    public List<MedicalVaccine> getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalVaccine> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalVaccine::getUserId, userId);
        wrapper.orderByDesc(MedicalVaccine::getVaccineDate);
        return baseMapper.selectList(wrapper);
    }
}
