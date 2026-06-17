<template>
  <div class="page-container">
    <el-card class="config-card">
      <template #header>
        <div class="card-header">
          <span>系统全局配置</span>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础配置" name="basic">
          <el-form :model="basicConfig" label-width="150px" style="max-width: 600px;">
            <el-form-item label="系统名称">
              <el-input v-model="basicConfig.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            <el-form-item label="系统版本">
              <el-input v-model="basicConfig.version" disabled />
            </el-form-item>
            <el-form-item label="挂号开放时间">
              <el-time-picker
                  v-model="basicConfig.registerStartTime"
                  placeholder="开始时间"
                  format="HH:mm"
              />
              <span style="margin: 0 10px;">至</span>
              <el-time-picker
                  v-model="basicConfig.registerEndTime"
                  placeholder="结束时间"
                  format="HH:mm"
              />
            </el-form-item>
            <el-form-item label="最大挂号天数">
              <el-input-number v-model="basicConfig.maxRegisterDays" :min="1" :max="30" />
            </el-form-item>
            <el-form-item label="病假最长天数">
              <el-input-number v-model="basicConfig.maxSickLeaveDays" :min="1" :max="90" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveBasic" :loading="saveLoading">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="通知配置" name="notification">
          <el-form :model="notificationConfig" label-width="150px" style="max-width: 600px;">
            <el-form-item label="短信通知">
              <el-switch v-model="notificationConfig.smsEnabled" />
            </el-form-item>
            <el-form-item label="邮件通知">
              <el-switch v-model="notificationConfig.emailEnabled" />
            </el-form-item>
            <el-form-item label="系统消息">
              <el-switch v-model="notificationConfig.systemMsgEnabled" />
            </el-form-item>
            <el-form-item label="缴费提醒">
              <el-switch v-model="notificationConfig.paymentReminder" />
            </el-form-item>
            <el-form-item label="审批提醒">
              <el-switch v-model="notificationConfig.approvalReminder" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveNotification" :loading="saveLoading">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="安全配置" name="security">
          <el-form :model="securityConfig" label-width="150px" style="max-width: 600px;">
            <el-form-item label="密码最小长度">
              <el-input-number v-model="securityConfig.minPasswordLength" :min="6" :max="20" />
            </el-form-item>
            <el-form-item label="密码复杂度">
              <el-checkbox-group v-model="securityConfig.passwordComplexity">
                <el-checkbox label="uppercase">大写字母</el-checkbox>
                <el-checkbox label="lowercase">小写字母</el-checkbox>
                <el-checkbox label="number">数字</el-checkbox>
                <el-checkbox label="special">特殊字符</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-switch v-model="securityConfig.loginLockEnabled" />
            </el-form-item>
            <el-form-item label="最大失败次数" v-if="securityConfig.loginLockEnabled">
              <el-input-number v-model="securityConfig.maxLoginAttempts" :min="3" :max="10" />
            </el-form-item>
            <el-form-item label="锁定时长(分钟)" v-if="securityConfig.loginLockEnabled">
              <el-input-number v-model="securityConfig.lockDuration" :min="5" :max="60" />
            </el-form-item>
            <el-form-item label="会话超时(分钟)">
              <el-input-number v-model="securityConfig.sessionTimeout" :min="10" :max="480" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveSecurity" :loading="saveLoading">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')
const saveLoading = ref(false)

const basicConfig = reactive({
  systemName: '校园医务系统',
  version: 'V1.0.0',
  registerStartTime: '08:00',
  registerEndTime: '22:00',
  maxRegisterDays: 7,
  maxSickLeaveDays: 14
})

const notificationConfig = reactive({
  smsEnabled: true,
  emailEnabled: true,
  systemMsgEnabled: true,
  paymentReminder: true,
  approvalReminder: true
})

const securityConfig = reactive({
  minPasswordLength: 6,
  passwordComplexity: ['number'],
  loginLockEnabled: true,
  maxLoginAttempts: 5,
  lockDuration: 30,
  sessionTimeout: 120
})

const handleSaveBasic = async () => {
  saveLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('基础配置保存成功')
  } finally {
    saveLoading.value = false
  }
}

const handleSaveNotification = async () => {
  saveLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('通知配置保存成功')
  } finally {
    saveLoading.value = false
  }
}

const handleSaveSecurity = async () => {
  saveLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('安全配置保存成功')
  } finally {
    saveLoading.value = false
  }
}
</script>

<style scoped>
.page-container { padding: 0; }
.config-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; }
</style>
