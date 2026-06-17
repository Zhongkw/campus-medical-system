package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.OrderPayRequestDTO;
import com.campus.medical.entity.MedicalInvoice;
import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.entity.MedicalPrescription;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalInvoiceMapper;
import com.campus.medical.mapper.MedicalOrderMapper;
import com.campus.medical.mapper.MedicalPrescriptionMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.OrderBusinessService;
import com.campus.medical.vo.FinanceDashboardVO;
import com.campus.medical.vo.OrderDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单支付业务服务实现类
 */
@Service
public class OrderBusinessServiceImpl implements OrderBusinessService {

    private static final Logger log = LoggerFactory.getLogger(OrderBusinessServiceImpl.class);

    @Autowired
    private MedicalOrderMapper orderMapper;

    @Autowired
    private MedicalInvoiceMapper invoiceMapper;

    @Autowired
    private MedicalPrescriptionMapper prescriptionMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询用户订单列表
     */
    @Override
    public List<MedicalOrder> getUserOrders(Long userId, String status, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getUserId, userId);
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalOrder::getStatus, status);
        }
        
        if (startDate != null) {
            wrapper.ge(MedicalOrder::getCreateTime, LocalDateTime.of(startDate, LocalTime.MIN));
        }
        if (endDate != null) {
            wrapper.le(MedicalOrder::getCreateTime, LocalDateTime.of(endDate, LocalTime.MAX));
        }
        
        wrapper.orderByDesc(MedicalOrder::getCreateTime);
        return orderMapper.selectList(wrapper);
    }

    /**
     * 查询订单详情（含明细和发票）
     */
    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        // 1. 查询订单基本信息
        MedicalOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 组装订单详情VO
        OrderDetailVO detailVO = new OrderDetailVO();
        detailVO.setOrderId(order.getId());
        detailVO.setOrderNo(order.getOrderNo());
        detailVO.setTotalAmount(order.getTotalAmount());
        detailVO.setPayType(order.getPayType());
        detailVO.setPayTime(order.getPayTime() != null ? order.getPayTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null);
        detailVO.setStatus(order.getStatus());
        detailVO.setPrescriptionId(order.getPrescriptionId());
        detailVO.setCreateTime(order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // 3. 查询用户姓名
        SysUser user = userMapper.selectById(order.getUserId());
        if (user != null) {
            detailVO.setUserName(user.getRealName());
        }

        // 4. 查询订单明细（从处方明细获取）
        List<OrderDetailVO.OrderItemVO> items = new ArrayList<>();
        if (order.getPrescriptionId() != null) {
            // TODO: 这里需要根据实际的订单明细表结构查询
            // 目前从处方明细获取药品信息
            LambdaQueryWrapper<MedicalPrescription> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MedicalPrescription::getId, order.getPrescriptionId());
            MedicalPrescription prescription = prescriptionMapper.selectOne(wrapper);
            
            if (prescription != null) {
                OrderDetailVO.OrderItemVO item = new OrderDetailVO.OrderItemVO();
                item.setItemName("诊疗费+药品费");
                item.setItemType("诊疗费");
                item.setPrice(order.getTotalAmount());
                item.setQuantity(1);
                item.setAmount(order.getTotalAmount());
                items.add(item);
            }
        }
        detailVO.setItems(items);

        // 5. 查询关联发票
        LambdaQueryWrapper<MedicalInvoice> invoiceWrapper = new LambdaQueryWrapper<>();
        invoiceWrapper.eq(MedicalInvoice::getOrderId, orderId);
        MedicalInvoice invoice = invoiceMapper.selectOne(invoiceWrapper);
        
        if (invoice != null) {
            OrderDetailVO.InvoiceVO invoiceVO = new OrderDetailVO.InvoiceVO();
            invoiceVO.setInvoiceId(invoice.getId());
            invoiceVO.setInvoiceNo(invoice.getInvoiceNo());
            invoiceVO.setTotalAmount(invoice.getTotalAmount());
            invoiceVO.setInvoiceUrl(invoice.getInvoiceUrl());
            invoiceVO.setCreateTime(invoice.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            detailVO.setInvoice(invoiceVO);
        }

        return detailVO;
    }

    /**
     * 支付订单（核心业务方法 - Mock支付）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(OrderPayRequestDTO requestDTO) {
        // 1. 查询订单信息
        MedicalOrder order = orderMapper.selectById(requestDTO.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 校验订单状态（只有待支付状态才能支付）
        if (!"待支付".equals(order.getStatus())) {
            throw new RuntimeException("订单状态异常，只有待支付状态的订单才能支付");
        }

        // 3. Mock支付处理（模拟支付接口调用）
        log.info("调用Mock支付接口，订单号：{}，支付方式：{}，金额：{}", 
                order.getOrderNo(), requestDTO.getPayType(), order.getTotalAmount());
        
        // 模拟支付成功（实际项目中这里调用真实的支付接口）
        boolean paySuccess = true;

        if (!paySuccess) {
            throw new RuntimeException("支付失败，请重试");
        }

        // 4. 更新订单状态为已支付
        order.setStatus("已支付");
        order.setPayType(requestDTO.getPayType());
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 5. 自动生成电子发票
        String invoiceNo = generateInvoice(order.getId());
        log.info("支付成功，自动生成电子发票：{}", invoiceNo);

        // 6. 如果有关联处方，更新处方状态为"待配药"（推送至药师端）
        if (order.getPrescriptionId() != null) {
            MedicalPrescription prescription = prescriptionMapper.selectById(order.getPrescriptionId());
            if (prescription != null) {
                prescription.setStatus("待配药");
                prescriptionMapper.updateById(prescription);
                log.info("处方已推送至药师端，处方号：{}", prescription.getId());
            }
        }

        log.info("订单支付成功，订单号：{}", order.getOrderNo());
        return true;
    }

    /**
     * 生成电子发票
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateInvoice(Long orderId) {
        // 1. 查询订单信息
        MedicalOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 检查是否已生成发票
        LambdaQueryWrapper<MedicalInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInvoice::getOrderId, orderId);
        MedicalInvoice existInvoice = invoiceMapper.selectOne(wrapper);
        if (existInvoice != null) {
            return existInvoice.getInvoiceNo();
        }

        // 3. 生成发票编号（INV+日期+序号）
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String invoiceNo = "INV" + dateStr + String.format("%04d", System.currentTimeMillis() % 10000);

        // 4. 创建发票记录
        MedicalInvoice invoice = new MedicalInvoice();
        invoice.setInvoiceNo(invoiceNo);
        invoice.setOrderId(orderId);
        invoice.setUserId(order.getUserId());
        invoice.setTotalAmount(order.getTotalAmount());
        // Mock发票PDF地址（实际项目中需要生成真实PDF文件）
        invoice.setInvoiceUrl("/invoices/" + invoiceNo + ".pdf");
        invoiceMapper.insert(invoice);

        log.info("电子发票生成成功，发票号：{}", invoiceNo);
        return invoiceNo;
    }

    /**
     * 查询用户发票列表
     */
    @Override
    public List<OrderDetailVO.InvoiceVO> getUserInvoices(Long userId) {
        LambdaQueryWrapper<MedicalInvoice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalInvoice::getUserId, userId);
        wrapper.orderByDesc(MedicalInvoice::getCreateTime);
        List<MedicalInvoice> invoices = invoiceMapper.selectList(wrapper);

        List<OrderDetailVO.InvoiceVO> result = new ArrayList<>();
        for (MedicalInvoice invoice : invoices) {
            OrderDetailVO.InvoiceVO vo = new OrderDetailVO.InvoiceVO();
            vo.setInvoiceId(invoice.getId());
            vo.setInvoiceNo(invoice.getInvoiceNo());
            vo.setTotalAmount(invoice.getTotalAmount());
            vo.setInvoiceUrl(invoice.getInvoiceUrl());
            vo.setCreateTime(invoice.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            result.add(vo);
        }
        return result;
    }

    /**
     * 财务统计面板数据
     */
    @Override
    public FinanceDashboardVO getFinanceDashboard() {
        FinanceDashboardVO dashboard = new FinanceDashboardVO();

        // 1. 今日营收
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<MedicalOrder> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(MedicalOrder::getStatus, "已支付");
        todayWrapper.ge(MedicalOrder::getPayTime, todayStart);
        List<MedicalOrder> todayOrders = orderMapper.selectList(todayWrapper);
        BigDecimal todayIncome = todayOrders.stream()
                .map(MedicalOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setTodayIncome(todayIncome);

        // 2. 本月营收
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LambdaQueryWrapper<MedicalOrder> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(MedicalOrder::getStatus, "已支付");
        monthWrapper.ge(MedicalOrder::getPayTime, monthStart);
        List<MedicalOrder> monthOrders = orderMapper.selectList(monthWrapper);
        BigDecimal monthIncome = monthOrders.stream()
                .map(MedicalOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dashboard.setMonthIncome(monthIncome);

        // 3. 待缴费订单数
        LambdaQueryWrapper<MedicalOrder> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(MedicalOrder::getStatus, "待支付");
        Long unpaidCount = orderMapper.selectCount(unpaidWrapper);
        dashboard.setUnpaidOrderCount(unpaidCount.intValue());

        // 4. 今日退费金额（TODO: 需要退费记录表）
        dashboard.setTodayRefund(BigDecimal.ZERO);
        dashboard.setMonthRefund(BigDecimal.ZERO);

        return dashboard;
    }

    /**
     * 退费审核（财务端）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditRefund(Long orderId, String auditStatus, String auditRemark, Long auditorId) {
        // 1. 查询订单信息
        MedicalOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 2. 校验订单状态（只有已支付状态才能退费）
        if (!"已支付".equals(order.getStatus())) {
            throw new RuntimeException("订单状态异常，只有已支付状态的订单才能退费");
        }

        // 3. 审核处理
        if ("PASS".equals(auditStatus)) {
            // 审核通过：更新订单状态为已退费
            order.setStatus("已退费");
            orderMapper.updateById(order);
            log.info("退费审核通过，订单号：{}，审核人：{}，备注：{}", order.getOrderNo(), auditorId, auditRemark);
            
            // TODO: 如果有关联处方，需要回退处方状态
            // TODO: 记录退费审核日志
        } else if ("REJECT".equals(auditStatus)) {
            // 审核驳回：订单状态保持已支付，记录驳回原因
            log.info("退费审核驳回，订单号：{}，审核人：{}，原因：{}", order.getOrderNo(), auditorId, auditRemark);
            // TODO: 记录退费审核日志
        } else {
            throw new RuntimeException("审核状态异常");
        }

        return true;
    }
}
