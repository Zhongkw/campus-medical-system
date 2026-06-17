package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalInstockItem;
import com.campus.medical.mapper.MedicalInstockItemMapper;
import com.campus.medical.service.MedicalInstockItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入库单明细服务实现类
 */
@Service
public class MedicalInstockItemServiceImpl extends BaseServiceImpl<MedicalInstockItemMapper, MedicalInstockItem> implements MedicalInstockItemService {

    /**
     * 根据入库单ID查询入库单明细列表
     */
    @Override
    public List<MedicalInstockItem> getByInstockId(Long instockId) {
        LambdaQueryWrapper<MedicalInstockItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInstockItem::getInstockId, instockId);
        return baseMapper.selectList(wrapper);
    }
}
