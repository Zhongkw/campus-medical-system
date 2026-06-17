package com.campus.medical.service;

import com.campus.medical.dto.OrderQueryDTO;
import com.campus.medical.vo.FinanceDashboardVO;
import com.campus.medical.vo.FinanceOrderDetailVO;
import com.campus.medical.vo.FinanceOrderVO;
import com.campus.medical.vo.PageResultVO;

/**
 * 财务订单服务接口
 */
public interface FinanceOrderService {
    
    /**
     * 分页查询财务订单
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResultVO<FinanceOrderVO> pageQueryFinanceOrder(OrderQueryDTO queryDTO);
    
    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    FinanceOrderDetailVO getOrderDetail(Long orderId);
    
    /**
     * 处理退费审核
     * @param orderId 订单ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param auditorId 审核人ID
     * @return 是否成功
     */
    Boolean handleRefundAudit(Long orderId, String auditStatus, String auditRemark, Long auditorId);
    
    /**
     * 导出订单Excel
     * @param queryDTO 查询条件
     * @return 文件路径
     */
    String exportOrderExcel(OrderQueryDTO queryDTO);
    
    /**
     * 获取财务看板数据
     * @return 财务看板数据
     */
    FinanceDashboardVO getFinanceDashboardData();
}
