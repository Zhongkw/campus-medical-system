package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.entity.MedicalPrescriptionItem;
import com.campus.medical.mapper.MedicalMedicineMapper;
import com.campus.medical.mapper.MedicalPrescriptionItemMapper;
import com.campus.medical.service.DrugConsumeService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 药品消耗统计服务实现类
 */
@Slf4j
@Service
public class DrugConsumeServiceImpl implements DrugConsumeService {
    
    @Autowired
    private MedicalPrescriptionItemMapper prescriptionItemMapper;
    
    @Autowired
    private MedicalMedicineMapper medicineMapper;
    
    @Override
    public List<DrugConsumeVO> getDrugUseRank(String startTime, String endTime) {
        LambdaQueryWrapper<MedicalPrescriptionItem> wrapper = new LambdaQueryWrapper<>();
        
        if (startTime != null) {
            wrapper.ge(MedicalPrescriptionItem::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MedicalPrescriptionItem::getCreateTime, endTime);
        }
        
        List<MedicalPrescriptionItem> items = prescriptionItemMapper.selectList(wrapper);
        
        // 按药品名称分组统计
        Map<String, DrugConsumeVO> drugStats = new HashMap<>();
        for (MedicalPrescriptionItem item : items) {
            String name = item.getMedicineName() != null ? item.getMedicineName() : "未知";
            drugStats.computeIfAbsent(name, k -> {
                DrugConsumeVO vo = new DrugConsumeVO();
                vo.setMedicineName(name);
                vo.setConsumeCount(0L);
                vo.setConsumeAmount(BigDecimal.ZERO);
                return vo;
            });
            DrugConsumeVO vo = drugStats.get(name);
            vo.setConsumeCount(vo.getConsumeCount() + item.getQuantity());
            vo.setConsumeAmount(vo.getConsumeAmount().add(item.getTotalPrice()));
        }
        
        return drugStats.values().stream()
            .sorted((a, b) -> b.getConsumeCount().compareTo(a.getConsumeCount()))
            .limit(20)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DrugTypeRatioVO> getDrugClassRatio() {
        LambdaQueryWrapper<MedicalPrescriptionItem> wrapper = new LambdaQueryWrapper<>();
        List<MedicalPrescriptionItem> items = prescriptionItemMapper.selectList(wrapper);
        
        // 按药品类型分组统计
        Map<String, Long> typeCount = new HashMap<>();
        long totalCount = 0;
        for (MedicalPrescriptionItem item : items) {
            // 获取药品类型
            LambdaQueryWrapper<MedicalMedicine> medWrapper = new LambdaQueryWrapper<>();
            medWrapper.eq(MedicalMedicine::getMedicineName, item.getMedicineName());
            MedicalMedicine medicine = medicineMapper.selectOne(medWrapper);
            
            String typeName = medicine != null && medicine.getCategory() != null 
                ? medicine.getCategory() : "其他";
            typeCount.merge(typeName, 1L, Long::sum);
            totalCount++;
        }
        
        if (totalCount == 0) {
            return new ArrayList<>();
        }
        
        final long finalTotal = totalCount;
        return typeCount.entrySet().stream()
            .map(entry -> {
                DrugTypeRatioVO vo = new DrugTypeRatioVO();
                vo.setTypeName(entry.getKey());
                vo.setRatio(new BigDecimal(entry.getValue())
                    .divide(new BigDecimal(finalTotal), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")));
                return vo;
            })
            .sorted((a, b) -> b.getRatio().compareTo(a.getRatio()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DrugReplenishVO> calcDrugReplenishNeed() {
        List<MedicalMedicine> medicines = medicineMapper.selectList(null);
        List<DrugReplenishVO> result = new ArrayList<>();
        
        for (MedicalMedicine medicine : medicines) {
            // 计算日均消耗
            LambdaQueryWrapper<MedicalPrescriptionItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MedicalPrescriptionItem::getMedicineName, medicine.getMedicineName());
            List<MedicalPrescriptionItem> items = prescriptionItemMapper.selectList(wrapper);
            
            if (items.isEmpty()) {
                continue;
            }
            
            long totalConsume = items.stream()
                .mapToLong(MedicalPrescriptionItem::getQuantity)
                .sum();
            
            // 假设统计的是30天的数据
            long dailyConsume = totalConsume / 30;
            if (dailyConsume == 0) dailyConsume = 1;
            
            // 计算建议补货量（库存不足7天时建议补货）
            long currentStock = medicine.getStockQuantity() != null ? medicine.getStockQuantity() : 0;
            long daysOfStock = currentStock / dailyConsume;
            
            if (daysOfStock < 7) {
                DrugReplenishVO vo = new DrugReplenishVO();
                vo.setMedicineName(medicine.getMedicineName());
                vo.setCurrentStock(currentStock);
                vo.setDailyConsume(dailyConsume);
                vo.setReplenishQty(Math.max(100 - currentStock, 50)); // 至少补到100
                result.add(vo);
            }
        }
        
        return result.stream()
            .sorted(Comparator.comparing(DrugReplenishVO::getReplenishQty).reversed())
            .collect(Collectors.toList());
    }
}
