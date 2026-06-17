package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysDictData;
import com.campus.medical.mapper.SysDictDataMapper;
import com.campus.medical.service.SysDictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据服务实现类
 */
@Service
public class SysDictDataServiceImpl extends BaseServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    /**
     * 根据字典编码查询字典数据列表
     */
    @Override
    public List<SysDictData> getByDictCode(String dictCode) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictCode);
        wrapper.orderByAsc(SysDictData::getSort);
        return baseMapper.selectList(wrapper);
    }
}
