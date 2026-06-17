package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysDictType;
import com.campus.medical.mapper.SysDictTypeMapper;
import com.campus.medical.service.SysDictTypeService;
import org.springframework.stereotype.Service;

/**
 * 字典类型服务实现类
 */
@Service
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    /**
     * 根据字典编码查询字典类型
     */
    @Override
    public SysDictType getByDictCode(String dictCode) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictCode);
        return baseMapper.selectOne(wrapper);
    }
}
