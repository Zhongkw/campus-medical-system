package com.campus.medical.controller;

import com.campus.medical.entity.SysDictData;
import com.campus.medical.service.IBaseService;
import com.campus.medical.service.SysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 */
@RestController
@RequestMapping("/api/system/dict-data")
public class SysDictDataController extends BaseController<SysDictData> {

    @Autowired
    private SysDictDataService sysDictDataService;

    @Override
    protected IBaseService<SysDictData> getService() {
        return sysDictDataService;
    }

    /**
     * 根据字典编码查询字典数据列表
     */
    @GetMapping("/code/{dictCode}")
    public List<SysDictData> getByDictCode(@PathVariable String dictCode) {
        return sysDictDataService.getByDictCode(dictCode);
    }
}
