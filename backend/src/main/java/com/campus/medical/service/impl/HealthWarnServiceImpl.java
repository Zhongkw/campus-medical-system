package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.entity.SysConfig;
import com.campus.medical.mapper.MedicalRecordMapper;
import com.campus.medical.mapper.SysConfigMapper;
import com.campus.medical.service.HealthWarnService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 健康预警服务实现类
 */
@Slf4j
@Service
public class HealthWarnServiceImpl implements HealthWarnService {
    
    @Autowired
    private MedicalRecordMapper medicalRecordMapper;
    
    @Autowired
    private SysConfigMapper sysConfigMapper;
    
    private static final String WARN_THRESHOLD_KEY = "disease_warn_threshold";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public List<AbnormalDiseaseVO> scanCurrentHealthAbnormal() {
        // 获取预警阈值，默认10
        Integer threshold = getWarnThreshold();
        
        // 查询今日就诊记录
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MedicalRecord::getCreateTime, startOfDay);
        wrapper.lt(MedicalRecord::getCreateTime, endOfDay);
        
        List<MedicalRecord> todayRecords = medicalRecordMapper.selectList(wrapper);
        
        // 按疾病分组统计
        Map<String, Long> diseaseCount = new HashMap<>();
        for (MedicalRecord record : todayRecords) {
            String diagnosis = record.getDiagnosis() != null ? record.getDiagnosis() : "未知";
            diseaseCount.merge(diagnosis, 1L, Long::sum);
        }
        
        return diseaseCount.entrySet().stream()
            .map(entry -> {
                AbnormalDiseaseVO vo = new AbnormalDiseaseVO();
                vo.setDiseaseName(entry.getKey());
                vo.setTodayCount(entry.getValue());
                vo.setThreshold(threshold);
                vo.setIsAbnormal(entry.getValue() > threshold);
                return vo;
            })
            .filter(vo -> vo.getIsAbnormal())
            .sorted((a, b) -> b.getTodayCount().compareTo(a.getTodayCount()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<WarnHistoryVO> getWarnRecordList() {
        // 查询今日有异常的疾病记录作为预警历史
        List<AbnormalDiseaseVO> abnormalList = scanCurrentHealthAbnormal();
        
        return abnormalList.stream()
            .map(abnormal -> {
                WarnHistoryVO vo = new WarnHistoryVO();
                vo.setId(System.currentTimeMillis());
                vo.setDiseaseName(abnormal.getDiseaseName());
                vo.setWarnTime(LocalDateTime.now());
                vo.setStatus(0); // 未处理
                vo.setStatusName("未处理");
                return vo;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setWarnThreshold(Integer num) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, WARN_THRESHOLD_KEY);
        
        SysConfig config = sysConfigMapper.selectOne(wrapper);
        
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(WARN_THRESHOLD_KEY);
            config.setConfigValue(String.valueOf(num));
            config.setConfigName("疾病预警阈值");
            config.setCreateTime(LocalDateTime.now());
            return sysConfigMapper.insert(config) > 0;
        } else {
            config.setConfigValue(String.valueOf(num));
            config.setUpdateTime(LocalDateTime.now());
            return sysConfigMapper.updateById(config) > 0;
        }
    }
    
    private Integer getWarnThreshold() {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, WARN_THRESHOLD_KEY);
        
        SysConfig config = sysConfigMapper.selectOne(wrapper);
        if (config != null && config.getConfigValue() != null) {
            try {
                return Integer.parseInt(config.getConfigValue());
            } catch (NumberFormatException e) {
                return 10; // 默认10
            }
        }
        return 10; // 默认10
    }
}
