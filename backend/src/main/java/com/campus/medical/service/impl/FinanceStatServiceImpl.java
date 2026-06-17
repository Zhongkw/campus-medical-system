package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalOrder;
import com.campus.medical.entity.MedicalOrderItem;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalOrderItemMapper;
import com.campus.medical.mapper.MedicalOrderMapper;
import com.campus.medical.service.FinanceStatService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务统计服务实现类
 */
@Slf4j
@Service
public class FinanceStatServiceImpl implements FinanceStatService {
    
    @Autowired
    private MedicalOrderMapper medicalOrderMapper;
    
    @Autowired
    private MedicalOrderItemMapper medicalOrderItemMapper;
    
    @Autowired
    private MedicalDepartmentMapper medicalDepartmentMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public List<DailyIncomeVO> getRangeIncomeData(String startTime, String endTime) {
        List<DailyIncomeVO> result = new ArrayList<>();
        
        LocalDate start = startTime != null ? LocalDate.parse(startTime.substring(0, 10)) : LocalDate.now().minusDays(30);
        LocalDate end = endTime != null ? LocalDate.parse(endTime.substring(0, 10)) : LocalDate.now();
        
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getStatus, "已支付"); // 已支付
        wrapper.ge(MedicalOrder::getCreateTime, start.atStartOfDay());
        wrapper.le(MedicalOrder::getCreateTime, end.plusDays(1).atStartOfDay());
        
        List<MedicalOrder> orders = medicalOrderMapper.selectList(wrapper);
        
        // 按日期分组统计
        Map<String, BigDecimal> dailyIncome = new HashMap<>();
        for (MedicalOrder order : orders) {
            String date = order.getCreateTime().format(DATE_FORMATTER);
            BigDecimal amount = order.getTotalAmount();
            dailyIncome.merge(date, amount, BigDecimal::add);
        }
        
        // 生成日期范围内的所有日期数据
        LocalDate current = start;
        while (!current.isAfter(end)) {
            DailyIncomeVO vo = new DailyIncomeVO();
            vo.setDate(current.format(DATE_FORMATTER));
            vo.setIncome(dailyIncome.getOrDefault(current.format(DATE_FORMATTER), BigDecimal.ZERO));
            result.add(vo);
            current = current.plusDays(1);
        }
        
