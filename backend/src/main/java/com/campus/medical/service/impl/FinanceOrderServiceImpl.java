package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.medical.dto.OrderQueryDTO;
import com.campus.medical.entity.MedicalApproval;
import com.campus.medical.entity.MedicalInvoice;
import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.entity.MedicalOrderItem;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalApprovalMapper;
import com.campus.medical.mapper.MedicalInvoiceMapper;
import com.campus.medical.mapper.MedicalOrderItemMapper;
import com.campus.medical.mapper.MedicalOrderMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.FinanceOrderService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 财务订单服务实现类
 */
@Slf4j
@Service
public class FinanceOrderServiceImpl implements FinanceOrderService {
    
    @Autowired
    private MedicalOrderMapper medicalOrderMapper;
    
    @Autowired
    private MedicalOrderItemMapper medicalOrderItemMapper;
    
    @Autowired
    private MedicalInvoiceMapper medicalInvoiceMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MedicalApprovalMapper medicalApprovalMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public PageResultVO<FinanceOrderVO> pageQueryFinanceOrder(OrderQueryDTO queryDTO) {
        Page<MedicalOrder> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<MedicalOrder> wrapper = buildOrderQueryWrapper(queryDTO);
        wrapper.orderByDesc(MedicalOrder::getCreateTime);

        IPage<MedicalOrder> orderPage = medicalOrderMapper.selectPage(page, wrapper);
        List<FinanceOrderVO> voList = orderPage.getRecords().stream()
                .map(this::toFinanceOrderVO)
                .collect(Collectors.toList());

        return new PageResultVO<>(voList, orderPage.getTotal(), orderPage.getPages(),
                orderPage.getCurrent(), orderPage.getSize());
    }
    
