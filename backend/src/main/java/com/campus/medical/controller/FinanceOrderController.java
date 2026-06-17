package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.OrderQueryDTO;
import com.campus.medical.service.FinanceOrderService;
import com.campus.medical.utils.ThreadLocalUtils;
import com.campus.medical.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 财务订单控制器
 */
@RestController
@RequestMapping("/api/finance/order")
public class FinanceOrderController {
    
    @Autowired
    private FinanceOrderService financeOrderService;
    
    /**
     * 分页查询财务订单
     */
    @GetMapping("/pageList")
    public Result<PageResultVO<FinanceOrderVO>> pageList(OrderQueryDTO queryDTO) {
        PageResultVO<FinanceOrderVO> result = financeOrderService.pageQueryFinanceOrder(queryDTO);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public Result<FinanceOrderDetailVO> getDetail(@PathVariable Long orderId) {
        FinanceOrderDetailVO detail = financeOrderService.getOrderDetail(orderId);
        return ResultUtils.success(detail);
    }
    
    /**
     * 处理退费审核
     */
    @PostMapping("/auditRefund")
    public Result<Boolean> auditRefund(
            @RequestParam Long orderId,
            @RequestParam String auditStatus,
            @RequestParam(required = false) String auditRemark) {
        Map<String, Object> currentUser = ThreadLocalUtils.get();
        Long auditorId = Long.parseLong(String.valueOf(currentUser.get("userId")));
        Boolean result = financeOrderService.handleRefundAudit(orderId, auditStatus, auditRemark, auditorId);
        return ResultUtils.success(result);
    }
    
    /**
     * 导出订单Excel
     */
    @GetMapping("/export")
    public Result<String> exportExcel(OrderQueryDTO queryDTO) {
        String filePath = financeOrderService.exportOrderExcel(queryDTO);
        return ResultUtils.success(filePath);
    }
    
    /**
     * 获取财务看板数据
     */
    @GetMapping("/dashboard")
    public Result<FinanceDashboardVO> getDashboard() {
        FinanceDashboardVO dashboard = financeOrderService.getFinanceDashboardData();
        return ResultUtils.success(dashboard);
    }
}
