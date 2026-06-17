package com.campus.medical.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.medical.entity.MedicalDepartment;
import com.campus.medical.entity.MedicalDoctor;
import com.campus.medical.entity.MedicalHealthProfile;
import com.campus.medical.entity.MedicalMedicine;
import com.campus.medical.entity.MedicalSchedule;
import com.campus.medical.entity.SysDictData;
import com.campus.medical.entity.SysDictType;
import com.campus.medical.entity.SysMenu;
import com.campus.medical.entity.SysRole;
import com.campus.medical.entity.SysRoleMenu;
import com.campus.medical.entity.SysUser;
import com.campus.medical.mapper.MedicalDepartmentMapper;
import com.campus.medical.mapper.MedicalDoctorMapper;
import com.campus.medical.mapper.MedicalHealthProfileMapper;
import com.campus.medical.mapper.MedicalMedicineMapper;
import com.campus.medical.mapper.MedicalScheduleMapper;
import com.campus.medical.mapper.SysDictDataMapper;
import com.campus.medical.mapper.SysDictTypeMapper;
import com.campus.medical.mapper.SysMenuMapper;
import com.campus.medical.mapper.SysRoleMapper;
import com.campus.medical.mapper.SysRoleMenuMapper;
import com.campus.medical.mapper.SysUserMapper;
import com.campus.medical.utils.PasswordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化配置
 */
@Slf4j
@Component
public class DataInit implements CommandLineRunner {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private MedicalDepartmentMapper departmentMapper;

    @Autowired
    private MedicalDoctorMapper doctorMapper;

    @Autowired
    private MedicalScheduleMapper scheduleMapper;

    @Autowired
    private MedicalMedicineMapper medicineMapper;

    @Autowired
    private MedicalHealthProfileMapper healthProfileMapper;

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Override
    public void run(String... args) {
        initRoles();
        initUsers();
        initDepartments();
        initMenus();
        initRoleMenus();
        initDictData();
        initDoctors();
        repairDoctorProfiles();
        initSchedules();
        initMedicines();
        repairMedicineData();
        removeDeprecatedCheckMenu();
        removeDeprecatedMenus();
        initHealthProfiles();
    }

