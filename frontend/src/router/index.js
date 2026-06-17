import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const Login = () => import('../views/login/index.vue')
const Layout = () => import('../views/layout/index.vue')
const Home = () => import('../views/home/index.vue')

// 学生
const StudentRegister = () => import('../views/student/register.vue')
const StudentPay = () => import('../views/student/pay.vue')
const StudentRecord = () => import('../views/student/record.vue')
const StudentSickLeave = () => import('../views/student/sickLeave.vue')
const StudentHealthRecord = () => import('../views/student/healthRecord.vue')

// 医生
const DoctorQueue = () => import('../views/doctor/queue.vue')
const DoctorMedicalRecord = () => import('../views/doctor/medicalRecord.vue')
const DoctorPrescription = () => import('../views/doctor/prescription.vue')
const DoctorSchedule = () => import('../views/doctor/schedule.vue')
// 药师
const PharmacistPrescription = () => import('../views/pharmacist/prescription.vue')
const PharmacistDrugIn = () => import('../views/pharmacist/drugIn.vue')
const PharmacistStock = () => import('../views/pharmacist/stock.vue')

// 审批员
const ApproverSickLeave = () => import('../views/approver/sickLeave.vue')
const ApproverCancelRegister = () => import('../views/approver/cancelRegister.vue')

// 财务人员
const FinanceOrder = () => import('../views/finance/order.vue')
const FinanceReconciliation = () => import('../views/finance/reconciliation.vue')
const FinanceReminder = () => import('../views/finance/reminder.vue')

// 校领导
const LeaderOverview = () => import('../views/leader/overview.vue')
const LeaderVisitStats = () => import('../views/leader/visitStats.vue')
const LeaderDiseaseStats = () => import('../views/leader/diseaseStats.vue')
const LeaderDrugStats = () => import('../views/leader/drugStats.vue')
const LeaderSickLeaveStats = () => import('../views/leader/sickLeaveStats.vue')

// 系统管理员（合并超级管理员功能）
const AdminUser = () => import('../views/admin/user.vue')
const AdminRole = () => import('../views/admin/role.vue')
const AdminDictionary = () => import('../views/admin/dictionary.vue')
const AdminSchedule = () => import('../views/admin/schedule.vue')

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: Login,
        meta: { title: '登录 - 校园医务系统', noAuth: true }
    },
    {
        path: '/',
        component: Layout,
        redirect: '/home',
        children: [
            { path: '/home', name: 'Home', component: Home, meta: { title: '首页' } },

            // 学生路由
            { path: '/student/register', name: 'StudentRegister', component: StudentRegister, meta: { title: '预约挂号', role: 'student' } },
            { path: '/student/pay', name: 'StudentPay', component: StudentPay, meta: { title: '在线缴费', role: 'student' } },
            { path: '/student/record', name: 'StudentRecord', component: StudentRecord, meta: { title: '诊疗记录', role: 'student' } },
            { path: '/student/sickLeave', name: 'StudentSickLeave', component: StudentSickLeave, meta: { title: '病假申请', role: 'student' } },
            { path: '/student/healthRecord', name: 'StudentHealthRecord', component: StudentHealthRecord, meta: { title: '健康档案', role: 'student' } },

            // 医生路由
            { path: '/doctor/queue', name: 'DoctorQueue', component: DoctorQueue, meta: { title: '候诊队列', role: 'doctor' } },
            { path: '/doctor/medicalRecord', name: 'DoctorMedicalRecord', component: DoctorMedicalRecord, meta: { title: '病历书写', role: 'doctor' } },
            { path: '/doctor/prescription', name: 'DoctorPrescription', component: DoctorPrescription, meta: { title: '处方开具', role: 'doctor' } },
            { path: '/doctor/schedule', name: 'DoctorSchedule', component: DoctorSchedule, meta: { title: '个人排班', role: 'doctor' } },

            // 药师路由
            { path: '/pharmacist/prescription', name: 'PharmacistPrescription', component: PharmacistPrescription, meta: { title: '处方配药', role: 'pharmacist' } },
            { path: '/pharmacist/drugIn', name: 'PharmacistDrugIn', component: PharmacistDrugIn, meta: { title: '药品入库', role: 'pharmacist' } },
            { path: '/pharmacist/stock', name: 'PharmacistStock', component: PharmacistStock, meta: { title: '库存管理', role: 'pharmacist' } },

            // 审批员路由
            { path: '/approver/sickLeave', name: 'ApproverSickLeave', component: ApproverSickLeave, meta: { title: '病假审批', role: 'approver' } },
            { path: '/approver/cancelRegister', name: 'ApproverCancelRegister', component: ApproverCancelRegister, meta: { title: '退号审批', role: 'approver' } },

            // 财务人员路由
            { path: '/finance/order', name: 'FinanceOrder', component: FinanceOrder, meta: { title: '收费订单管理', role: 'finance' } },
            { path: '/finance/reconciliation', name: 'FinanceReconciliation', component: FinanceReconciliation, meta: { title: '财务对账', role: 'finance' } },
            { path: '/finance/reminder', name: 'FinanceReminder', component: FinanceReminder, meta: { title: '未缴费催缴', role: 'finance' } },

            // 校领导路由
            { path: '/leader/overview', name: 'LeaderOverview', component: LeaderOverview, meta: { title: '首页', role: 'leader' } },
            { path: '/leader/visitStats', name: 'LeaderVisitStats', component: LeaderVisitStats, meta: { title: '就诊量统计', role: 'leader' } },
            { path: '/leader/diseaseStats', name: 'LeaderDiseaseStats', component: LeaderDiseaseStats, meta: { title: '疾病谱分析', role: 'leader' } },
            { path: '/leader/drugStats', name: 'LeaderDrugStats', component: LeaderDrugStats, meta: { title: '药品消耗统计', role: 'leader' } },
            { path: '/leader/sickLeaveStats', name: 'LeaderSickLeaveStats', component: LeaderSickLeaveStats, meta: { title: '病假数据统计', role: 'leader' } },

            // 系统管理员路由（合并超级管理员功能）
            { path: '/admin/user', name: 'AdminUser', component: AdminUser, meta: { title: '用户管理', role: 'admin' } },
            { path: '/admin/role', name: 'AdminRole', component: AdminRole, meta: { title: '角色权限管理', role: 'admin' } },
            { path: '/admin/schedule', name: 'AdminSchedule', component: AdminSchedule, meta: { title: '医生排班管理', role: 'admin' } },
            { path: '/admin/dictionary', name: 'AdminDictionary', component: AdminDictionary, meta: { title: '基础数据管理', role: 'admin' } }
        ]
    },
    { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach(async (to, from, next) => {
    const token = localStorage.getItem('medical_token')

    if (to.meta.noAuth) {
        next()
        return
    }

    if (!token) {
        next('/login')
        return
    }

    if (to.meta.role) {
        const userInfoStr = localStorage.getItem('medical_userInfo')
        if (userInfoStr) {
            const userInfo = JSON.parse(userInfoStr)
            if (userInfo.role && userInfo.role !== to.meta.role) {
                ElMessage.error('无权限访问该页面')
                next('/home')
                return
            }
        }
    }

    document.title = `${to.meta.title} - 校园医务系统`
    next()
})

export default router
