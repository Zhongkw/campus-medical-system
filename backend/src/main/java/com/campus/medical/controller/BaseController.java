package com.campus.medical.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.IBaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 基础Controller
 * 提供通用CRUD接口
 */
public abstract class BaseController<T> {

    /**
     * 获取Service实例
     */
    protected abstract IBaseService<T> getService();

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<T> getById(@PathVariable Long id) {
        T entity = getService().getById(id);
        if (entity != null) {
            return ResultUtils.success(entity);
        } else {
            return ResultUtils.error("数据不存在");
        }
    }

    /**
     * 查询列表
     */
    @GetMapping("/list")
    public Result<List<T>> list() {
        List<T> list = getService().list();
        return ResultUtils.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public Result<IPage<T>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        IPage<T> result = getService().page(page);
        return ResultUtils.success(result);
    }

    /**
     * 保存
     */
    @PostMapping
    public Result<Void> save(@RequestBody T entity) {
        boolean success = getService().save(entity);
        if (success) {
            return ResultUtils.success("保存成功", null);
        } else {
            return ResultUtils.error("保存失败");
        }
    }

    /**
     * 更新
     */
    @PutMapping
    public Result<Void> updateById(@RequestBody T entity) {
        boolean success = getService().updateById(entity);
        if (success) {
            return ResultUtils.success("更新成功", null);
        } else {
            return ResultUtils.error("更新失败");
        }
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> removeById(@PathVariable Long id) {
        boolean success = getService().removeById(id);
        if (success) {
            return ResultUtils.success("删除成功", null);
        } else {
            return ResultUtils.error("删除失败");
        }
    }
}
