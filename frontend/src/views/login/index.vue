<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>校园医务系统</h2>
        <p>Campus Medical System</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择角色" size="large" style="width: 100%" @change="handleRoleChange">
            <el-option label="学生" value="student" />
            <el-option label="医生" value="doctor" />
            <el-option label="药师" value="pharmacist" />
            <el-option label="审批员" value="approver" />
            <el-option label="财务人员" value="finance" />
            <el-option label="校领导" value="leader" />
            <el-option label="系统管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item prop="loginName">
          <el-input v-model="loginForm.username" placeholder="请输入账号" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码（测试密码：123456）" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
          登 录
        </el-button>
        <div class="login-tip">
          <p>测试说明：选择对应角色，账号为角色英文名，密码统一为123456</p>
          <p>例如：学生账号student，医生账号doctor，系统管理员账号admin</p>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  role: 'student',
  username: 'student',
  password: '123456'
})

const loginRules = {
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleRoleChange = (val) => {
  loginForm.username = val
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        const result = await router.push('/home').catch(err => {
          console.error('路由跳转失败:', err)
          ElMessage.error('页面跳转失败，请刷新重试')
        })
        console.log('路由跳转结果:', result)
        console.log('当前路由:', router.currentRoute.value.path)
      } catch (error) {
        console.error('登录失败:', error)
        ElMessage.error('登录失败: ' + (error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #2385bb 0%, #36b37e 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-box {
  width: 420px;
  background: #fff;
  border-radius: 12px;
  padding: 40px 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
}
.login-title h2 {
  color: #2385bb;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}
.login-title p {
  color: #909399;
  font-size: 14px;
}
.login-form {
  width: 100%;
}
.login-btn {
  width: 100%;
  margin-top: 10px;
  background-color: #2385bb;
}
.login-tip {
  margin-top: 20px;
  text-align: center;
  color: #909399;
  font-size: 12px;
  line-height: 1.8;
}
</style>
