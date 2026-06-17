package com.campus.medical.service;

import com.campus.medical.dto.OrderPayRequestDTO;
import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.vo.FinanceDashboardVO;
import com.campus.medical.vo.OrderDetailVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 订单支付业务服务接口
 */
public interface OrderBusinessService {

    /**
     * 查询用户订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 订单列表
     */
    List<MedicalOrder> getUserOrders(Long userId, String status, LocalDate startDate, LocalDate endDate);

    /**
     * 查询订单详情（含明细和发票）
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId);

    /**
     * 支付订单（核心业务方法 - Mock支付）
     * @param requestDTO 支付请求
     * @return 支付结果
     */
    Boolean payOrder(OrderPayRequestDTO requestDTO);

    /**
     * 生成电子发票
     * @param orderId 订单ID
     * @return 发票编号
     */
    String generateInvoice(Long orderId);

    /**
     * 查询用户发票列表
     * @param userId 用户ID
     * @return 发票列表
     */
    List<OrderDetailVO.InvoiceVO> getUserInvoices(Long userId);

    /**
     * 财务统计面板数据
     * @return 统计数据
     */
    FinanceDashboardVO getFinanceDashboard();

    /**
     * 退费审核（财务端）
     * @param orderId 订单ID
     * @param auditStatus 审核状态(PASS/REJECT)
     * @param auditRemark 审核备注
     * @param auditorId 审核人ID
     * @return 审核结果
     */
    Boolean auditRefund(Long orderId, String auditStatus, String auditRemark, Long auditorId);
}
