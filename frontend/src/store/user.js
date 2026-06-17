import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '../api'

export const useUserStore = defineStore('user', () => {
    const token = ref(localStorage.getItem('medical_token') || '')
    const storedUserInfo = localStorage.getItem('medical_userInfo')
    const userInfo = ref(storedUserInfo ? JSON.parse(storedUserInfo) : {})
    const dynamicMenus = ref([])
    const isLogin = computed(() => !!token.value)

    const roleMenuMap = {
        student: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/student/register', name: '预约挂号', icon: 'Calendar' },
            { path: '/student/pay', name: '在线缴费', icon: 'Money' },
            { path: '/student/record', name: '诊疗记录', icon: 'Document' },
            { path: '/student/sickLeave', name: '病假申请', icon: 'Tickets' },
            { path: '/student/healthRecord', name: '健康档案', icon: 'Notebook' }
        ],
        doctor: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/doctor/queue', name: '候诊队列', icon: 'User' },
            { path: '/doctor/medicalRecord', name: '病历书写', icon: 'Notebook' },
            { path: '/doctor/prescription', name: '处方开具', icon: 'Reading' },
            { path: '/doctor/schedule', name: '个人排班', icon: 'Clock' }
        ],
        pharmacist: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/pharmacist/prescription', name: '处方配药', icon: 'List' },
            { path: '/pharmacist/drugIn', name: '药品入库', icon: 'Box' },
            { path: '/pharmacist/stock', name: '库存管理', icon: 'Warehouse' }
        ],
        approver: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/approver/sickLeave', name: '病假审批', icon: 'Tickets' },
            { path: '/approver/cancelRegister', name: '退号审批', icon: 'CloseBold' }
        ],
        finance: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/finance/order', name: '收费订单管理', icon: 'Money' },
            { path: '/finance/reconciliation', name: '财务对账', icon: 'DataAnalysis' },
            { path: '/finance/reminder', name: '未缴费催缴', icon: 'Bell' }
        ],
        leader: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/leader/visitStats', name: '就诊量统计', icon: 'TrendCharts' },
            { path: '/leader/diseaseStats', name: '疾病谱分析', icon: 'DataAnalysis' },
            { path: '/leader/drugStats', name: '药品消耗统计', icon: 'Box' },
            { path: '/leader/sickLeaveStats', name: '病假数据统计', icon: 'Tickets' }
        ],
        admin: [
            { path: '/home', name: '首页', icon: 'HomeFilled' },
            { path: '/admin/user', name: '用户管理', icon: 'UserFilled' },
            { path: '/admin/role', name: '角色权限管理', icon: 'Lock' },
            { path: '/admin/schedule', name: '医生排班管理', icon: 'Calendar' },
            { path: '/admin/dictionary', name: '基础数据管理', icon: 'Collection' }
        ]
    }

    const loadUserMenus = async () => {
        if (!token.value) {
            dynamicMenus.value = []
            return []
        }
        try {
            const menus = await api.getUserMenus() || []
            if (menus.length > 0) {
                dynamicMenus.value = menus.map(item => ({
                    path: item.path,
                    name: item.name,
                    icon: item.icon || 'Menu'
                }))
                return dynamicMenus.value
            }
        } catch (error) {
            console.error('加载用户菜单失败', error)
        }
        dynamicMenus.value = []
        return []
    }

    const login = async (loginData) => {
        const res = await api.login(loginData)
        token.value = res.accessToken
        const userData = {
            id: res.userId,
            loginName: res.username,
            name: res.realName,
            role: res.roleCode
        }
        userInfo.value = userData
        localStorage.setItem('medical_token', res.accessToken)
        localStorage.setItem('medical_userInfo', JSON.stringify(userData))
        await loadUserMenus()
        return res
    }

    const getUserInfo = async () => {
        if (!userInfo.value.id) {
            return userInfo.value
        }
        const res = await api.getUserInfo(userInfo.value.id)
        userInfo.value = {
            id: res.userId || res.id,
            loginName: res.username || res.loginName,
            name: res.realName || res.name,
            role: res.roleCode || res.role,
            department: res.department,
            classname: res.className || res.classname
        }
        await loadUserMenus()
        return res
    }

    const logout = () => {
        token.value = ''
        userInfo.value = {}
        dynamicMenus.value = []
        localStorage.removeItem('medical_token')
        localStorage.removeItem('medical_userInfo')
    }

    const userMenu = computed(() => {
        if (dynamicMenus.value.length > 0) {
            return dynamicMenus.value
        }
        return roleMenuMap[userInfo.value.role] || []
    })

    return {
        userInfo,
        token,
        isLogin,
        userMenu,
        dynamicMenus,
        login,
        getUserInfo,
        loadUserMenus,
        logout
    }
})