    private void initRoles() {
        List<String[]> roles = Arrays.asList(
                new String[]{"student", "学生", "学生角色"},
                new String[]{"doctor", "医生", "医生角色"},
                new String[]{"pharmacist", "药师", "药师角色"},
                new String[]{"finance", "财务人员", "财务人员角色"},
                new String[]{"approver", "审批管理员", "审批管理员角色"},
                new String[]{"leader", "校领导", "校领导角色"},
                new String[]{"admin", "系统管理员", "系统管理员角色"}
        );

        for (String[] roleData : roles) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, roleData[0]);
            if (roleMapper.selectCount(wrapper) == 0) {
                SysRole role = new SysRole();
                role.setRoleCode(roleData[0]);
                role.setRoleName(roleData[1]);
                role.setDescription(roleData[2]);
                role.setStatus(1);
                roleMapper.insert(role);
                log.info("初始化角色: {}", roleData[1]);
            }
        }
    }

    private void initUsers() {
        Long studentRoleId = getRoleId("student");
        Long doctorRoleId = getRoleId("doctor");
        Long pharmacistRoleId = getRoleId("pharmacist");
        Long financeRoleId = getRoleId("finance");
        Long approverRoleId = getRoleId("approver");
        Long leaderRoleId = getRoleId("leader");
        Long adminRoleId = getRoleId("admin");

        createOrUpdateUser("student", "测试学生", studentRoleId, "计算机学院", "计算机202班", "13800138001");
        createOrUpdateUser("doctor", "王医生", doctorRoleId, "内科", "内科", "13800138002");
        createOrUpdateUser("pharmacist", "李药师", pharmacistRoleId, "药房", "药房", "13800138004");
        createOrUpdateUser("finance", "张财务", financeRoleId, "财务科", "财务科", "13800138005");
        createOrUpdateUser("approver", "刘审批", approverRoleId, "学生处", "学生处", "13800138006");
        createOrUpdateUser("leader", "陈校长", leaderRoleId, "校办", "校办", "13800138007");
        createOrUpdateUser("admin", "系统管理员", adminRoleId, "信息中心", "信息中心", "13800138003");
    }

    private void initDepartments() {
        if (departmentMapper.selectCount(null) > 0) {
            return;
        }

        String[][] departments = {
                {"内科", "internal", "内科诊疗", "1"},
                {"外科", "surgical", "外科诊疗", "2"},
                {"口腔科", "dental", "口腔科诊疗", "3"},
                {"眼科", "ophthalmology", "眼科诊疗", "4"},
                {"皮肤科", "dermatology", "皮肤科诊疗", "5"}
        };

        for (String[] dept : departments) {
            MedicalDepartment department = new MedicalDepartment();
            department.setDeptName(dept[0]);
            department.setDeptCode(dept[1]);
            department.setDescription(dept[2]);
            department.setSort(Integer.parseInt(dept[3]));
            department.setStatus(1);
            departmentMapper.insert(department);
        }
        log.info("初始化科室数据完成");
    }

    private void initMenus() {
        if (menuMapper.selectCount(null) > 0) {
            return;
        }
        Object[][] menus = {
                {1L, 0L, "首页", "/home", "HomeFilled", 1, 1},
                {100L, 0L, "学生模块", null, "User", 0, 10},
                {101L, 100L, "预约挂号", "/student/register", "Calendar", 1, 11},
                {102L, 100L, "在线缴费", "/student/pay", "Money", 1, 12},
                {103L, 100L, "诊疗记录", "/student/record", "Document", 1, 13},
                {104L, 100L, "病假申请", "/student/sickLeave", "Tickets", 1, 14},
                {105L, 100L, "健康档案", "/student/healthRecord", "Notebook", 1, 15},
                {200L, 0L, "医生模块", null, "FirstAidKit", 0, 20},
                {201L, 200L, "候诊队列", "/doctor/queue", "User", 1, 21},
                {202L, 200L, "病历书写", "/doctor/medicalRecord", "Notebook", 1, 22},
                {203L, 200L, "处方开具", "/doctor/prescription", "Reading", 1, 23},
                {204L, 200L, "个人排班", "/doctor/schedule", "Clock", 1, 24},
                {300L, 0L, "药师模块", null, "Box", 0, 30},
                {301L, 300L, "处方配药", "/pharmacist/prescription", "List", 1, 31},
                {302L, 300L, "药品入库", "/pharmacist/drugIn", "Box", 1, 32},
                {303L, 300L, "库存管理", "/pharmacist/stock", "Warehouse", 1, 33},
                {400L, 0L, "审批模块", null, "CircleCheck", 0, 40},
                {401L, 400L, "病假审批", "/approver/sickLeave", "Tickets", 1, 41},
                {403L, 400L, "退号审批", "/approver/cancelRegister", "CloseBold", 1, 43},
                {500L, 0L, "财务模块", null, "Money", 0, 50},
                {501L, 500L, "收费订单管理", "/finance/order", "Money", 1, 51},
                {502L, 500L, "财务对账", "/finance/reconciliation", "DataAnalysis", 1, 52},
                {503L, 500L, "未缴费催缴", "/finance/reminder", "Bell", 1, 53},
                {600L, 0L, "校领导模块", null, "TrendCharts", 0, 60},
                {601L, 600L, "就诊量统计", "/leader/visitStats", "TrendCharts", 1, 61},
                {602L, 600L, "疾病谱分析", "/leader/diseaseStats", "DataAnalysis", 1, 62},
                {603L, 600L, "药品消耗统计", "/leader/drugStats", "Box", 1, 63},
                {604L, 600L, "病假数据统计", "/leader/sickLeaveStats", "Tickets", 1, 64},
                {700L, 0L, "系统管理", null, "Setting", 0, 70},
                {701L, 700L, "用户管理", "/admin/user", "UserFilled", 1, 71},
                {702L, 700L, "角色权限管理", "/admin/role", "Lock", 1, 72},
                {703L, 700L, "医生排班管理", "/admin/schedule", "Calendar", 1, 73},
                {704L, 700L, "基础数据管理", "/admin/dictionary", "Collection", 1, 74}
        };
        for (Object[] item : menus) {
            SysMenu menu = new SysMenu();
            menu.setId((Long) item[0]);
            menu.setParentId((Long) item[1]);
            menu.setMenuName((String) item[2]);
            menu.setMenuPath((String) item[3]);
            menu.setMenuIcon((String) item[4]);
            menu.setMenuType((Integer) item[5]);
            menu.setSort((Integer) item[6]);
            menu.setStatus(1);
            menuMapper.insert(menu);
        }
        log.info("初始化系统菜单完成");
    }

    private void initRoleMenus() {
        if (roleMenuMapper.selectCount(null) > 0) {
            return;
        }
        assignMenus("student", new Long[]{1L, 100L, 101L, 102L, 103L, 104L, 105L});
        assignMenus("doctor", new Long[]{1L, 200L, 201L, 202L, 203L, 204L});
        assignMenus("pharmacist", new Long[]{1L, 300L, 301L, 302L, 303L});
        assignMenus("approver", new Long[]{1L, 400L, 401L, 403L});
        assignMenus("finance", new Long[]{1L, 500L, 501L, 502L, 503L});
        assignMenus("leader", new Long[]{1L, 600L, 601L, 602L, 603L, 604L});
        assignMenus("admin", new Long[]{1L, 700L, 701L, 702L, 703L, 704L});
        log.info("初始化角色菜单权限完成");
    }

    private void assignMenus(String roleCode, Long[] menuIds) {
        Long roleId = getRoleId(roleCode);
        if (roleId == null) {
            return;
        }
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    private void initDictData() {
        ensureDictType("drug_category", "药品分类");
        ensureDictType("disease", "病种管理");
        if (dictDataMapper.selectCount(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, "drug_category")) > 0) {
            return;
        }
        String[][] drugCategories = {
                {"KSS", "抗生素类", "抗生素类药品"},
                {"GM", "感冒用药", "感冒类药品"},
                {"ZT", "止痛药", "止痛类药品"},
                {"YW", "外用药", "外用类药品"}
        };
        int sort = 1;
        for (String[] item : drugCategories) {
            SysDictData data = new SysDictData();
            data.setDictType("drug_category");
            data.setDictValue(item[0]);
            data.setDictLabel(item[1]);
            data.setRemark(item[2]);
            data.setSort(sort++);
            data.setStatus(1);
            dictDataMapper.insert(data);
        }
        String[][] diseases = {
                {"NK001", "急性上呼吸道感染", "内科疾病", "常见感冒症状"},
                {"WK001", "软组织损伤", "外科疾病", "扭伤、拉伤等"},
                {"KQ001", "牙龈炎", "口腔疾病", "牙龈发炎"},
                {"YK001", "结膜炎", "眼科疾病", "眼部发炎"}
        };
        sort = 1;
        for (String[] item : diseases) {
            SysDictData data = new SysDictData();
            data.setDictType("disease");
            data.setDictValue(item[0]);
            data.setDictLabel(item[1]);
            data.setCategory(item[2]);
            data.setRemark(item[3]);
            data.setSort(sort++);
            data.setStatus(1);
            dictDataMapper.insert(data);
        }
        log.info("初始化基础字典数据完成");
    }

    private void ensureDictType(String dictTypeCode, String dictTypeName) {
        Long count = dictTypeMapper.selectCount(new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictTypeCode));
        if (count == 0) {
            SysDictType dictType = new SysDictType();
            dictType.setDictType(dictTypeCode);
            dictType.setDictName(dictTypeName);
            dictType.setStatus(1);
            dictTypeMapper.insert(dictType);
        }
    }

    private void repairDoctorProfiles() {
        Long doctorRoleId = getRoleId("doctor");
        if (doctorRoleId == null) {
            return;
        }
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getRoleId, doctorRoleId);
        List<SysUser> doctors = userMapper.selectList(userWrapper);
        for (SysUser user : doctors) {
            LambdaQueryWrapper<MedicalDoctor> doctorWrapper = new LambdaQueryWrapper<>();
            doctorWrapper.eq(MedicalDoctor::getUserId, user.getId());
            if (doctorMapper.selectCount(doctorWrapper) > 0) {
                continue;
            }
            MedicalDepartment dept = getDepartmentByName(user.getDepartment());
            if (dept == null) {
                dept = departmentMapper.selectOne(new LambdaQueryWrapper<MedicalDepartment>().last("LIMIT 1"));
            }
            if (dept == null) {
                continue;
            }
            MedicalDoctor doctor = new MedicalDoctor();
            doctor.setUserId(user.getId());
            doctor.setDeptId(dept.getId());
            doctor.setTitle("医师");
            doctor.setSpecialty(dept.getDeptName() + "诊疗");
            doctor.setIntroduction(user.getRealName() + "，" + dept.getDeptName() + "医生");
            doctorMapper.insert(doctor);
            log.info("修复医生档案: {}", user.getUsername());
        }
    }

    private void initDoctors() {
        if (doctorMapper.selectCount(null) > 0) {
            return;
        }

        SysUser doctorUser = getUserByUsername("doctor");
        if (doctorUser == null) {
            return;
        }

        MedicalDepartment internalDept = getDepartmentByName("内科");
        if (internalDept == null) {
            return;
        }

        MedicalDoctor doctor = new MedicalDoctor();
        doctor.setUserId(doctorUser.getId());
        doctor.setDeptId(internalDept.getId());
        doctor.setTitle("主治医师");
        doctor.setSpecialty("内科常见病诊疗");
        doctor.setIntroduction("校医院内科主治医师");
        doctorMapper.insert(doctor);
        log.info("初始化医生档案完成");
    }

    private void initSchedules() {
        if (scheduleMapper.selectCount(null) > 0) {
            return;
        }

        MedicalDoctor doctor = doctorMapper.selectOne(new LambdaQueryWrapper<MedicalDoctor>().last("LIMIT 1"));
        if (doctor == null) {
            return;
        }

        String[] timeSlots = {"上午08:30-11:30", "下午14:30-17:30"};
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            LocalDate scheduleDate = today.plusDays(i);
            for (String timeSlot : timeSlots) {
                MedicalSchedule schedule = new MedicalSchedule();
                schedule.setDoctorId(doctor.getId());
                schedule.setDeptId(doctor.getDeptId());
                schedule.setScheduleDate(scheduleDate);
                schedule.setTimeSlot(timeSlot);
                schedule.setMaxNum(20);
                schedule.setRemainNum(20);
                schedule.setScheduleType("普通");
                schedule.setStatus(1);
                scheduleMapper.insert(schedule);
            }
        }
        log.info("初始化医生排班完成");
    }

    private void initMedicines() {
        if (medicineMapper.selectCount(null) > 0) {
            return;
        }
        Object[][] medicines = {
                {"阿莫西林胶囊", "0.5g*24粒", "盒", new BigDecimal("35.00"), 200, 20, "KSS", "用于敏感菌所致的呼吸道、泌尿生殖道等感染"},
                {"感冒清热颗粒", "12g*10袋", "盒", new BigDecimal("25.75"), 150, 15, "GM", "疏风散寒、解表清热，用于风寒感冒、头痛发热"},
                {"布洛芬缓释胶囊", "0.3g*20粒", "盒", new BigDecimal("30.00"), 180, 20, "ZT", "解热镇痛，用于缓解轻至中度疼痛及发热"},
                {"甲硝唑片", "0.2g*21片", "盒", new BigDecimal("18.50"), 120, 10, "KSS", "抗厌氧菌感染，用于口腔、腹腔及妇科感染"},
                {"云南白药气雾剂", "85g", "瓶", new BigDecimal("42.00"), 80, 10, "YW", "活血散瘀、消肿止痛，用于跌打损伤、肌肉酸痛"}
        };
        for (Object[] data : medicines) {
            MedicalMedicine medicine = new MedicalMedicine();
            medicine.setMedicineName((String) data[0]);
            medicine.setSpec((String) data[1]);
            medicine.setUnit((String) data[2]);
            medicine.setPrice((BigDecimal) data[3]);
            medicine.setStock((Integer) data[4]);
            medicine.setMinStock((Integer) data[5]);
            medicine.setCategory((String) data[6]);
            medicine.setDescription((String) data[7]);
            medicine.setStatus(1);
            medicineMapper.insert(medicine);
        }
        log.info("初始化药品数据完成");
    }

    /**
     * 将历史药品归类到标准分类编码，并补充作用说明
     */
    private void repairMedicineData() {
        Object[][] medicineMeta = {
                {"阿莫西林胶囊", "KSS", "用于敏感菌所致的呼吸道、泌尿生殖道等感染"},
                {"甲硝唑片", "KSS", "抗厌氧菌感染，用于口腔、腹腔及妇科感染"},
                {"感冒清热颗粒", "GM", "疏风散寒、解表清热，用于风寒感冒、头痛发热"},
                {"布洛芬缓释胶囊", "ZT", "解热镇痛，用于缓解轻至中度疼痛及发热"},
                {"云南白药气雾剂", "YW", "活血散瘀、消肿止痛，用于跌打损伤、肌肉酸痛"}
        };
        for (Object[] meta : medicineMeta) {
            LambdaQueryWrapper<MedicalMedicine> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MedicalMedicine::getMedicineName, meta[0]);
            MedicalMedicine medicine = medicineMapper.selectOne(wrapper);
            if (medicine == null) {
                continue;
            }
            boolean changed = false;
            if (!meta[1].equals(medicine.getCategory())) {
                medicine.setCategory((String) meta[1]);
                changed = true;
            }
            if (medicine.getDescription() == null || medicine.getDescription().isBlank()) {
                medicine.setDescription((String) meta[2]);
                changed = true;
            }
            if (changed) {
                medicineMapper.updateById(medicine);
                log.info("修复药品归类: {} -> {}", meta[0], meta[1]);
            }
        }
    }

    /**
     * 移除已废弃的药品盘点菜单
     */
    private void removeDeprecatedCheckMenu() {
        removeMenusByIds(304L);
    }

    private void removeDeprecatedMenus() {
        removeMenusByIds(205L, 402L, 705L, 706L);
    }

    private void removeMenusByIds(Long... menuIds) {
        for (Long menuId : menuIds) {
            roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>()
                    .eq(SysRoleMenu::getMenuId, menuId));
            if (menuMapper.selectById(menuId) != null) {
                menuMapper.deleteById(menuId);
                log.info("已移除废弃菜单: {}", menuId);
            }
        }
    }

    private void initHealthProfiles() {
        SysUser student = getUserByUsername("student");
        if (student == null) {
            return;
        }
        LambdaQueryWrapper<MedicalHealthProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalHealthProfile::getUserId, student.getId());
        if (healthProfileMapper.selectCount(wrapper) > 0) {
            return;
        }
        MedicalHealthProfile profile = new MedicalHealthProfile();
        profile.setUserId(student.getId());
        profile.setBloodType("A型");
        profile.setAllergy("青霉素过敏");
        profile.setPastHistory("2025年曾患急性胃炎");
        healthProfileMapper.insert(profile);
        log.info("初始化学生健康档案完成");
    }

    private Long getRoleId(String roleCode) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, roleCode);
        SysRole role = roleMapper.selectOne(wrapper);
        return role != null ? role.getId() : null;
    }

    private SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    private MedicalDepartment getDepartmentByName(String deptName) {
        LambdaQueryWrapper<MedicalDepartment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MedicalDepartment::getDeptName, deptName);
        return departmentMapper.selectOne(wrapper);
    }

    private void createOrUpdateUser(String username, String realName, Long roleId, String department, String className, String phone) {
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getUsername, username);
        SysUser existingUser = userMapper.selectOne(userWrapper);

        if (existingUser != null) {
            existingUser.setRealName(realName);
            existingUser.setRoleId(roleId);
            existingUser.setDepartment(department);
            existingUser.setClassName(className);
            existingUser.setPhone(phone);
            existingUser.setStatus(1);
            existingUser.setPassword(PasswordUtil.encode("123456"));
            userMapper.updateById(existingUser);
            log.info("更新用户: {}", username);
        } else {
            SysUser user = new SysUser();
            user.setUsername(username);
            user.setPassword(PasswordUtil.encode("123456"));
            user.setRealName(realName);
            user.setRoleId(roleId);
            user.setDepartment(department);
            user.setClassName(className);
            user.setPhone(phone);
            user.setStatus(1);
            userMapper.insert(user);
            log.info("创建用户: {}", username);
        }
    }
}
