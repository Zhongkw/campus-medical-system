package com.campus.medical.controller;

import com.campus.medical.entity.SysDictType;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 字典类型控制器
 */
@RestController
@RequestMapping("/api/system/dict-type")
public class SysDictTypeController extends BaseController<SysDictType> {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @Override
    protected IBaseService<SysDictType> getService() {
        return sysDictTypeService;
    }

    /**
     * 根据字典编码查询字典类型
     */
    @GetMapping("/code/{dictCode}")
    public SysDictType getByDictCode(@PathVariable String dictCode) {
        return sysDictTypeService.getByDictCode(dictCode);
    }
}
