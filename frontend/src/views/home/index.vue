<template>
  <div class="home-container">
    <div class="welcome-card">
      <h2>{{ welcomeText }}</h2>
      <p>当前角色：{{ roleName }}</p>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="stat in statsData" :key="stat.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="30"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="todo-card" v-if="todoList.length > 0">
      <template #header>
        <div class="card-header">
          <span>待办事项</span>
        </div>
      </template>
      <el-table :data="todoList" stripe>
        <el-table-column prop="title" label="事项" />
        <el-table-column prop="content" label="详情" />
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="goTodo(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const router = useRouter()
const userStore = useUserStore()
const todoList = ref([])
const dashboardData = ref(null)

const roleNameMap = {
  student: '学生',
  doctor: '医生',
  pharmacist: '药师',
  approver: '审批员',
  finance: '财务人员',
  leader: '校领导',
  admin: '系统管理员'
}

const statColors = ['#2385bb', '#36b37e', '#67c23a', '#e6a23c']
const statIcons = ['Clock', 'Money', 'CircleCheck', 'Tickets', 'User', 'Reading', 'Bell', 'Warning']

const roleName = computed(() => roleNameMap[userStore.userInfo.role] || '')
const welcomeText = computed(() => dashboardData.value?.welcome || `欢迎回来，${userStore.userInfo.name}！`)

const statsData = computed(() => {
  if (!dashboardData.value?.statistics) return []
  return dashboardData.value.statistics.map((item, index) => ({
    title: item.name,
    value: item.value,
    icon: statIcons[index % statIcons.length],
    color: statColors[index % statColors.length]
  }))
})

const loadDashboard = async () => {
  try {
    const data = await api.getDashboardData()
    dashboardData.value = data
    todoList.value = data.todoList || []
  } catch (error) {
    console.error(error)
  }
}

const goTodo = (row) => {
  if (row.url) {
    router.push(row.url)
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
}
.welcome-card {
  background: linear-gradient(135deg, #2385bb 0%, #36b37e 100%);
  color: #fff;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.welcome-card h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}
.welcome-card p {
  margin: 0;
  opacity: 0.9;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  margin-bottom: 20px;
}
.stat-content {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.todo-card {
  margin-top: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>
