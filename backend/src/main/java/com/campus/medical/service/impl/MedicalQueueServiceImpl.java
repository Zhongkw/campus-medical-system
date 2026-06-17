package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalQueue;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalQueueMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.service.MedicalQueueService;
import com.campus.medical.vo.QueueDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 队列服务实现类
 */
@Service
public class MedicalQueueServiceImpl extends BaseServiceImpl<MedicalQueueMapper, MedicalQueue> implements MedicalQueueService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private MedicalDepartmentMapper departmentMapper;

    /**
     * 根据科室ID查询队列列表
     */
    @Override
    public List<MedicalQueue> getByDeptId(Long deptId) {
        LambdaQueryWrapper<MedicalQueue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalQueue::getDeptId, deptId);
        wrapper.orderByAsc(MedicalQueue::getQueueNo);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 根据医生ID查询队列列表
     */
    @Override
    public List<MedicalQueue> getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<MedicalQueue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalQueue::getDoctorId, doctorId);
        wrapper.orderByAsc(MedicalQueue::getQueueNo);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<QueueDetailVO> getQueueDetailByDoctor(Long doctorId, String queueDate, String status) {
        LambdaQueryWrapper<MedicalQueue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalQueue::getDoctorId, doctorId);
        if (queueDate != null && !queueDate.isEmpty()) {
            wrapper.eq(MedicalQueue::getQueueDate, LocalDate.parse(queueDate));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalQueue::getStatus, status);
        }
        wrapper.orderByAsc(MedicalQueue::getQueueNo);

        List<MedicalQueue> queues = baseMapper.selectList(wrapper);
        List<QueueDetailVO> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (MedicalQueue queue : queues) {
            QueueDetailVO vo = new QueueDetailVO();
            vo.setId(queue.getId());
            vo.setAppointmentId(queue.getAppointmentId());
            vo.setDeptId(queue.getDeptId());
            vo.setQueueNo(queue.getQueueNo());
            vo.setUserId(queue.getUserId());
            vo.setSymptom(queue.getSymptom());
            vo.setStatus(queue.getStatus());
            vo.setQueueDate(queue.getQueueDate() != null ? queue.getQueueDate().toString() : null);

            SysUser user = userMapper.selectById(queue.getUserId());
            if (user != null) {
                vo.setStudentName(user.getRealName());
                vo.setStudentNo(user.getUsername());
            }

            MedicalDepartment dept = departmentMapper.selectById(queue.getDeptId());
            if (dept != null) {
                vo.setDepartment(dept.getDeptName());
            }

            if (queue.getCreateTime() != null) {
                long minutes = Duration.between(queue.getCreateTime(), now).toMinutes();
                vo.setWaitTime(Math.max(minutes, 0) + "分钟");
            } else {
                vo.setWaitTime("0分钟");
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    public void deleteQueueRecord(Long queueId, Long doctorId) {
        MedicalQueue queue = baseMapper.selectById(queueId);
        if (queue == null) {
            throw new RuntimeException("队列记录不存在");
        }
        if (!queue.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权删除该队列记录");
        }
        baseMapper.deleteById(queueId);
    }
}
