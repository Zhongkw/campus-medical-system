package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.service.SysIndexService;
import com.campus.medical.vo.SysIndexStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统首页控制器
 */
@RestController
@RequestMapping("/api/sys/index")
public class SysIndexController {
    
    @Autowired
    private SysIndexService sysIndexService;
    
    /**
     * 获取首页统计数据
     */
    @GetMapping("/stat")
    public Result<SysIndexStatVO> getStatData() {
        SysIndexStatVO result = sysIndexService.getIndexStatData();
        return ResultUtils.success(result);
    }
}
