package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.dto.AppointmentRequestDTO;
import com.campus.medical.entity.*;
import com.campus.medical.mapper.*;
import com.campus.medical.service.AppointmentBusinessService;
import com.campus.medical.vo.AppointmentResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 预约挂号业务服务实现类
 */
@Service
public class AppointmentBusinessServiceImpl implements AppointmentBusinessService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentBusinessServiceImpl.class);

    @Autowired
    private MedicalScheduleMapper scheduleMapper;

    @Autowired
    private MedicalAppointmentMapper appointmentMapper;

    @Autowired
    private MedicalQueueMapper queueMapper;

    @Autowired
    private MedicalDoctorMapper doctorMapper;

    @Autowired
    private MedicalDepartmentMapper departmentMapper;

    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询医生排班列表
     */
    @Override
    public List<MedicalSchedule> getDoctorSchedules(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<MedicalSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSchedule::getDoctorId, doctorId);
        wrapper.ge(MedicalSchedule::getScheduleDate, startDate);
        wrapper.le(MedicalSchedule::getScheduleDate, endDate);
        wrapper.eq(MedicalSchedule::getStatus, 1); // 只查询正常排班
        wrapper.orderByAsc(MedicalSchedule::getScheduleDate, MedicalSchedule::getTimeSlot);
        return scheduleMapper.selectList(wrapper);
    }

    /**
     * 查询科室排班列表
     */
    @Override
    public List<MedicalSchedule> getDeptSchedules(Long deptId, LocalDate date) {
        LambdaQueryWrapper<MedicalSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalSchedule::getDeptId, deptId);
        wrapper.eq(MedicalSchedule::getScheduleDate, date);
        wrapper.eq(MedicalSchedule::getStatus, 1);
        wrapper.orderByAsc(MedicalSchedule::getTimeSlot);
        return scheduleMapper.selectList(wrapper);
    }

    /**
     * 创建预约（核心业务方法 - 完整流程）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppointmentResultVO createAppointment(AppointmentRequestDTO requestDTO) {
        log.info("开始创建预约：{}", requestDTO);

        // 1. 查询排班信息
        MedicalSchedule schedule = scheduleMapper.selectById(requestDTO.getScheduleId());
        if (schedule == null) {
            throw new RuntimeException("排班不存在");
        }

        // 2. 检查排班状态
        if (schedule.getStatus() != 1) {
            throw new RuntimeException("该排班已取消");
        }

        // 3. 检查号源是否充足
        if (schedule.getRemainNum() <= 0) {
            throw new RuntimeException("号源已满，请选择其他时段");
        }

        // 4. 检查用户是否已预约相同时段
        LambdaQueryWrapper<MedicalAppointment> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(MedicalAppointment::getUserId, requestDTO.getUserId());
        checkWrapper.eq(MedicalAppointment::getScheduleId, requestDTO.getScheduleId());
        checkWrapper.in(MedicalAppointment::getStatus, "待确认", "已确认");
        Long count = appointmentMapper.selectCount(checkWrapper);
        if (count > 0) {
            throw new RuntimeException("您已预约该时段，请勿重复预约");
        }

        // 5. 扣减号源（乐观锁）
        schedule.setRemainNum(schedule.getRemainNum() - 1);
        int updateRows = scheduleMapper.updateById(schedule);
        if (updateRows == 0) {
            throw new RuntimeException("号源扣减失败，请重试");
        }

        // 6. 创建预约记录
        MedicalAppointment appointment = new MedicalAppointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setUserId(requestDTO.getUserId());
        appointment.setDoctorId(schedule.getDoctorId());
        appointment.setScheduleId(schedule.getId());
        appointment.setDeptId(schedule.getDeptId());
        appointment.setAppointmentDate(schedule.getScheduleDate());
        appointment.setTimeSlot(schedule.getTimeSlot());
        appointment.setAppointmentType(requestDTO.getAppointmentType() != null ? requestDTO.getAppointmentType() : "普通");
        appointment.setStatus("待确认");
        appointmentMapper.insert(appointment);

        // 7. 创建候诊队列记录
        MedicalQueue queue = new MedicalQueue();
        queue.setQueueNo(generateQueueNo(schedule.getScheduleDate()));
        queue.setAppointmentId(appointment.getId());
        queue.setUserId(requestDTO.getUserId());
        queue.setDoctorId(schedule.getDoctorId());
        queue.setDeptId(schedule.getDeptId());
        queue.setQueueDate(schedule.getScheduleDate());
        queue.setSymptom(requestDTO.getSymptom());
        queue.setStatus("候诊中");
        queueMapper.insert(queue);

        // 8. 构建返回结果
        AppointmentResultVO resultVO = new AppointmentResultVO();
        resultVO.setAppointmentId(appointment.getId());
        resultVO.setAppointmentNo(appointment.getAppointmentNo());
        resultVO.setQueueNo(queue.getQueueNo());
        
        // 查询医生和科室信息
        MedicalDoctor doctor = doctorMapper.selectById(schedule.getDoctorId());
        MedicalDepartment dept = departmentMapper.selectById(schedule.getDeptId());
        if (doctor != null) {
            SysUser doctorUser = userMapper.selectById(doctor.getUserId());
            resultVO.setDoctorName(doctorUser != null ? doctorUser.getRealName() : "医生" + doctor.getId());
        }
        if (dept != null) {
            resultVO.setDeptName(dept.getDeptName());
        }
        resultVO.setAppointmentDate(schedule.getScheduleDate().toString());
        resultVO.setTimeSlot(schedule.getTimeSlot());
        resultVO.setStatus("待确认");
        resultVO.setMessage("预约成功，请按时就诊");

        log.info("预约创建成功：{}", resultVO);
        return resultVO;
    }

    /**
     * 取消预约
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAppointment(Long appointmentId, Long userId) {
        log.info("取消预约：appointmentId={}, userId={}", appointmentId, userId);

        // 1. 查询预约信息
        MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        // 2. 验证是否是本人预约
        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("无权取消该预约");
        }

        // 3. 检查预约状态
        if (!"待确认".equals(appointment.getStatus())
                && !"已确认".equals(appointment.getStatus())
                && !"待审批".equals(appointment.getStatus())) {
            throw new RuntimeException("当前状态不允许取消");
        }

        // 4. 更新预约状态
        appointment.setStatus("已取消");
        appointmentMapper.updateById(appointment);

        // 5. 恢复号源
        MedicalSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
        if (schedule != null) {
            schedule.setRemainNum(schedule.getRemainNum() + 1);
            scheduleMapper.updateById(schedule);
        }

        // 6. 更新队列状态
        LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
        queueWrapper.eq(MedicalQueue::getAppointmentId, appointmentId);
        MedicalQueue queue = queueMapper.selectOne(queueWrapper);
        if (queue != null) {
            queue.setStatus("已取消");
            queueMapper.updateById(queue);
        }

        log.info("预约取消成功：{}", appointmentId);
    }

    /**
     * 确认预约（医生操作）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmAppointment(Long appointmentId, Long doctorId) {
        log.info("确认预约：appointmentId={}, doctorId={}", appointmentId, doctorId);

        // 1. 查询预约信息
        MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        // 2. 验证是否是本人的预约
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作该预约");
        }

        // 3. 检查预约状态
        if (!"待确认".equals(appointment.getStatus())) {
            throw new RuntimeException("只有待确认状态的预约才能确认");
        }

        // 4. 更新预约状态
        appointment.setStatus("已确认");
        appointmentMapper.updateById(appointment);

        // 5. 更新队列状态
        LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
        queueWrapper.eq(MedicalQueue::getAppointmentId, appointmentId);
        MedicalQueue queue = queueMapper.selectOne(queueWrapper);
        if (queue != null) {
            queue.setStatus("就诊中");
            queueMapper.updateById(queue);
        }

        log.info("预约确认成功：{}", appointmentId);
    }

    /**
     * 完成预约（就诊完成）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeAppointment(Long appointmentId, Long doctorId) {
        log.info("完成预约：appointmentId={}, doctorId={}", appointmentId, doctorId);

        // 1. 查询预约信息
        MedicalAppointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        // 2. 验证是否是本人的预约
        if (!appointment.getDoctorId().equals(doctorId)) {
            throw new RuntimeException("无权操作该预约");
        }

        // 3. 检查预约状态
        if (!"已确认".equals(appointment.getStatus())) {
            throw new RuntimeException("只有已确认状态的预约才能完成");
        }

        // 4. 更新预约状态
        appointment.setStatus("已完成");
        appointmentMapper.updateById(appointment);

        // 5. 更新队列状态
        LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
        queueWrapper.eq(MedicalQueue::getAppointmentId, appointmentId);
        MedicalQueue queue = queueMapper.selectOne(queueWrapper);
        if (queue != null) {
            queue.setStatus("已完成");
            queueMapper.updateById(queue);
        }

        log.info("预约完成成功：{}", appointmentId);
    }

    /**
     * 查询用户预约列表
     */
    @Override
    public List<MedicalAppointment> getUserAppointments(Long userId, String status) {
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalAppointment::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MedicalAppointment::getStatus, status);
        }
        wrapper.orderByDesc(MedicalAppointment::getCreateTime);
        return appointmentMapper.selectList(wrapper);
    }

    /**
     * 查询医生预约列表
     */
    @Override
    public List<MedicalAppointment> getDoctorAppointments(Long doctorId, LocalDate date) {
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalAppointment::getDoctorId, doctorId);
        wrapper.eq(MedicalAppointment::getAppointmentDate, date);
        wrapper.in(MedicalAppointment::getStatus, "待确认", "已确认");
        wrapper.orderByAsc(MedicalAppointment::getTimeSlot);
        return appointmentMapper.selectList(wrapper);
    }

    /**
     * 生成预约编号
     * 格式：AP + yyyyMMdd + 4位序号
     */
    private String generateAppointmentNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "AP" + dateStr;
        
        // 查询今天最后一个预约编号
        LambdaQueryWrapper<MedicalAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.likeRight(MedicalAppointment::getAppointmentNo, prefix);
        wrapper.orderByDesc(MedicalAppointment::getAppointmentNo);
        wrapper.last("LIMIT 1");
        MedicalAppointment lastAppointment = appointmentMapper.selectOne(wrapper);
        
        int sequence = 1;
        if (lastAppointment != null) {
            String lastNo = lastAppointment.getAppointmentNo();
            sequence = Integer.parseInt(lastNo.substring(lastNo.length() - 4)) + 1;
        }
        
        return prefix + String.format("%04d", sequence);
    }

    /**
     * 生成排队号（3位序号，日期由 queue_date 字段存储，总长不超过10）
     */
    private String generateQueueNo(LocalDate date) {
        LambdaQueryWrapper<MedicalQueue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalQueue::getQueueDate, date);
        wrapper.orderByDesc(MedicalQueue::getQueueNo);
        wrapper.last("LIMIT 1");
        MedicalQueue lastQueue = queueMapper.selectOne(wrapper);

        int sequence = 1;
        if (lastQueue != null && lastQueue.getQueueNo() != null) {
            String lastNo = lastQueue.getQueueNo();
            try {
                sequence = Integer.parseInt(lastNo) + 1;
            } catch (NumberFormatException e) {
                if (lastNo.length() > 3) {
                    sequence = Integer.parseInt(lastNo.substring(lastNo.length() - 3)) + 1;
                }
            }
        }
        if (sequence > 999) {
            throw new RuntimeException("当日排队号已满，请明日再试");
        }
        return String.format("%03d", sequence);
    }
}