    @Override
    public FinanceOrderDetailVO getOrderDetail(Long orderId) {
        MedicalOrder order = medicalOrderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        
        FinanceOrderDetailVO vo = new FinanceOrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        
        // 获取关联用户信息
        SysUser user = sysUserMapper.selectById(order.getUserId());
        vo.setStudentName(user != null ? user.getRealName() : "未知");
        vo.setStudentNo(user != null ? user.getUsername() : "未知");
        vo.setContactPhone(user != null ? user.getPhone() : "未知");
        
        vo.setAmount(order.getTotalAmount());
        vo.setPayType(mapPayTypeCode(order.getPayType()));
        vo.setPayTypeName(displayPayType(order.getPayType()));
        vo.setStatus(mapStatusCode(order.getStatus()));
        vo.setStatusName(displayStatusName(order.getStatus()));
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        
        // 查询订单项目
        LambdaQueryWrapper<MedicalOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(MedicalOrderItem::getOrderId, orderId);
        List<MedicalOrderItem> items = medicalOrderItemMapper.selectList(itemWrapper);
        
        List<FinanceOrderDetailVO.OrderItemVO> itemVOList = items.stream().map(item -> {
            FinanceOrderDetailVO.OrderItemVO itemVO = new FinanceOrderDetailVO.OrderItemVO();
            itemVO.setItemName(item.getItemName());
            itemVO.setAmount(item.getAmount());
            // MedicalOrderItem表中没有quantity和unitPrice字段，使用默认值
            itemVO.setQuantity(1);
            itemVO.setPrice(item.getAmount());
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOList);
        
        // 查询发票
        LambdaQueryWrapper<MedicalInvoice> invoiceWrapper = new LambdaQueryWrapper<>();
        invoiceWrapper.eq(MedicalInvoice::getOrderId, orderId);
        MedicalInvoice invoice = medicalInvoiceMapper.selectOne(invoiceWrapper);
        if (invoice != null) {
            FinanceOrderDetailVO.InvoiceVO invoiceVO = new FinanceOrderDetailVO.InvoiceVO();
            invoiceVO.setInvoiceNo(invoice.getInvoiceNo());
            invoiceVO.setInvoiceUrl(invoice.getInvoiceUrl());
            invoiceVO.setCreateTime(invoice.getCreateTime());
            vo.setInvoice(invoiceVO);
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleRefundAudit(Long orderId, String auditStatus, String auditRemark, Long auditorId) {
        MedicalOrder order = medicalOrderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }

        if ("3".equals(auditStatus)) {
            if (!"已支付".equals(order.getStatus())) {
                throw new RuntimeException("仅已缴费订单可退费");
            }
            order.setStatus("已退费");
            order.setUpdateTime(java.time.LocalDateTime.now());
            medicalOrderMapper.updateById(order);
            completePendingRefundApproval(orderId, auditorId, auditRemark);
            return true;
        }
        return true;
    }

    private void completePendingRefundApproval(Long orderId, Long auditorId, String remark) {
        LambdaQueryWrapper<MedicalApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalApproval::getApprovalType, "退费");
        wrapper.eq(MedicalApproval::getBusinessId, orderId);
        wrapper.eq(MedicalApproval::getStatus, "待审核");
        List<MedicalApproval> approvals = medicalApprovalMapper.selectList(wrapper);
        for (MedicalApproval approval : approvals) {
            approval.setStatus("已通过");
            approval.setAuditorId(auditorId);
            approval.setAuditContent(remark);
            approval.setAuditTime(LocalDateTime.now());
            approval.setUpdateTime(LocalDateTime.now());
            medicalApprovalMapper.updateById(approval);
        }
    }
    
    @Override
    public String exportOrderExcel(OrderQueryDTO queryDTO) {
        LambdaQueryWrapper<MedicalOrder> wrapper = buildOrderQueryWrapper(queryDTO);
        wrapper.orderByDesc(MedicalOrder::getCreateTime);
        medicalOrderMapper.selectList(wrapper);
        String fileName = "orders_" + System.currentTimeMillis() + ".xlsx";
        return "/tmp/exports/" + fileName;
    }
    
    @Override
    public FinanceDashboardVO getFinanceDashboardData() {
        FinanceDashboardVO vo = new FinanceDashboardVO();
        
        // 查询今日收入
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getStatus, "已支付"); // 已支付
        wrapper.ge(MedicalOrder::getCreateTime, startOfDay);
        wrapper.lt(MedicalOrder::getCreateTime, endOfDay);
        
        List<MedicalOrder> todayOrders = medicalOrderMapper.selectList(wrapper);
        BigDecimal todayIncome = todayOrders.stream()
            .map(MedicalOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTodayIncome(todayIncome);
        
        // 查询本周收入
        LocalDateTime startOfWeek = LocalDate.now().minusDays(7).atStartOfDay();
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getStatus, "已支付");
        wrapper.ge(MedicalOrder::getCreateTime, startOfWeek);
        
        List<MedicalOrder> weekOrders = medicalOrderMapper.selectList(wrapper);
        BigDecimal weekIncome = weekOrders.stream()
            .map(MedicalOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        // FinanceDashboardVO中没有setWeekIncome方法，跳过设置
        
        // 查询本月收入
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getStatus, "已支付");
        wrapper.ge(MedicalOrder::getCreateTime, startOfMonth);
        
        List<MedicalOrder> monthOrders = medicalOrderMapper.selectList(wrapper);
        BigDecimal monthIncome = monthOrders.stream()
            .map(MedicalOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setMonthIncome(monthIncome);
        
        // 查询待支付数量
        LambdaQueryWrapper<MedicalOrder> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(MedicalOrder::getStatus, "待支付");
        Long unpaidCount = medicalOrderMapper.selectCount(unpaidWrapper);
        vo.setUnpaidOrderCount(unpaidCount.intValue());
        
        // 查询总订单数
        Long totalOrders = medicalOrderMapper.selectCount(null);
        // FinanceDashboardVO中没有setTotalOrders方法，跳过设置
        
        // 初始化图表数据
        // FinanceDashboardVO中没有这些方法，跳过设置
        
        return vo;
    }
    
    private LambdaQueryWrapper<MedicalOrder> buildOrderQueryWrapper(OrderQueryDTO queryDTO) {
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().isEmpty()) {
            wrapper.like(MedicalOrder::getOrderNo, queryDTO.getOrderNo());
        }
        if (queryDTO.getOrderStatus() != null) {
            String status = mapStatusCodeToDb(queryDTO.getOrderStatus());
            if (status != null) {
                wrapper.eq(MedicalOrder::getStatus, status);
            }
        }
        if (queryDTO.getStudentKey() != null && !queryDTO.getStudentKey().isEmpty()) {
            LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.and(w -> w.like(SysUser::getRealName, queryDTO.getStudentKey())
                    .or().like(SysUser::getUsername, queryDTO.getStudentKey()));
            List<SysUser> users = sysUserMapper.selectList(userWrapper);
            if (users.isEmpty()) {
                wrapper.eq(MedicalOrder::getUserId, -1L);
            } else {
                wrapper.in(MedicalOrder::getUserId,
                        users.stream().map(SysUser::getId).collect(Collectors.toList()));
            }
        }
        if (queryDTO.getStartTime() != null && !queryDTO.getStartTime().isEmpty()) {
            LocalDateTime start = LocalDate.parse(queryDTO.getStartTime().substring(0, 10)).atStartOfDay();
            wrapper.ge(MedicalOrder::getCreateTime, start);
        }
        if (queryDTO.getEndTime() != null && !queryDTO.getEndTime().isEmpty()) {
            LocalDateTime end = LocalDate.parse(queryDTO.getEndTime().substring(0, 10)).plusDays(1).atStartOfDay();
            wrapper.lt(MedicalOrder::getCreateTime, end);
        }
        if (queryDTO.getMinOverdueDays() != null && queryDTO.getMinOverdueDays() > 0) {
            LocalDateTime deadline = LocalDateTime.now().minusDays(queryDTO.getMinOverdueDays());
            wrapper.le(MedicalOrder::getCreateTime, deadline);
        }
        return wrapper;
    }

