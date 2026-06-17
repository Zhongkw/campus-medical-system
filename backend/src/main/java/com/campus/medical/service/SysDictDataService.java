package com.campus.medical.service;

import com.campus.medical.entity.SysDictData;
import com.campus.medical.service.IBaseService;

import java.util.List;

/**
 * 字典数据服务接口
 */
public interface SysDictDataService extends IBaseService<SysDictData> {

    /**
     * 根据字典编码查询字典数据列表
     */
    List<SysDictData> getByDictCode(String dictCode);
}
