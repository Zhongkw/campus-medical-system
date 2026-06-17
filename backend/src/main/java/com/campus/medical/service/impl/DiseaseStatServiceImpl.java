package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.entity.SysDictData;
import com.campus.medical.mapper.MedicalRecordMapper;
import com.campus.medical.mapper.SysDictDataMapper;
import com.campus.medical.service.DiseaseStatService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 疾病统计服务实现类
 */
@Slf4j
@Service
public class DiseaseStatServiceImpl implements DiseaseStatService {
    
    @Autowired
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    @Override
    public List<DiseaseRankVO> getTopDiseaseRank(String startTime, String endTime) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (startTime != null) {
            wrapper.ge(MedicalRecord::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(MedicalRecord::getCreateTime, endTime);
        }
        
        List<MedicalRecord> records = medicalRecordMapper.selectList(wrapper);
        
        Map<String, Long> diseaseCount = new HashMap<>();
        Map<String, String> diseaseCategoryMap = new HashMap<>();
        long totalCount = 0;
        for (MedicalRecord record : records) {
            String diseaseKey = resolveDiseaseKey(record);
            diseaseCount.merge(diseaseKey, 1L, Long::sum);
            diseaseCategoryMap.putIfAbsent(diseaseKey, resolveDiseaseCategory(record));
            totalCount++;
        }
        
        if (totalCount == 0) {
            return new ArrayList<>();
        }
        
        final long finalTotal = totalCount;
        return diseaseCount.entrySet().stream()
            .map(entry -> {
                DiseaseRankVO vo = new DiseaseRankVO();
                vo.setDiseaseName(entry.getKey());
                vo.setCategory(diseaseCategoryMap.getOrDefault(entry.getKey(), "未分类"));
                vo.setCount(entry.getValue());
                vo.setPercentage(new BigDecimal(entry.getValue())
                    .divide(new BigDecimal(finalTotal), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")));
                return vo;
            })
            .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
            .limit(10)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DiseaseTrendVO> getSingleDiseaseTrend(String diseaseName) {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(MedicalRecord::getDiseaseName, diseaseName)
                .or().like(MedicalRecord::getDiagnosis, diseaseName));
        
        List<MedicalRecord> records = medicalRecordMapper.selectList(wrapper);
        
        // 按月份分组统计
        Map<String, Long> monthlyCount = new HashMap<>();
        for (MedicalRecord record : records) {
            String month = record.getCreateTime().format(DATE_FORMATTER);
            monthlyCount.merge(month, 1L, Long::sum);
        }
        
        return monthlyCount.entrySet().stream()
            .map(entry -> {
                DiseaseTrendVO vo = new DiseaseTrendVO();
                vo.setMonth(entry.getKey());
                vo.setCount(entry.getValue());
                return vo;
            })
            .sorted(Comparator.comparing(DiseaseTrendVO::getMonth))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<CollegeDiseaseVO> getCollegeDiseaseDistData() {
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(MedicalRecord::getCollege);
        
        List<MedicalRecord> records = medicalRecordMapper.selectList(wrapper);
        
        Map<String, Long> collegeCount = new HashMap<>();
        for (MedicalRecord record : records) {
            String college = record.getCollege() != null ? record.getCollege() : "未知";
            collegeCount.merge(college, 1L, Long::sum);
        }
        
        return collegeCount.entrySet().stream()
            .map(entry -> {
                CollegeDiseaseVO vo = new CollegeDiseaseVO();
                vo.setCollege(entry.getKey());
                vo.setCount(entry.getValue());
                return vo;
            })
            .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
            .collect(Collectors.toList());
    }

    private String resolveDiseaseKey(MedicalRecord record) {
        if (record.getDiseaseName() != null && !record.getDiseaseName().isBlank()) {
            return record.getDiseaseName();
        }
        return record.getDiagnosis() != null && !record.getDiagnosis().isBlank() ? record.getDiagnosis() : "未知";
    }

    private String resolveDiseaseCategory(MedicalRecord record) {
        if (record.getDiseaseCode() != null && !record.getDiseaseCode().isBlank()) {
            SysDictData dict = dictDataMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysDictData>()
                    .eq(SysDictData::getDictType, "disease")
                    .eq(SysDictData::getDictValue, record.getDiseaseCode())
                    .last("LIMIT 1"));
            if (dict != null && dict.getCategory() != null) {
                return dict.getCategory();
            }
        }
        if (record.getDiseaseName() != null && !record.getDiseaseName().isBlank()) {
            return "标准病种";
        }
        return "自由诊断";
    }
}
