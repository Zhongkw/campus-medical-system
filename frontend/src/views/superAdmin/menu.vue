//角色菜单维护
<template>
  <div class="page-container">
    <el-card class="menu-card">
      <template #header>
        <div class="card-header">
          <span>角色菜单统一维护</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card>
            <template #header><span>角色列表</span></template>
            <el-table :data="roleList" highlight-current-row @current-change="handleRoleChange" height="500">
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column prop="roleName" label="角色名称" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :span="16">
          <el-card>
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>菜单权限配置 - {{ currentRoleName }}</span>
                <el-button type="primary" @click="handleSaveMenu" :loading="saveLoading">保存配置</el-button>
              </div>
            </template>
            <el-tree
                ref="treeRef"
                :data="menuTreeData"
                show-checkbox
                node-key="id"
                :default-expanded-keys="[1, 2, 3, 4, 5, 6, 7, 8]"
                :props="{ label: 'name', children: 'children' }"
                height="500"
            />
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const saveLoading = ref(false)
const treeRef = ref(null)
const currentRoleName = ref('学生')

const roleList = ref([
  { id: 1, roleName: '学生', roleCode: 'student' },
  { id: 2, roleName: '医生', roleCode: 'doctor' },
  { id: 3, roleName: '药师', roleCode: 'pharmacist' },
  { id: 4, roleName: '审批员', roleCode: 'approver' },
  { id: 5, roleName: '财务人员', roleCode: 'finance' },
  { id: 6, roleName: '校领导', roleCode: 'leader' },
  { id: 7, roleName: '系统管理员', roleCode: 'admin' },
  { id: 8, roleName: '超级管理员', roleCode: 'superAdmin' }
])

const menuTreeData = ref([
  {
    id: 1,
    name: '系统首页',
    children: []
  },
  {
    id: 2,
    name: '学生服务',
    children: [
      { id: 21, name: '预约挂号' },
      { id: 22, name: '在线缴费' },
      { id: 23, name: '诊疗记录' },
      { id: 24, name: '病假申请' },
      { id: 25, name: '健康档案' }
    ]
  },
  {
    id: 3,
    name: '医生诊疗',
    children: [
      { id: 31, name: '候诊队列' },
      { id: 32, name: '病历书写' },
      { id: 33, name: '处方开具' },
      { id: 34, name: '病假条开具' }
    ]
  },
  {
    id: 4,
    name: '药房管理',
    children: [
      { id: 41, name: '处方配药' },
      { id: 42, name: '药品入库' },
      { id: 43, name: '库存管理' },
      { id: 44, name: '药品盘点' }
    ]
  },
  {
    id: 5,
    name: '财务管理',
    children: [
      { id: 51, name: '收费订单管理' },
      { id: 52, name: '财务对账' },
      { id: 53, name: '未缴费催缴' }
    ]
  },
  {
    id: 6,
    name: '流程审批',
    children: [
      { id: 61, name: '病假审批' },
      { id: 62, name: '特殊挂号审批' },
      { id: 63, name: '退号审批' }
    ]
  },
  {
    id: 7,
    name: '统计分析',
    children: [
      { id: 71, name: '就诊量统计' },
      { id: 72, name: '疾病谱分析' },
      { id: 73, name: '药品消耗统计' },
      { id: 74, name: '病假数据统计' }
    ]
  },
  {
    id: 8,
    name: '系统管理',
    children: [
      { id: 81, name: '用户管理' },
      { id: 82, name: '角色权限管理' },
      { id: 83, name: '系统日志' },
      { id: 84, name: '数据字典' },
      { id: 85, name: '医生排班管理' }
    ]
  }
])

const handleRoleChange = (role) => {
  if (role) {
    currentRoleName.value = role.roleName
    // 加载该角色的菜单权限
    console.log('加载角色菜单权限:', role.roleCode)
  }
}

const handleSaveMenu = async () => {
  saveLoading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    const checkedKeys = treeRef.value.getCheckedKeys()
    console.log('保存的菜单权限:', checkedKeys)
    ElMessage.success('菜单权限保存成功')
  } finally {
    saveLoading.value = false
  }
}

onMounted(() => {
  // 初始化加载第一个角色的菜单
})
</script>

<style scoped>
.page-container { padding: 0; }
.menu-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; }
</style>
