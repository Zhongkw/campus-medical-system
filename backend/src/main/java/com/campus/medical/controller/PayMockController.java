package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付Mock控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/mock/pay")
public class PayMockController {
    
    /**
     * 模拟创建支付订单
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createPayOrder(
            @RequestParam String orderNo,
            @RequestParam BigDecimal amount,
            @RequestParam Integer payType) {
        log.info("创建支付订单: orderNo={}, amount={}, payType={}", orderNo, amount, payType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("payOrderNo", "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        result.put("orderNo", orderNo);
        result.put("amount", amount);
        result.put("payType", payType);
        result.put("status", "SUCCESS");
        result.put("payTime", System.currentTimeMillis());
        
        return ResultUtils.success(result);
    }
    
    /**
     * 模拟查询支付结果
     */
    @PostMapping("/query")
    public Result<Map<String, Object>> queryPayResult(@RequestParam String payOrderNo) {
        log.info("查询支付结果: payOrderNo={}", payOrderNo);
        
        Map<String, Object> result = new HashMap<>();
        result.put("payOrderNo", payOrderNo);
        result.put("status", "SUCCESS");
        result.put("payTime", System.currentTimeMillis());
        result.put("message", "支付成功");
        
        return ResultUtils.success(result);
    }
    
    /**
     * 模拟退费
     */
    @PostMapping("/refund")
    public Result<Map<String, Object>> refund(
            @RequestParam String payOrderNo,
            @RequestParam BigDecimal amount) {
        log.info("模拟退费: payOrderNo={}, amount={}", payOrderNo, amount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("refundNo", "REF" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        result.put("payOrderNo", payOrderNo);
        result.put("amount", amount);
        result.put("status", "SUCCESS");
        result.put("refundTime", System.currentTimeMillis());
        
        return ResultUtils.success(result);
    }
}