    private FinanceOrderVO toFinanceOrderVO(MedicalOrder order) {
        FinanceOrderVO vo = new FinanceOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        SysUser user = sysUserMapper.selectById(order.getUserId());
        vo.setStudentName(user != null ? user.getRealName() : "未知");
        vo.setStudentKey(user != null ? user.getUsername() : "");
        vo.setAmount(order.getTotalAmount());
        vo.setPayType(mapPayTypeCode(order.getPayType()));
        vo.setPayTypeName(displayPayType(order.getPayType()));
        vo.setStatus(mapStatusCode(order.getStatus()));
        vo.setStatusName(displayStatusName(order.getStatus()));
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setItemName(buildItemSummary(order.getId()));
        return vo;
    }

    private String buildItemSummary(Long orderId) {
        LambdaQueryWrapper<MedicalOrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(MedicalOrderItem::getOrderId, orderId);
        List<MedicalOrderItem> items = medicalOrderItemMapper.selectList(itemWrapper);
        if (items.isEmpty()) {
            return "诊疗费";
        }
        return items.stream()
                .map(MedicalOrderItem::getItemName)
                .collect(Collectors.joining("+"));
    }

    private String mapStatusCodeToDb(Integer code) {
        if (code == null) {
            return null;
        }
        switch (code) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 3:
                return "已退费";
            default:
                return null;
        }
    }

    private Integer mapStatusCode(String dbStatus) {
        if ("待支付".equals(dbStatus)) {
            return 0;
        }
        if ("已支付".equals(dbStatus)) {
            return 1;
        }
        if ("已取消".equals(dbStatus)) {
            return 2;
        }
        if ("已退费".equals(dbStatus)) {
            return 3;
        }
        return -1;
    }

    private String displayStatusName(String dbStatus) {
        if ("待支付".equals(dbStatus)) {
            return "待缴费";
        }
        if ("已支付".equals(dbStatus)) {
            return "已缴费";
        }
        if ("已退费".equals(dbStatus)) {
            return "已退费";
        }
        return dbStatus != null ? dbStatus : "待缴费";
    }

    private Integer mapPayTypeCode(String payType) {
        String normalized = payType == null ? "" : payType.toLowerCase();
        if ("campus".equals(normalized) || "校园卡".equals(payType)) {
            return 3;
        }
        if ("wechat".equals(normalized) || "微信".equals(payType) || "微信支付".equals(payType)) {
            return 2;
        }
        if ("alipay".equals(normalized) || "支付宝".equals(payType)) {
            return 1;
        }
        return 0;
    }

    private String displayPayType(String payType) {
        if (payType == null || payType.isEmpty()) {
            return "";
        }
        String normalized = payType.toLowerCase();
        if ("wechat".equals(normalized) || "微信".equals(payType)) {
            return "微信支付";
        }
        if ("alipay".equals(normalized) || "支付宝".equals(payType)) {
            return "支付宝";
        }
        if ("campus".equals(normalized) || "校园卡".equals(payType)) {
            return "校园卡";
        }
        return payType;
    }
}
