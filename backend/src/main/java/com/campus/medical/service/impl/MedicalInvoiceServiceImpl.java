package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalInvoice;
import com.campus.medical.mapper.MedicalInvoiceMapper;
import com.campus.medical.service.MedicalInvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 发票服务实现类
 */
@Service
public class MedicalInvoiceServiceImpl extends BaseServiceImpl<MedicalInvoiceMapper, MedicalInvoice> implements MedicalInvoiceService {

    /**
     * 根据发票编号查询发票
     */
    @Override
    public MedicalInvoice getByInvoiceNo(String invoiceNo) {
        LambdaQueryWrapper<MedicalInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInvoice::getInvoiceNo, invoiceNo);
        return baseMapper.selectOne(wrapper);
    }

    /**
     * 根据用户ID查询发票列表
     */
    @Override
    public List<MedicalInvoice> getByUserId(Long userId) {
        LambdaQueryWrapper<MedicalInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInvoice::getUserId, userId);
        wrapper.orderByDesc(MedicalInvoice::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据订单ID查询发票
     */
    @Override
    public MedicalInvoice getByOrderId(Long orderId) {
        LambdaQueryWrapper<MedicalInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInvoice::getOrderId, orderId);
        return baseMapper.selectOne(wrapper);
    }
}
