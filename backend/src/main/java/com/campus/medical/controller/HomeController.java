package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/home")
public class HomeController {
    
    /**
     * 获取待办事项列表
     */
    @PostMapping("/todoList")
    public Result<List<Map<String, Object>>> getTodoList() {
        log.info("获取待办事项列表");
        
        List<Map<String, Object>> todoList = new ArrayList<>();
        
        Map<String, Object> todo1 = new HashMap<>();
        todo1.put("id", 1);
        todo1.put("title", "审核学生病假申请");
        todo1.put("count", 3);
        todo1.put("type", "approval");
        todoList.add(todo1);
        
        Map<String, Object> todo2 = new HashMap<>();
        todo2.put("id", 2);
        todo2.put("title", "待处理挂号预约");
        todo2.put("count", 5);
        todo2.put("type", "appointment");
        todoList.add(todo2);
        
        Map<String, Object> todo3 = new HashMap<>();
        todo3.put("id", 3);
        todo3.put("title", "待缴费订单");
        todo3.put("count", 2);
        todo3.put("type", "payment");
        todoList.add(todo3);
        
        return ResultUtils.success("获取成功", todoList);
    }
}