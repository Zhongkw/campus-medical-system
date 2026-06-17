import axios from 'axios'
import { ElMessage } from 'element-plus'

const baseURL = '/api'

const request = axios.create({
    baseURL: baseURL,
    timeout: 10000
})

request.interceptors.request.use(config => {
    const token = localStorage.getItem('medical_token')
    if (token) {
        config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
})

request.interceptors.response.use(response => {
    const res = response.data
    if (res.code !== 0) {
        ElMessage.error(res.message || '请求失败')
        if (res.code === 401) {
            localStorage.removeItem('medical_token')
            localStorage.removeItem('medical_userInfo')
            window.location.href = '/login'
        }
        return Promise.reject(res)
    }
    return res.data
}, error => {
    ElMessage.error('网络请求异常')
    return Promise.reject(error)
})

export const api = {
    // 认证
    login: (data) => request.post('/auth/login', data),
    getUserInfo: (userId) => request.post('/auth/getUserInfo', { userId }),

    // 工作台
    getDashboardData: () => request.get('/dashboard/getData'),
    getTodoList: () => request.get('/dashboard/getTodoList'),
    getDoneList: () => request.get('/dashboard/getDoneList'),

    // 公共接口
    getCommonUserInfo: (userId) => request.get('/common/getUserInfo', { params: { userId } }),
    getScheduleDetailList: (params) => request.get('/common/getScheduleDetailList', { params }),
    getDoctorIdByUserId: (userId) => request.get('/common/getDoctorIdByUserId', { params: { userId } }),
    getDoctorProfile: (userId) => request.get('/common/getDoctorProfile', { params: { userId } }),
    getDepartmentList: () => request.get('/common/getDepartmentList'),

    // 学生 - 预约挂号
    submitRegister: (data) => request.post('/student/register', data),
    getMyAppointments: () => request.get('/student/appointments'),
    cancelAppointment: (appointmentId) =>
        request.post('/student/cancelAppointment', null, { params: { appointmentId } }),
    submitCancelRegister: (data) => request.post('/student/cancelRegister', data),
    submitRefund: (data) => request.post('/student/refund', data),
    getMyRefundApplications: () => request.get('/student/refundApplications'),

    // 预约业务
    confirmAppointment: (appointmentId, doctorId) =>
        request.post(`/medical/appointment-business/confirm/${appointmentId}`, null, { params: { doctorId } }),
    completeAppointment: (appointmentId, doctorId) =>
        request.post(`/medical/appointment-business/complete/${appointmentId}`, null, { params: { doctorId } }),

    // 医生 - 候诊队列
    getQueueDetail: (doctorId, params) =>
        request.get(`/medical/queue/doctor/${doctorId}/detail`, { params }),
    deleteQueueRecord: (queueId, doctorId) =>
        request.delete(`/medical/queue/${queueId}`, { params: { doctorId } }),

    // 诊疗业务
    receivePatient: (data) => request.post('/medical/business/receive', data),
    updateRecord: (data) => request.put('/medical/business/record', data),
    getRecordByAppointment: (appointmentId) =>
        request.get(`/medical/business/records/appointment/${appointmentId}`),
    createPrescription: (data) => request.post('/medical/business/prescription/create', data),
    createRegistrationOrder: (params) => request.post('/medical/business/order/registration', null, { params }),
    submitPrescription: (prescriptionId, doctorId) =>
        request.post(`/medical/business/prescription/submit/${prescriptionId}`, null, { params: { doctorId } }),
    getPatientRecords: (userId) => request.get(`/medical/business/records/patient/${userId}`),
    getPatientRecordDetails: (userId) =>
        request.get(`/medical/business/records/patient/${userId}/detail`),
    getDoctorRecords: (doctorId) => request.get(`/medical/business/records/doctor/${doctorId}`),
    getPrescriptionDetailList: (params) => request.get('/medical/business/prescription/detail-list', { params }),
    getPrescriptionDetail: (prescriptionId) =>
        request.get(`/medical/business/prescription/detail/${prescriptionId}`),
    startDispense: (prescriptionId) =>
        request.post(`/medical/business/prescription/dispense/${prescriptionId}`),
    completeDispense: (prescriptionId) =>
        request.post(`/medical/business/prescription/complete-dispense/${prescriptionId}`),

    // 学生 - 订单缴费
    getOrderList: (params) => request.get('/medical/order-business/list', { params }),
    payOrder: (data) => request.post('/medical/order-business/pay', data),

    // 学生 - 病假申请
    submitSickLeave: (data, userId) =>
        request.post('/medical/sickleave-business/submit', data, { params: { userId } }),
    getMySickLeaves: (userId, status) =>
        request.get('/medical/sickleave-business/myList', { params: { userId, status } }),
    withdrawSickLeave: (applyId, userId) =>
        request.post(`/medical/sickleave-business/withdraw/${applyId}`, null, { params: { userId } }),

    // 审批员 - 病假审批
    getSickLeaveApprovalList: (params) =>
        request.get('/medical/sickleave-business/approvalList', { params }),
    auditSickLeave: (data) => request.post('/medical/sickleave-business/audit', data),

    // 审批员 - 退号审批
    getCancelRegList: (params) => request.get('/approve/cancelReg/list', { params }),
    auditCancelReg: (params) => request.post('/approve/cancelReg/audit', null, { params }),

    // 药师 - 药品管理
    getMedicineList: (params) => request.get('/medical/medicine-business/medicine/list', { params }),
    getLowStockMedicines: () => request.get('/medical/medicine-business/medicine/low-stock'),
    searchMedicines: (medicineName) =>
        request.get('/medical/medicine-business/medicine/search', { params: { medicineName } }),
    instockMedicine: (data) => request.post('/medical/medicine-business/instock', data),
    getInstockList: (params) => request.get('/medical/medicine-business/instock/list', { params }),
    completeInstock: (instockId, operatorId) =>
        request.post(`/medical/medicine-business/instock/complete/${instockId}`, null, { params: { operatorId } }),
    updateMedicineStock: (params) => request.post('/medical/medicine-business/stock/update', null, { params }),
    updateMedicineInfo: (data) => request.put('/medical/medicine-business/medicine/update', data),

    // 财务
    getFinanceOrderList: (params) => request.get('/finance/order/pageList', { params }),
    getFinanceOrderDetail: (orderId) => request.get(`/finance/order/detail/${orderId}`),
    getFinanceDashboard: () => request.get('/finance/order/dashboard'),
    auditRefund: (params) => request.post('/finance/order/auditRefund', null, { params }),
    getFinanceSummary: () => request.get('/finance/stat/summary'),
    getFinanceRangeIncome: (params) => request.get('/finance/stat/rangeIncome', { params }),
    getFinanceReconciliation: (params) => request.get('/finance/stat/reconciliation', { params }),

    // 校领导统计
    getVisitTrend: (params) => request.get('/leader/stat/visitTrend', { params }),
    getDeptVisitRank: () => request.get('/leader/stat/deptVisitRank'),
    getDiseaseTop10: (params) => request.get('/leader/stat/diseaseTop10', { params }),
    getDrugConsumeRank: (params) => request.get('/leader/stat/drugConsumeRank', { params }),
    getCollegeSickStat: () => request.get('/medical/sickleave-business/collegeStat'),
    getSickLeaveSummary: () => request.get('/medical/sickleave-business/summary'),

    // 健康档案
    getHealthProfile: (userId) => request.get(`/medical/health-profile/user/${userId}`),

    // 管理员 - 排班
    createSchedule: (data) => request.post('/medical/schedule/create', data),
    updateSchedule: (data) => request.put('/medical/schedule/update-slots', data),
    deleteSchedule: (id) => request.delete(`/medical/schedule/${id}`),

    // 管理员 - 用户管理
    getUserPageList: (params) => request.get('/system/user/pageList', { params }),
    addUser: (data) => request.post('/system/user/add', data),
    editUser: (data) => request.post('/system/user/edit', data),
    switchUserStatus: (params) => request.post('/system/user/switchStatus', null, { params }),
    resetUserPwd: (userId) => request.post('/system/user/resetPwd', null, { params: { userId } }),
    deleteUser: (userId) => request.post('/system/user/delete', null, { params: { userId } }),

    // 管理员 - 角色
    getRoleList: () => request.get('/system/role/list'),

    // 管理员 - 系统管理（菜单/角色/基础数据）
    getMenuTree: () => request.get('/system/manage/menu/tree'),
    getRoleMenuIds: (roleId) => request.get(`/system/manage/role-menu/${roleId}`),
    assignRoleMenus: (data) => request.post('/system/manage/role-menu/assign', data),
    getUserMenus: () => request.get('/system/manage/user-menus'),
    addRole: (data) => request.post('/system/manage/role/add', data),
    editRole: (data) => request.post('/system/manage/role/edit', data),
    deleteRole: (roleId) => request.delete(`/system/manage/role/${roleId}`),

    listDepartments: () => request.get('/system/manage/basic-data/departments'),
    saveDepartment: (data) => request.post('/system/manage/basic-data/department', data),
    deleteDepartment: (id) => request.delete(`/system/manage/basic-data/department/${id}`),
    listDoctorsByDepartment: (deptId) => request.get(`/system/manage/basic-data/department/${deptId}/doctors`),

    listDrugCategories: () => request.get('/system/manage/basic-data/drug-categories'),
    saveDrugCategory: (data) => request.post('/system/manage/basic-data/drug-category', data),
    deleteDrugCategory: (id) => request.delete(`/system/manage/basic-data/drug-category/${id}`),

    listDiseases: () => request.get('/system/manage/basic-data/diseases'),
    saveDisease: (data) => request.post('/system/manage/basic-data/disease', data),
    deleteDisease: (id) => request.delete(`/system/manage/basic-data/disease/${id}`),

    listMedicinesByCategory: (categoryCode) =>
        request.get('/system/manage/basic-data/medicines', { params: { categoryCode } }),
    saveMedicine: (data) => request.post('/system/manage/basic-data/medicine', data),
    deleteMedicine: (id) => request.delete(`/system/manage/basic-data/medicine/${id}`),

    // 管理员 - 日志
    getOperLogList: () => request.get('/system/oper-log/list')
}
