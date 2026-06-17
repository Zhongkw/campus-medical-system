package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.mapper.MedicalOrderMapper;
import com.campus.medical.service.MedicalOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class MedicalOrderServiceImpl extends BaseServiceImpl<MedicalOrderMapper, MedicalOrder> implements MedicalOrderService {

    /**
     * 根据用户ID查询订单列表
     */
    @Override
    public List<MedicalOrder> getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getUserId, userId);
        wrapper.orderByDesc(MedicalOrder::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据订单编号查询订单
     */
    @Override
    public MedicalOrder getByOrderNo(String orderNo) {
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getOrderNo, orderNo);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据状态查询订单列表
     */
    @Override
    public List<MedicalOrder> getByStatus(String status) {
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getStatus, status);
        wrapper.orderByDesc(MedicalOrder::getCreateTime);
        return baseMapper.selectList(wrapper);
    }
}
