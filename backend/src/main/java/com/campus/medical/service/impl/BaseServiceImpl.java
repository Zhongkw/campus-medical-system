package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.medical.service.IBaseService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 基础Service实现类
 * 提供通用CRUD方法实现
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> implements IBaseService<T> {

    @Autowired
    protected M baseMapper;

    @Override
    public T getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<T> list() {
        return baseMapper.selectList(null);
    }

    @Override
    public IPage<T> page(Page<T> page) {
        return baseMapper.selectPage(page, null);
    }

    @Override
    public boolean save(T entity) {
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public boolean updateById(T entity) {
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return baseMapper.deleteById(id) > 0;
    }
}
