package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pay")
public class PayController {
    
    /**
     * 统一支付接口
     */
    @PostMapping("/unifiedPay")
    public Result<Map<String, Object>> unifiedPay(@RequestBody Map<String, Object> requestBody) {
        String orderNo = (String) requestBody.get("orderNo");
        String payMethod = (String) requestBody.get("payMethod");
        
        log.info("统一支付请求: orderNo={}, payMethod={}", orderNo, payMethod);
        
        // 模拟支付处理
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("payOrderNo", "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        result.put("payMethod", payMethod);
        result.put("payStatus", "SUCCESS");
        result.put("payTime", System.currentTimeMillis());
        result.put("message", "支付成功");
        
        return ResultUtils.success("支付成功", result);
    }
}