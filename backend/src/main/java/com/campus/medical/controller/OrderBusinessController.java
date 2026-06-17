package com.campus.medical.controller;

import com.campus.medical.common.Result;
import com.campus.medical.common.ResultUtils;
import com.campus.medical.dto.OrderPayRequestDTO;
import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.service.OrderBusinessService;
import com.campus.medical.vo.FinanceDashboardVO;
import com.campus.medical.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 订单支付业务控制器
 */
@RestController
@RequestMapping("/api/medical/order-business")
public class OrderBusinessController {

    @Autowired
    private OrderBusinessService orderBusinessService;

    /**
     * 查询用户订单列表
     */
    @GetMapping("/list")
    public Result<List<MedicalOrder>> getUserOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
        
        List<MedicalOrder> orders = orderBusinessService.getUserOrders(userId, status, start, end);
        return ResultUtils.success(orders);
    }

    /**
     * 查询订单详情（含明细和发票）
     */
    @GetMapping("/detail/{orderId}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        OrderDetailVO detail = orderBusinessService.getOrderDetail(orderId);
        return ResultUtils.success(detail);
    }

    /**
     * 支付订单（核心接口 - Mock支付）
     */
    @PostMapping("/pay")
    public Result<Boolean> payOrder(@RequestBody OrderPayRequestDTO requestDTO) {
        Boolean result = orderBusinessService.payOrder(requestDTO);
        return ResultUtils.success("支付成功", result);
    }

    /**
     * 生成电子发票
     */
    @PostMapping("/invoice/generate/{orderId}")
    public Result<String> generateInvoice(@PathVariable Long orderId) {
        String invoiceNo = orderBusinessService.generateInvoice(orderId);
        return ResultUtils.success("发票生成成功", invoiceNo);
    }

    /**
     * 查询用户发票列表
     */
    @GetMapping("/invoice/list")
    public Result<List<OrderDetailVO.InvoiceVO>> getUserInvoices(@RequestParam Long userId) {
        List<OrderDetailVO.InvoiceVO> invoices = orderBusinessService.getUserInvoices(userId);
        return ResultUtils.success(invoices);
    }

    /**
     * 财务统计面板数据（财务端）
     */
    @GetMapping("/finance/dashboard")
    public Result<FinanceDashboardVO> getFinanceDashboard() {
        FinanceDashboardVO dashboard = orderBusinessService.getFinanceDashboard();
        return ResultUtils.success(dashboard);
    }

    /**
     * 退费审核（财务端）
     */
    @PostMapping("/finance/audit-refund")
    public Result<Boolean> auditRefund(
            @RequestParam Long orderId,
            @RequestParam String auditStatus,
            @RequestParam(required = false) String auditRemark,
            @RequestParam Long auditorId) {
        
        Boolean result = orderBusinessService.auditRefund(orderId, auditStatus, auditRemark, auditorId);
        return ResultUtils.success("审核完成", result);
    }
}
