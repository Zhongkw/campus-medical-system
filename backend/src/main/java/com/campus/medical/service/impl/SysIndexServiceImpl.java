package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.SysLoginLog;
import com.campus.medical.entity.SysOperLog;
import com.campus.medical.entity.SysRole;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.SysLoginLogMapper;
import com.campus.medical.mapper.SysOperLogMapper;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.SysIndexService;
import com.campus.medical.vo.SysIndexStatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 系统首页服务实现类
 */
@Slf4j
@Service
public class SysIndexServiceImpl implements SysIndexService {
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysRoleMapper sysRoleMapper;
    
    @Autowired
    private SysLoginLogMapper sysLoginLogMapper;
    
    @Autowired
    private SysOperLogMapper sysOperLogMapper;
    
    @Override
    public SysIndexStatVO getIndexStatData() {
        SysIndexStatVO vo = new SysIndexStatVO();
        
        // 用户总数
        Long userCount = sysUserMapper.selectCount(null);
        vo.setUserCount(userCount);
        
        // 角色总数
        Long roleCount = sysRoleMapper.selectCount(null);
        vo.setRoleCount(roleCount);
        
        // 今日登录人数
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        LambdaQueryWrapper<SysLoginLog> loginWrapper = new LambdaQueryWrapper<>();
        loginWrapper.ge(SysLoginLog::getLoginTime, startOfDay);
        loginWrapper.lt(SysLoginLog::getLoginTime, endOfDay);
        Long todayLogin = sysLoginLogMapper.selectCount(loginWrapper);
        vo.setTodayLogin(todayLogin);
        
        // 操作日志数量
        LambdaQueryWrapper<SysOperLog> operWrapper = new LambdaQueryWrapper<>();
        operWrapper.ge(SysOperLog::getOperTime, startOfDay.minusDays(7));
        Long logCount = sysOperLogMapper.selectCount(operWrapper);
        vo.setLogCount(logCount);
        
        return vo;
    }
}