        return result;
    }
    
    @Override
    public List<DeptIncomeVO> getDepartmentIncomeStat() {
        List<DeptIncomeVO> result = new ArrayList<>();
        
        // 获取所有科室
        List<MedicalDepartment> departments = medicalDepartmentMapper.selectList(null);
        
        for (MedicalDepartment dept : departments) {
            // 查询该科室的订单项目
            LambdaQueryWrapper<MedicalOrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MedicalOrderItem::getDeptId, dept.getId());
            
            List<MedicalOrderItem> items = medicalOrderItemMapper.selectList(wrapper);
            
            BigDecimal totalIncome = items.stream()
                .filter(item -> {
                    MedicalOrder order = medicalOrderMapper.selectById(item.getOrderId());
                    return order != null && order.getStatus() != null && order.getStatus().equals("已支付");
                })
                .map(MedicalOrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            DeptIncomeVO vo = new DeptIncomeVO();
            vo.setDeptName(dept.getDeptName());
            vo.setIncome(totalIncome);
            result.add(vo);
        }
        
        return result.stream()
            .sorted((a, b) -> b.getIncome().compareTo(a.getIncome()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<IncomeRatioVO> getIncomeStructureRatio() {
        List<IncomeRatioVO> result = new ArrayList<>();
        
        // 按支付方式统计
        LambdaQueryWrapper<MedicalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalOrder::getStatus, "已支付");
        
        List<MedicalOrder> orders = medicalOrderMapper.selectList(wrapper);
        
        BigDecimal totalIncome = orders.stream()
            .map(MedicalOrder::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalIncome.compareTo(BigDecimal.ZERO) == 0) {
            return result;
        }
        
        Map<String, BigDecimal> incomeByType = new HashMap<>();
        for (MedicalOrder order : orders) {
            String payType = normalizePayType(order.getPayType());
            incomeByType.merge(payType, order.getTotalAmount(), BigDecimal::add);
        }
        
        for (Map.Entry<String, BigDecimal> entry : incomeByType.entrySet()) {
            IncomeRatioVO vo = new IncomeRatioVO();
            vo.setTypeName(entry.getKey());
            vo.setRatio(entry.getValue().divide(totalIncome, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")));
            result.add(vo);
        }
        
        return result;
    }
    
    @Override
    public FinanceSummaryVO getWholeFinanceSummary() {
        FinanceSummaryVO vo = new FinanceSummaryVO();

        LambdaQueryWrapper<MedicalOrder> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(MedicalOrder::getStatus, "已支付");
        List<MedicalOrder> paidOrders = medicalOrderMapper.selectList(incomeWrapper);
        BigDecimal totalIncome = paidOrders.stream()
                .map(MedicalOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalIncome(totalIncome);

        LambdaQueryWrapper<MedicalOrder> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.eq(MedicalOrder::getStatus, "已退费");
        List<MedicalOrder> refundOrders = medicalOrderMapper.selectList(refundWrapper);
        BigDecimal totalRefund = refundOrders.stream()
                .map(MedicalOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalRefund(totalRefund);

        LambdaQueryWrapper<MedicalOrder> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(MedicalOrder::getStatus, "待支付");
        Long unpaidCount = medicalOrderMapper.selectCount(unpaidWrapper);
        vo.setUnpaidCount(unpaidCount);

        return vo;
    }

    @Override
    public ReconciliationVO getDailyReconciliation(String date) {
        ReconciliationVO result = new ReconciliationVO();
        LocalDate targetDate = date != null && !date.isEmpty()
                ? LocalDate.parse(date.substring(0, 10))
                : LocalDate.now();
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay();

        LambdaQueryWrapper<MedicalOrder> paidWrapper = new LambdaQueryWrapper<>();
        paidWrapper.eq(MedicalOrder::getStatus, "已支付");
        paidWrapper.ge(MedicalOrder::getPayTime, start);
        paidWrapper.lt(MedicalOrder::getPayTime, end);
        List<MedicalOrder> paidOrders = medicalOrderMapper.selectList(paidWrapper);

        LambdaQueryWrapper<MedicalOrder> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.eq(MedicalOrder::getStatus, "已退费");
        refundWrapper.ge(MedicalOrder::getUpdateTime, start);
        refundWrapper.lt(MedicalOrder::getUpdateTime, end);
        List<MedicalOrder> refundOrders = medicalOrderMapper.selectList(refundWrapper);

        Map<String, BigDecimal> paidByMethod = new HashMap<>();
        Map<String, Integer> countByMethod = new HashMap<>();
        for (MedicalOrder order : paidOrders) {
            String method = normalizePayType(order.getPayType());
            paidByMethod.merge(method, order.getTotalAmount(), BigDecimal::add);
            countByMethod.merge(method, 1, Integer::sum);
            result.setTotalIncome(result.getTotalIncome().add(order.getTotalAmount()));
            if ("微信支付".equals(method)) {
                result.setWechatIncome(result.getWechatIncome().add(order.getTotalAmount()));
            } else if ("支付宝".equals(method)) {
                result.setAlipayIncome(result.getAlipayIncome().add(order.getTotalAmount()));
            } else if ("校园卡".equals(method)) {
                result.setCampusIncome(result.getCampusIncome().add(order.getTotalAmount()));
            }
        }

        Map<String, BigDecimal> refundByMethod = new HashMap<>();
        Map<String, Integer> refundCountByMethod = new HashMap<>();
        for (MedicalOrder order : refundOrders) {
            String method = normalizePayType(order.getPayType());
            refundByMethod.merge(method, order.getTotalAmount(), BigDecimal::add);
            refundCountByMethod.merge(method, 1, Integer::sum);
            result.setTotalRefund(result.getTotalRefund().add(order.getTotalAmount()));
        }

        List<String> methods = List.of("微信支付", "支付宝", "校园卡");
        for (String method : methods) {
            ReconciliationDetailVO detail = new ReconciliationDetailVO();
            detail.setPayMethod(method);
            detail.setOrderCount(countByMethod.getOrDefault(method, 0));
            detail.setTotalAmount(paidByMethod.getOrDefault(method, BigDecimal.ZERO));
            detail.setRefundCount(refundCountByMethod.getOrDefault(method, 0));
            detail.setRefundAmount(refundByMethod.getOrDefault(method, BigDecimal.ZERO));
            detail.setActualAmount(detail.getTotalAmount().subtract(detail.getRefundAmount()));
            detail.setReconciliationStatus("已对账");
            result.getDetails().add(detail);
        }

        return result;
    }

    private String normalizePayType(String payType) {
        if (payType == null || payType.isEmpty()) {
            return "未知";
        }
        String normalized = payType.toLowerCase();
        if ("wechat".equals(normalized) || "微信".equals(payType) || "微信支付".equals(payType)) {
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
