package com.campus.medical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.*;
import com.campus.medical.mapper.*;
import com.campus.medical.service.DashboardBusinessService;
import com.campus.medical.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作台业务服务实现类
 */
@Slf4j
@Service
public class DashboardBusinessServiceImpl implements DashboardBusinessService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private MedicalAppointmentMapper appointmentMapper;

    @Autowired
    private MedicalOrderMapper orderMapper;

    @Autowired
    private MedicalPrescriptionMapper prescriptionMapper;

    @Autowired
    private MedicalSickLeaveMapper sickLeaveMapper;

    @Autowired
    private MedicalRecordMapper recordMapper;

    @Autowired
    private MedicalQueueMapper queueMapper;

    @Autowired
    private MedicalApprovalMapper approvalMapper;

    @Autowired
    private MedicalScheduleMapper scheduleMapper;

    @Autowired
    private MedicalDoctorMapper doctorMapper;

    /**
     * 获取工作台核心数据（根据角色动态返回）
     */
    @Override
    public DashboardVO getDashboardData(Long userId, String roleCode) {
        DashboardVO dashboard = new DashboardVO();

        // 1. 获取用户信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 2. 设置欢迎语和角色
        dashboard.setWelcome("欢迎回来，" + user.getRealName() + "！");
        dashboard.setRole(getRoleName(roleCode));

        // 3. 根据角色返回不同的统计数据
        switch (roleCode) {
            case "student":
                dashboard.setStatistics(getStudentStatistics(userId));
                break;
            case "doctor":
                dashboard.setStatistics(getDoctorStatistics(userId));
                break;
            case "pharmacist":
                dashboard.setStatistics(getPharmacistStatistics(userId));
                break;
            case "finance":
                dashboard.setStatistics(getFinanceStatistics(userId));
                break;
            case "approver":
                dashboard.setStatistics(getApproverStatistics(userId));
                break;
            case "leader":
                dashboard.setStatistics(getLeaderStatistics(userId));
                break;
            case "admin":
                dashboard.setStatistics(getAdminStatistics(userId));
                break;
            default:
                dashboard.setStatistics(new ArrayList<>());
        }

        // 4. 统计待办和已办数量
        dashboard.setTodoCount(calculateTodoCount(userId, roleCode));
        dashboard.setDoneCount(calculateDoneCount(userId, roleCode));

        // 5. 获取待办事项列表（最近5条）
        dashboard.setTodoList(getTodoList(userId, roleCode).subList(0, Math.min(5, dashboard.getTodoCount())));

        // 6. 获取已办事项列表（最近5条）
        dashboard.setDoneList(getDoneList(userId, roleCode).subList(0, Math.min(5, dashboard.getDoneCount())));

        // 7. 设置快捷操作
        dashboard.setQuickActions(getQuickActions(roleCode));

        return dashboard;
    }

    /**
     * 获取待办事项列表
     */
    @Override
    public List<DashboardVO.TodoItemVO> getTodoList(Long userId, String roleCode) {
        List<DashboardVO.TodoItemVO> todoList = new ArrayList<>();

        switch (roleCode) {
            case "student":
                // 待缴费订单
                LambdaQueryWrapper<MedicalOrder> orderWrapper = new LambdaQueryWrapper<>();
                orderWrapper.eq(MedicalOrder::getUserId, userId);
                orderWrapper.eq(MedicalOrder::getStatus, "待支付");
                List<MedicalOrder> unpaidOrders = orderMapper.selectList(orderWrapper);
                if (!unpaidOrders.isEmpty()) {
                    DashboardVO.TodoItemVO todo = new DashboardVO.TodoItemVO();
                    todo.setId(unpaidOrders.get(0).getId());
                    todo.setTitle("待缴费订单");
                    todo.setContent("共" + unpaidOrders.size() + "笔订单待缴费");
                    todo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    todo.setUrl("/student/payment");
                    todoList.add(todo);
                }

                // 待就诊预约
                LambdaQueryWrapper<MedicalAppointment> apptWrapper = new LambdaQueryWrapper<>();
                apptWrapper.eq(MedicalAppointment::getUserId, userId);
                apptWrapper.eq(MedicalAppointment::getStatus, "待就诊");
                List<MedicalAppointment> pendingAppts = appointmentMapper.selectList(apptWrapper);
                if (!pendingAppts.isEmpty()) {
                    DashboardVO.TodoItemVO todo = new DashboardVO.TodoItemVO();
                    todo.setId(pendingAppts.get(0).getId());
                    todo.setTitle("待就诊预约");
                    MedicalAppointment appt = pendingAppts.get(0);
                    todo.setContent(appt.getAppointmentDate() + " " + appt.getTimeSlot());
                    todo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    todo.setUrl("/student/appointment");
                    todoList.add(todo);
                }

                // 待审核病假申请
                LambdaQueryWrapper<MedicalSickLeave> sickWrapper = new LambdaQueryWrapper<>();
                sickWrapper.eq(MedicalSickLeave::getUserId, userId);
                sickWrapper.eq(MedicalSickLeave::getStatus, 1);
                List<MedicalSickLeave> pendingSickLeaves = sickLeaveMapper.selectList(sickWrapper);
                if (!pendingSickLeaves.isEmpty()) {
                    DashboardVO.TodoItemVO todo = new DashboardVO.TodoItemVO();
                    todo.setId(pendingSickLeaves.get(0).getId());
                    todo.setTitle("病假申请审核中");
                    todo.setContent(pendingSickLeaves.get(0).getLeaveNo());
                    todo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    todo.setUrl("/student/sickleave");
                    todoList.add(todo);
                }
                break;

            case "doctor":
                // 待接诊患者
                Long doctorProfileId = resolveDoctorId(userId);
                LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
                queueWrapper.eq(MedicalQueue::getDoctorId, doctorProfileId);
                queueWrapper.eq(MedicalQueue::getStatus, "候诊中");
                List<MedicalQueue> waitingQueues = queueMapper.selectList(queueWrapper);
                if (!waitingQueues.isEmpty()) {
                    DashboardVO.TodoItemVO todo = new DashboardVO.TodoItemVO();
                    todo.setId(waitingQueues.get(0).getId());
                    todo.setTitle("待接诊患者");
                    todo.setContent("当前候诊" + waitingQueues.size() + "人");
                    todo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    todo.setUrl("/doctor/queue");
                    todoList.add(todo);
                }
                break;

            case "approver":
                // 待审批病假
                LambdaQueryWrapper<MedicalSickLeave> sickApproveWrapper = new LambdaQueryWrapper<>();
                sickApproveWrapper.eq(MedicalSickLeave::getStatus, 1);
                List<MedicalSickLeave> pendingSickApproves = sickLeaveMapper.selectList(sickApproveWrapper);
                if (!pendingSickApproves.isEmpty()) {
                    DashboardVO.TodoItemVO todo = new DashboardVO.TodoItemVO();
                    todo.setId(pendingSickApproves.get(0).getId());
                    todo.setTitle("待审批病假申请");
                    todo.setContent(pendingSickApproves.get(0).getLeaveNo());
                    todo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    todo.setUrl("/approver/sickleave");
                    todoList.add(todo);
                }

                // 待审批特殊挂号
                LambdaQueryWrapper<MedicalApproval> regWrapper = new LambdaQueryWrapper<>();
                regWrapper.eq(MedicalApproval::getApprovalType, "特殊挂号");
                regWrapper.eq(MedicalApproval::getStatus, "待审核");
                List<MedicalApproval> pendingRegApprovals = approvalMapper.selectList(regWrapper);
                if (!pendingRegApprovals.isEmpty()) {
                    DashboardVO.TodoItemVO todo = new DashboardVO.TodoItemVO();
                    todo.setId(pendingRegApprovals.get(0).getId());
                    todo.setTitle("待审批挂号申请");
                    todo.setContent(pendingRegApprovals.get(0).getApprovalNo());
                    todo.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    todo.setUrl("/approver/registration");
                    todoList.add(todo);
                }
                break;

            default:
                break;
        }

        return todoList;
    }

    /**
     * 获取已办事项列表
     */
    @Override
    public List<DashboardVO.DoneItemVO> getDoneList(Long userId, String roleCode) {
        List<DashboardVO.DoneItemVO> doneList = new ArrayList<>();

        switch (roleCode) {
            case "student":
                // 已完成的就诊记录
                LambdaQueryWrapper<MedicalRecord> recordWrapper = new LambdaQueryWrapper<>();
                recordWrapper.eq(MedicalRecord::getUserId, userId);
                recordWrapper.orderByDesc(MedicalRecord::getCreateTime);
                recordWrapper.last("LIMIT 3");
                List<MedicalRecord> records = recordMapper.selectList(recordWrapper);
                for (MedicalRecord record : records) {
                    DashboardVO.DoneItemVO done = new DashboardVO.DoneItemVO();
                    done.setId(record.getId());
                    done.setTitle("就诊完成");
                    done.setContent(record.getDiagnosis());
                    done.setDoneTime(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    done.setUrl("/student/records");
                    doneList.add(done);
                }
                break;

            case "doctor":
                // 已接诊记录
                LambdaQueryWrapper<MedicalRecord> doctorRecordWrapper = new LambdaQueryWrapper<>();
                doctorRecordWrapper.eq(MedicalRecord::getDoctorId, userId);
                doctorRecordWrapper.orderByDesc(MedicalRecord::getCreateTime);
                doctorRecordWrapper.last("LIMIT 3");
                List<MedicalRecord> doctorRecords = recordMapper.selectList(doctorRecordWrapper);
                for (MedicalRecord record : doctorRecords) {
                    DashboardVO.DoneItemVO done = new DashboardVO.DoneItemVO();
                    done.setId(record.getId());
                    done.setTitle("接诊完成");
                    done.setContent(record.getDiagnosis());
                    done.setDoneTime(record.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    done.setUrl("/doctor/records");
                    doneList.add(done);
                }
                break;

            default:
                break;
        }

        return doneList;
    }

    // ========== 私有辅助方法 ==========

    /**
     * 获取角色名称
     */
    private String getRoleName(String roleCode) {
        switch (roleCode) {
            case "student": return "学生";
            case "doctor": return "医生";
            case "pharmacist": return "药师";
            case "finance": return "财务";
            case "approver": return "审批管理员";
            case "leader": return "校领导";
            case "admin": return "系统管理员";
            default: return "用户";
        }
    }

    /**
     * 学生统计数据
     */
    private List<DashboardVO.StatisticCardVO> getStudentStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();

        // 待缴费
        LambdaQueryWrapper<MedicalOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(MedicalOrder::getUserId, userId);
        orderWrapper.eq(MedicalOrder::getStatus, "待支付");
        Long unpaidCount = orderMapper.selectCount(orderWrapper);
        statistics.add(createStatisticCard("待缴费", unpaidCount.intValue()));

        // 待就诊
        LambdaQueryWrapper<MedicalAppointment> apptWrapper = new LambdaQueryWrapper<>();
        apptWrapper.eq(MedicalAppointment::getUserId, userId);
        apptWrapper.eq(MedicalAppointment::getStatus, "待就诊");
        Long pendingApptCount = appointmentMapper.selectCount(apptWrapper);
        statistics.add(createStatisticCard("待就诊", pendingApptCount.intValue()));

        // 已完成
        LambdaQueryWrapper<MedicalRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(MedicalRecord::getUserId, userId);
        Long completedCount = recordMapper.selectCount(recordWrapper);
        statistics.add(createStatisticCard("已完成", completedCount.intValue()));

        // 病假申请
        LambdaQueryWrapper<MedicalSickLeave> sickWrapper = new LambdaQueryWrapper<>();
        sickWrapper.eq(MedicalSickLeave::getUserId, userId);
        Long sickLeaveCount = sickLeaveMapper.selectCount(sickWrapper);
        statistics.add(createStatisticCard("病假申请", sickLeaveCount.intValue()));

        return statistics;
    }

    /**
     * 医生统计数据
     */
    private List<DashboardVO.StatisticCardVO> getDoctorStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();
        Long doctorProfileId = resolveDoctorId(userId);

        // 待接诊
        LambdaQueryWrapper<MedicalQueue> queueWrapper = new LambdaQueryWrapper<>();
        queueWrapper.eq(MedicalQueue::getDoctorId, doctorProfileId);
        queueWrapper.eq(MedicalQueue::getStatus, "候诊中");
        Long waitingCount = queueMapper.selectCount(queueWrapper);
        statistics.add(createStatisticCard("待接诊", waitingCount.intValue()));

        // 已接诊
        LambdaQueryWrapper<MedicalRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(MedicalRecord::getDoctorId, doctorProfileId);
        Long receivedCount = recordMapper.selectCount(recordWrapper);
        statistics.add(createStatisticCard("已接诊", receivedCount.intValue()));

        // 今日处方
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<MedicalPrescription> prescriptionWrapper = new LambdaQueryWrapper<>();
        prescriptionWrapper.eq(MedicalPrescription::getDoctorId, doctorProfileId);
        prescriptionWrapper.ge(MedicalPrescription::getCreateTime, todayStart);
        Long todayPrescriptionCount = prescriptionMapper.selectCount(prescriptionWrapper);
        statistics.add(createStatisticCard("今日处方", todayPrescriptionCount.intValue()));

        // 排班天数
        LambdaQueryWrapper<MedicalSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(MedicalSchedule::getDoctorId, doctorProfileId);
        Long scheduleDays = scheduleMapper.selectCount(scheduleWrapper);
        statistics.add(createStatisticCard("排班天数", scheduleDays.intValue()));

        return statistics;
    }

    /**
     * 药师统计数据
     */
    private List<DashboardVO.StatisticCardVO> getPharmacistStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();

        // 待配药
        LambdaQueryWrapper<MedicalPrescription> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(MedicalPrescription::getStatus, "待配药");
        Long pendingCount = prescriptionMapper.selectCount(pendingWrapper);
        statistics.add(createStatisticCard("待配药", pendingCount.intValue()));

        // 已发药
        LambdaQueryWrapper<MedicalPrescription> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(MedicalPrescription::getStatus, "已完成");
        Long completedCount = prescriptionMapper.selectCount(completedWrapper);
        statistics.add(createStatisticCard("已发药", completedCount.intValue()));

        // 药品种类（TODO: 需要查询药品表）
        statistics.add(createStatisticCard("药品种类", 0));

        // 库存预警（TODO: 需要查询库存预警）
        statistics.add(createStatisticCard("库存预警", 0));

        return statistics;
    }

    /**
     * 财务统计数据
     */
    private List<DashboardVO.StatisticCardVO> getFinanceStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();

        // 今日收费
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<MedicalOrder> todayWrapper = new LambdaQueryWrapper<>();
        todayWrapper.eq(MedicalOrder::getStatus, "已支付");
        todayWrapper.ge(MedicalOrder::getPayTime, todayStart);
        List<MedicalOrder> todayOrders = orderMapper.selectList(todayWrapper);
        statistics.add(createStatisticCard("今日收费", todayOrders.size()));

        // 本月收入
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LambdaQueryWrapper<MedicalOrder> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(MedicalOrder::getStatus, "已支付");
        monthWrapper.ge(MedicalOrder::getPayTime, monthStart);
        Long monthCount = orderMapper.selectCount(monthWrapper);
        statistics.add(createStatisticCard("本月收入", monthCount.intValue()));

        // 待缴费订单
        LambdaQueryWrapper<MedicalOrder> unpaidWrapper = new LambdaQueryWrapper<>();
        unpaidWrapper.eq(MedicalOrder::getStatus, "待支付");
        Long unpaidCount = orderMapper.selectCount(unpaidWrapper);
        statistics.add(createStatisticCard("待缴费订单", unpaidCount.intValue()));

        // 对账异常（TODO: 需要对账逻辑）
        statistics.add(createStatisticCard("对账异常", 0));

        return statistics;
    }

    /**
     * 审批员统计数据
     */
    private List<DashboardVO.StatisticCardVO> getApproverStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();

        // 今日已审
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<MedicalSickLeave> todaySickWrapper = new LambdaQueryWrapper<>();
        todaySickWrapper.eq(MedicalSickLeave::getStatus, 2);
        todaySickWrapper.ge(MedicalSickLeave::getAuditTime, todayStart);
        Long todayApprovedCount = sickLeaveMapper.selectCount(todaySickWrapper);
        statistics.add(createStatisticCard("今日已审", todayApprovedCount.intValue()));

        // 待审批病假
        LambdaQueryWrapper<MedicalSickLeave> pendingSickWrapper = new LambdaQueryWrapper<>();
        pendingSickWrapper.eq(MedicalSickLeave::getStatus, 1);
        Long pendingSickCount = sickLeaveMapper.selectCount(pendingSickWrapper);
        statistics.add(createStatisticCard("待审批病假", pendingSickCount.intValue()));

        // 待审批退费
        LambdaQueryWrapper<MedicalApproval> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.eq(MedicalApproval::getApprovalType, "退费");
        refundWrapper.eq(MedicalApproval::getStatus, "待审核");
        Long pendingRefundCount = approvalMapper.selectCount(refundWrapper);
        statistics.add(createStatisticCard("待审批退费", pendingRefundCount.intValue()));

        // 待审批挂号
        LambdaQueryWrapper<MedicalApproval> regWrapper = new LambdaQueryWrapper<>();
        regWrapper.eq(MedicalApproval::getApprovalType, "特殊挂号");
        regWrapper.eq(MedicalApproval::getStatus, "待审核");
        Long pendingRegCount = approvalMapper.selectCount(regWrapper);
        statistics.add(createStatisticCard("待审批挂号", pendingRegCount.intValue()));

        return statistics;
    }

    /**
     * 校领导统计数据
     */
    private List<DashboardVO.StatisticCardVO> getLeaderStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();

        // 今日就诊量
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LambdaQueryWrapper<MedicalRecord> todayRecordWrapper = new LambdaQueryWrapper<>();
        todayRecordWrapper.ge(MedicalRecord::getCreateTime, todayStart);
        Long todayVisitCount = recordMapper.selectCount(todayRecordWrapper);
        statistics.add(createStatisticCard("今日就诊量", todayVisitCount.intValue()));

        // 本月收入（订单数）
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LambdaQueryWrapper<MedicalOrder> monthWrapper = new LambdaQueryWrapper<>();
        monthWrapper.eq(MedicalOrder::getStatus, "已支付");
        monthWrapper.ge(MedicalOrder::getPayTime, monthStart);
        Long monthIncomeCount = orderMapper.selectCount(monthWrapper);
        statistics.add(createStatisticCard("本月收入", monthIncomeCount.intValue()));

        // 药品消耗总额（TODO: 需要药品消耗统计）
        statistics.add(createStatisticCard("药品消耗", 0));

        // 病假申请总数
        Long totalSickCount = sickLeaveMapper.selectCount(null);
        statistics.add(createStatisticCard("病假申请", totalSickCount.intValue()));

        return statistics;
    }

    /**
     * 管理员统计数据
     */
    private List<DashboardVO.StatisticCardVO> getAdminStatistics(Long userId) {
        List<DashboardVO.StatisticCardVO> statistics = new ArrayList<>();

        // 用户总数
        Long userCount = userMapper.selectCount(null);
        statistics.add(createStatisticCard("用户总数", userCount.intValue()));

        // 角色数量（TODO: 需要查询角色表）
        statistics.add(createStatisticCard("角色数量", 0));

        // 今日登录（TODO: 需要登录日志）
        statistics.add(createStatisticCard("今日登录", 0));

        // 异常日志（TODO: 需要日志统计）
        statistics.add(createStatisticCard("异常日志", 0));

        return statistics;
    }

    /**
     * 创建统计卡片
     */
    private DashboardVO.StatisticCardVO createStatisticCard(String name, Integer value) {
        DashboardVO.StatisticCardVO card = new DashboardVO.StatisticCardVO();
        card.setName(name);
        card.setValue(value);
        return card;
    }

    /**
     * 计算待办数量
     */
    private Integer calculateTodoCount(Long userId, String roleCode) {
        return getTodoList(userId, roleCode).size();
    }

    /**
     * 计算已办数量
     */
    private Integer calculateDoneCount(Long userId, String roleCode) {
        return getDoneList(userId, roleCode).size();
    }

    /**
     * 获取快捷操作（根据角色）
     */
    private List<DashboardVO.QuickActionVO> getQuickActions(String roleCode) {
        List<DashboardVO.QuickActionVO> quickActions = new ArrayList<>();

        switch (roleCode) {
            case "student":
                quickActions.add(createQuickAction("预约挂号", "📅", "/student/appointment"));
                quickActions.add(createQuickAction("在线缴费", "💰", "/student/payment"));
                quickActions.add(createQuickAction("病假申请", "📝", "/student/sickleave"));
                quickActions.add(createQuickAction("健康档案", "📋", "/student/health"));
                break;
            case "doctor":
                quickActions.add(createQuickAction("候诊队列", "👥", "/doctor/queue"));
                quickActions.add(createQuickAction("患者接诊", "🩺", "/doctor/receive"));
                quickActions.add(createQuickAction("开具处方", "💊", "/doctor/prescription"));
                quickActions.add(createQuickAction("个人排班", "📅", "/doctor/schedule"));
                break;
            case "pharmacist":
                quickActions.add(createQuickAction("处方配药", "📦", "/pharmacist/dispensing"));
                quickActions.add(createQuickAction("药品入库", "📥", "/pharmacist/instock"));
                quickActions.add(createQuickAction("库存管理", "📊", "/pharmacist/stock"));
                break;
            case "finance":
                quickActions.add(createQuickAction("订单管理", "📑", "/finance/order"));
                quickActions.add(createQuickAction("退费审核", "🔙", "/finance/refund"));
                quickActions.add(createQuickAction("收支统计", "📈", "/finance/statistics"));
                quickActions.add(createQuickAction("报表导出", "📤", "/finance/export"));
                break;
            case "approver":
                quickActions.add(createQuickAction("病假审批", "✔️", "/approver/sickleave"));
                quickActions.add(createQuickAction("挂号审批", "🎫", "/approver/registration"));
                quickActions.add(createQuickAction("退费审批", "💸", "/approver/refund"));
                quickActions.add(createQuickAction("审批记录", "📜", "/approver/history"));
                break;
            case "leader":
                quickActions.add(createQuickAction("就诊统计", "📊", "/leader/visit-stat"));
                quickActions.add(createQuickAction("疾病排行", "🏥", "/leader/disease-rank"));
                quickActions.add(createQuickAction("药品消耗", "💊", "/leader/medicine-stat"));
                quickActions.add(createQuickAction("健康预警", "⚠️", "/leader/health-warning"));
                break;
            case "admin":
                quickActions.add(createQuickAction("用户管理", "👤", "/admin/user"));
                quickActions.add(createQuickAction("角色管理", "🎭", "/admin/role"));
                quickActions.add(createQuickAction("排班管理", "📅", "/admin/schedule"));
                quickActions.add(createQuickAction("基础数据", "📚", "/admin/dictionary"));
                break;
            default:
                break;
        }

        return quickActions;
    }

    /**
     * 创建快捷操作
     */
    private DashboardVO.QuickActionVO createQuickAction(String name, String icon, String url) {
        DashboardVO.QuickActionVO action = new DashboardVO.QuickActionVO();
        action.setName(name);
        action.setIcon(icon);
        action.setUrl(url);
        return action;
    }

    /**
     * 将系统用户ID解析为医生档案ID
     */
    private Long resolveDoctorId(Long userId) {
        LambdaQueryWrapper<MedicalDoctor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalDoctor::getUserId, userId);
        MedicalDoctor doctor = doctorMapper.selectOne(wrapper);
        return doctor != null ? doctor.getId() : userId;
    }
}
