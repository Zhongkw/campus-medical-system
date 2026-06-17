package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalRecord;
import com.campus.medical.entity.SysRole;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalRecordMapper;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.VisitStatService;
import com.campus.medical.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 就诊统计服务实现类
 */
@Slf4j
@Service
public class VisitStatServiceImpl implements VisitStatService {
    
    @Autowired
    private MedicalRecordMapper medicalRecordMapper;
    
    @Autowired
    private MedicalDepartmentMapper medicalDepartmentMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public List<VisitTrendVO> getVisitTrendInfo(String startTime, String endTime) {
        List<VisitTrendVO> result = new ArrayList<>();
        
        LocalDate start = startTime != null ? LocalDate.parse(startTime.substring(0, 10)) : LocalDate.now().minusDays(30);
        LocalDate end = endTime != null ? LocalDate.parse(endTime.substring(0, 10)) : LocalDate.now();
        
        LambdaQueryWrapper<MedicalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MedicalRecord::getCreateTime, start.atStartOfDay());
        wrapper.le(MedicalRecord::getCreateTime, end.plusDays(1).atStartOfDay());
        
        List<MedicalRecord> records = medicalRecordMapper.selectList(wrapper);
        
        // 按日期分组统计
        Map<String, Long> dailyCount = new HashMap<>();
        for (MedicalRecord record : records) {
            String date = record.getCreateTime().format(DATE_FORMATTER);
            dailyCount.merge(date, 1L, Long::sum);
        }
        
        // 生成日期范围内的所有日期数据
        LocalDate current = start;
        while (!current.isAfter(end)) {
            VisitTrendVO vo = new VisitTrendVO();
            vo.setDate(current.format(DATE_FORMATTER));
            vo.setCount(dailyCount.getOrDefault(current.format(DATE_FORMATTER), 0L));
            result.add(vo);
            current = current.plusDays(1);
        }
        
        return result;
    }
    
    @Override
    public List<DeptVisitVO> getDeptVisitRank() {
        List<MedicalRecord> records = medicalRecordMapper.selectList(null);
        
        Map<String, Long> deptCount = new HashMap<>();
        for (MedicalRecord record : records) {
            String deptName = resolveDeptName(record);
            deptCount.merge(deptName, 1L, Long::sum);
        }
        
        return deptCount.entrySet().stream()
            .map(entry -> {
                DeptVisitVO vo = new DeptVisitVO();
                vo.setDeptName(entry.getKey());
                vo.setVisitCount(entry.getValue());
                return vo;
            })
            .sorted((a, b) -> b.getVisitCount().compareTo(a.getVisitCount()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<UserTypeVisitVO> getUserTypeVisitDist() {
        List<MedicalRecord> records = medicalRecordMapper.selectList(null);
        
        Map<String, Long> typeCount = new HashMap<>();
        for (MedicalRecord record : records) {
            String userType = determineUserType(record.getPatientId());
            typeCount.merge(userType, 1L, Long::sum);
        }
        
        return typeCount.entrySet().stream()
            .map(entry -> {
                UserTypeVisitVO vo = new UserTypeVisitVO();
                vo.setUserType(entry.getKey());
                vo.setCount(entry.getValue());
                return vo;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PeakVisitVO> getVisitPeakAnalysis() {
        List<MedicalRecord> records = medicalRecordMapper.selectList(null);
        
        // 按小时分组统计
        Map<String, Long> hourCount = new HashMap<>();
        String[] timeSlots = {"08:00-09:00", "09:00-10:00", "10:00-11:00", "11:00-12:00",
            "14:00-15:00", "15:00-16:00", "16:00-17:00", "17:00-18:00"};
        
        for (MedicalRecord record : records) {
            String hour = String.valueOf(record.getCreateTime().getHour());
            String timeSlot = getTimeSlot(record.getCreateTime().getHour());
            if (timeSlot != null) {
                hourCount.merge(timeSlot, 1L, Long::sum);
            }
        }
        
        return Arrays.stream(timeSlots)
            .map(slot -> {
                PeakVisitVO vo = new PeakVisitVO();
                vo.setTimeSlot(slot);
                vo.setVisitCount(hourCount.getOrDefault(slot, 0L));
                return vo;
            })
            .collect(Collectors.toList());
    }
    
    private String determineUserType(Long patientId) {
        if (patientId == null) return "未知";
        SysUser user = sysUserMapper.selectById(patientId);
        if (user == null) return "未知";
        
        // 通过用户的角色ID查询角色编码
        String roleCode = getRoleCodeByRoleId(user.getRoleId());
        if (roleCode != null && roleCode.contains("student")) {
            return "学生";
        } else if (roleCode != null && roleCode.contains("teacher")) {
            return "教职工";
        }
        return "其他";
    }
    
    private String getTimeSlot(int hour) {
        if (hour >= 8 && hour < 9) return "08:00-09:00";
        if (hour >= 9 && hour < 10) return "09:00-10:00";
        if (hour >= 10 && hour < 11) return "10:00-11:00";
        if (hour >= 11 && hour < 12) return "11:00-12:00";
        if (hour >= 14 && hour < 15) return "14:00-15:00";
        if (hour >= 15 && hour < 16) return "15:00-16:00";
        if (hour >= 16 && hour < 17) return "16:00-17:00";
        if (hour >= 17 && hour < 18) return "17:00-18:00";
        return null;
    }
    
    private String getRoleCodeByRoleId(Long roleId) {
        if (roleId == null) return null;
        SysRole role = sysRoleMapper.selectById(roleId);
        return role != null ? role.getRoleCode() : null;
    }

    private String resolveDeptName(MedicalRecord record) {
        if (record.getDeptName() != null && !record.getDeptName().isEmpty()) {
            return record.getDeptName();
        }
        if (record.getDeptId() != null) {
            MedicalDepartment department = medicalDepartmentMapper.selectById(record.getDeptId());
            if (department != null && department.getDeptName() != null) {
                return department.getDeptName();
            }
        }
        return "未知";
    }
}
