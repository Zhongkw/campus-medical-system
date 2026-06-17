package com.campus.medical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 基础Service接口
 * 提供通用CRUD方法
 */
public interface IBaseService<T> {

    /**
     * 根据ID查询
     */
    T getById(Long id);

    /**
     * 查询列表
     */
    List<T> list();

    /**
     * 分页查询
     */
    IPage<T> page(Page<T> page);

    /**
     * 保存
     */
    boolean save(T entity);

    /**
     * 更新
     */
    boolean updateById(T entity);

    /**
     * 删除
     */
    boolean removeById(Long id);
}
