import Mock from 'mockjs'

const successResponse = (data = {}) => {
    return {
        status: 0,
        message: '',
        success: true,
        data: data
    }
}

const errorResponse = (message = '请求失败') => {
    return {
        status: -1,
        message: message,
        success: false,
        data: ''
    }
}

// 登录接口
Mock.mock('/mock/api/authentication/login', 'post', (options) => {
    const { loginName, password } = JSON.parse(options.body)

    const userMap = {
        'student': { id: 1, loginName: 'student', name: '张三', role: 'student', department: '计算机学院', classname: '软工2601', token: 'mock_token_student' },
        'doctor': { id: 2, loginName: 'doctor', name: '李医生', role: 'doctor', department: '内科', token: 'mock_token_doctor' },
        'pharmacist': { id: 3, loginName: 'pharmacist', name: '王药师', role: 'pharmacist', department: '药房', token: 'mock_token_pharmacist' },
        'approver': { id: 4, loginName: 'approver', name: '审批员', role: 'approver', department: '辅导员', token: 'mock_token_approver' },
        'finance': { id: 5, loginName: 'finance', name: '财务员', role: 'finance', department: '财务处', token: 'mock_token_finance' },
        'leader': { id: 6, loginName: 'leader', name: '校领导', role: 'leader', department: '校办', token: 'mock_token_leader' },
        'admin': { id: 7, loginName: 'admin', name: '系统管理员', role: 'admin', department: '信息中心', token: 'mock_token_admin' }
    }

    if (userMap[loginName] && password === '123456') {
        return successResponse(userMap[loginName])
    }
    return errorResponse('账号或密码错误，测试密码统一为123456')
})

// 获取用户信息接口
Mock.mock('/mock/api/authentication/getUserInfo', 'post', (options) => {
    const headers = options.headers || {}
    const token = headers.accesstoken || headers['accessToken']

    const tokenMap = {
        'mock_token_student': { id: 1, loginName: 'student', name: '张三', role: 'student', department: '计算机学院', classname: '软工2601' },
        'mock_token_doctor': { id: 2, loginName: 'doctor', name: '李医生', role: 'doctor', department: '内科' },
        'mock_token_pharmacist': { id: 3, loginName: 'pharmacist', name: '王药师', role: 'pharmacist', department: '药房' },
        'mock_token_approver': { id: 4, loginName: 'approver', name: '审批员', role: 'approver', department: '辅导员' },
        'mock_token_finance': { id: 5, loginName: 'finance', name: '财务员', role: 'finance', department: '财务处' },
        'mock_token_leader': { id: 6, loginName: 'leader', name: '校领导', role: 'leader', department: '校办' },
        'mock_token_admin': { id: 7, loginName: 'admin', name: '系统管理员', role: 'admin', department: '信息中心' }
    }

    if (tokenMap[token]) {
        return successResponse(tokenMap[token])
    }
    return errorResponse('令牌失效，请重新登录')
})

// 首页待办事项接口
Mock.mock('/mock/api/home/todoList', 'post', (options) => {
    const headers = options.headers || {}
    const token = headers.accesstoken || headers['accessToken']

    if (!token) {
        return errorResponse('未登录，请先登录')
    }

    const role = token.split('_')[2]
    let todoList = []
    switch(role) {
        case 'student':
            todoList = [
                { id: 1, title: '待缴费处方', count: 1 },
                { id: 2, title: '待取药处方', count: 1 }
            ]
            break
        case 'doctor':
            todoList = [
                { id: 1, title: '待接诊患者', count: 3 }
            ]
            break
        case 'pharmacist':
            todoList = [
                { id: 1, title: '待配药处方', count: 2 }
            ]
            break
        case 'approver':
            todoList = [
                { id: 1, title: '待审批病假', count: 5 }
            ]
            break
        case 'finance':
            todoList = [
                { id: 1, title: '待缴费订单', count: 12 }
            ]
            break
        case 'leader':
            todoList = [
                { id: 1, title: '本月就诊报告', count: 1 }
            ]
            break
        case 'admin':
            todoList = [
                { id: 1, title: '待处理用户', count: 3 },
                { id: 2, title: '系统待办事项', count: 2 }
            ]
            break
        default:
            todoList = []
    }
    return successResponse(todoList)
})

// 学生预约挂号接口
Mock.mock(/\/mock\/api\/student\/register/, 'post', () => {
    return successResponse({ registerNo: 'REG20260508001', message: '预约成功' })
})

// 学生在线缴费接口
Mock.mock(/\/mock\/api\/pay\/unifiedPay/, 'post', () => {
    return successResponse({ orderNo: 'PAY20260508001', payStatus: 'SUCCESS', payTime: '2026-05-08 12:30:00' })
})

// 学生病假申请接口
Mock.mock(/\/mock\/api\/edu\/syncSickLeave/, 'post', () => {
    return successResponse({ sickLeaveNo: 'SL20260508001', message: '提交成功' })
})
