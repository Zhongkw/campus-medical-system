package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalOrderItem;
import com.campus.medical.mapper.MedicalOrderItemMapper;
import com.campus.medical.service.MedicalOrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单明细服务实现类
 */
@Service
public class MedicalOrderItemServiceImpl extends BaseServiceImpl<MedicalOrderItemMapper, MedicalOrderItem> implements MedicalOrderItemService {

    /**
     * 根据订单ID查询订单明细列表
     */
    @Override
    public List<MedicalOrderItem> getByOrderId(Long orderId) {
        LambdaQueryWrapper<MedicalOrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrderItem::getOrderId, orderId);
        return baseMapper.selectList(wrapper);
    }
}
