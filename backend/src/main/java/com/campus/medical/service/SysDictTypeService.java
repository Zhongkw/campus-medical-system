package com.campus.medical.service;

import com.campus.medical.entity.SysDictType;
import com.campus.medical.service.IBaseService;

/**
 * 字典类型服务接口
 */
public interface SysDictTypeService extends IBaseService<SysDictType> {

    /**
     * 根据字典编码查询字典类型
     */
    SysDictType getByDictCode(String dictCode);
}
