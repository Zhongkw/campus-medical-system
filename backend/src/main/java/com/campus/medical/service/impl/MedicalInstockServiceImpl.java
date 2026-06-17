package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalInstock;
import com.campus.medical.mapper.MedicalInstockMapper;
import com.campus.medical.service.MedicalInstockService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入库单服务实现类
 */
@Service
public class MedicalInstockServiceImpl extends BaseServiceImpl<MedicalInstockMapper, MedicalInstock> implements MedicalInstockService {

    /**
     * 根据入库单号查询入库单
     */
    @Override
    public MedicalInstock getByInstockNo(String instockNo) {
        LambdaQueryWrapper<MedicalInstock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInstock::getInstockNo, instockNo);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据状态查询入库单列表
     */
    @Override
    public List<MedicalInstock> getByStatus(Integer status) {
        // MedicalInstock实体类没有status字段，返回空列表
        return List.of();
    }
}
