<template>
  <div class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <h3>校园医务系统</h3>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            <el-avatar :size="32" icon="UserFilled" />
            <span class="username">{{ userStore.userInfo.name }}（{{ roleName }}）</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <div class="layout-body">
      <el-aside width="220px" class="layout-aside">
        <el-menu
            :default-active="route.path"
            router
            background-color="#fff"
            text-color="#303133"
            active-text-color="#2385bb"
        >
          <el-menu-item v-for="menu in userStore.userMenu" :key="menu.path" :index="menu.path">
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.name }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const roleNameMap = {
  student: '学生',
  doctor: '医生',
  pharmacist: '药师',
  approver: '审批员',
  finance: '财务人员',
  leader: '校领导',
  admin: '系统管理员'
}

const roleName = computed(() => {
  return roleNameMap[userStore.userInfo.role] || ''
})

const handleCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}

onMounted(async () => {
  if (userStore.isLogin) {
    if (!userStore.userInfo.role) {
      await userStore.getUserInfo()
    } else {
      await userStore.loadUserMenus()
    }
  }
})
</script>

<style scoped>
.layout-header {
  height: 60px;
  background: linear-gradient(90deg, #2385bb 0%, #36b37e 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header-left h3 {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}
.header-right {
  color: #fff;
}
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.username {
  margin-left: 10px;
  font-size: 14px;
}
.layout-body {
  display: flex;
  height: calc(100vh - 60px);
  overflow: hidden;
}
.layout-aside {
  background: #fff;
  border-right: 1px solid #e4e7ed;
}
.layout-main {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
