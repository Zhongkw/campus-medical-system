<template>
  <div class="page-container">
    <div class="header-actions">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增用户
      </el-button>
    </div>

    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="用户姓名">
          <el-input v-model="queryParams.userName" placeholder="请输入用户姓名" clearable />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable>
            <el-option
                v-for="role in roleList"
                :key="role.roleCode"
                :label="role.roleName"
                :value="role.roleCode"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="queryParams.department" placeholder="请输入部门" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="loginName" label="登录账号" />
        <el-table-column prop="name" label="用户姓名" />
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="所属部门" />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button
                :type="row.status === '启用' ? 'danger' : 'success'"
                size="small"
                @click="handleToggleStatus(row)"
            >
              {{ row.status === '启用' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" size="small" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="登录账号" prop="loginName">
              <el-input v-model="formData.loginName" placeholder="请输入登录账号" :disabled="!!formData.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入用户姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户角色" prop="role">
              <el-select v-model="formData.role" placeholder="请选择角色" style="width: 100%" @change="handleRoleChange">
                <el-option
                    v-for="role in roleList"
                    :key="role.roleCode"
                    :label="role.roleName"
                    :value="role.roleCode"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="department">
              <el-select
                  v-if="formData.role === 'doctor'"
                  v-model="formData.deptId"
                  placeholder="请选择科室"
                  style="width: 100%"
                  @change="handleDeptChange"
              >
                <el-option
                    v-for="dept in departmentList"
                    :key="dept.id"
                    :label="dept.name"
                    :value="dept.id"
                />
              </el-select>
              <el-input
                  v-else
                  v-model="formData.department"
                  :placeholder="deptPlaceholder"
                  :disabled="deptDisabled"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="密码" prop="password" v-if="!formData.id">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { api } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const formRef = ref(null)

const queryParams = reactive({
  userName: '',
  role: '',
  department: ''
})

const ROLE_AUTO_DEPT = {
  pharmacist: '药房',
  finance: '财务科',
  approver: '学生处',
  leader: '校办'
}

const formData = reactive({
  id: '',
  loginName: '',
  name: '',
  role: '',
  department: '',
  deptId: null,
  phone: '',
  email: '',
  password: ''
})

const departmentList = ref([])

const deptDisabled = computed(() => !!ROLE_AUTO_DEPT[formData.role])
const deptPlaceholder = computed(() => {
  if (formData.role === 'student' || formData.role === 'admin') {
    return '请输入部门'
  }
  if (ROLE_AUTO_DEPT[formData.role]) {
    return ROLE_AUTO_DEPT[formData.role]
  }
  return '请输入部门'
})

const rules = {
  loginName: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入用户姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  department: [{ required: true, message: '请输入所属部门', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const tableData = ref([])
const roleList = ref([])
const roleCodeToId = ref({})
const roleIdToCode = ref({})

const formatUser = (item) => ({
  id: item.id,
  loginName: item.username,
  name: item.realName,
  role: roleIdToCode.value[item.roleId] || item.roleName,
  roleId: item.roleId,
  department: item.department || '',
  phone: item.phone || '',
  email: item.email || '',
  status: item.status === 1 || item.statusName === '启用' ? '启用' : '禁用'
})

const getRoleName = (role) => {
  const map = {
    student: '学生',
    doctor: '医生',
    pharmacist: '药师',
    approver: '审批员',
    finance: '财务人员',
    leader: '校领导',
    admin: '系统管理员',
    superAdmin: '超级管理员'
  }
  return map[role] || ''
}

const getRoleType = (role) => {
  const map = {
    student: '',
    doctor: 'success',
    pharmacist: 'warning',
    admin: 'danger'
  }
  return map[role] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      realName: queryParams.userName || undefined,
      roleId: queryParams.role ? roleCodeToId.value[queryParams.role] : undefined
    }
    const res = await api.getUserPageList(params)
    let list = (res.records || []).map(formatUser)
    if (queryParams.department) {
      list = list.filter(item => item.department && item.department.includes(queryParams.department))
    }
    tableData.value = list
    total.value = res.total || list.length
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadData()
}

const handleReset = () => {
  queryParams.userName = ''
  queryParams.role = ''
  queryParams.department = ''
  handleSearch()
}

const applyRoleDepartment = (role, keepDeptId = false) => {
  if (ROLE_AUTO_DEPT[role]) {
    formData.department = ROLE_AUTO_DEPT[role]
    formData.deptId = null
    return
  }
  if (role === 'doctor') {
    if (!keepDeptId) {
      formData.deptId = null
      formData.department = ''
    }
    return
  }
  if (!keepDeptId) {
    formData.deptId = null
    if (role !== 'student' && role !== 'admin') {
      formData.department = ''
    }
  }
}

const handleRoleChange = (role) => {
  applyRoleDepartment(role)
}

const handleDeptChange = (deptId) => {
  const dept = departmentList.value.find(item => item.id === deptId)
  formData.department = dept ? dept.name : ''
}

const loadDepartments = async () => {
  try {
    departmentList.value = await api.listDepartments() || []
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(formData, {
    id: '',
    loginName: '',
    name: '',
    role: '',
    department: '',
    deptId: null,
    phone: '',
    email: '',
    password: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, { ...row, deptId: null })
  if (row.role === 'doctor' && row.department) {
    const dept = departmentList.value.find(item => item.name === row.department)
    formData.deptId = dept ? dept.id : null
  }
  applyRoleDepartment(row.role, true)
  dialogVisible.value = true
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 ${row.name} 的密码吗？重置后密码为 123456`, '重置密码确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.resetUserPwd(row.id)
    ElMessage.success(`用户 ${row.name} 的密码已重置`)
  } catch (error) {
    // 用户取消
  }
}

const handleToggleStatus = async (row) => {
  const action = row.status === '启用' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${row.name} 吗？`, `${action}确认`, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const newStatus = row.status === '启用' ? 0 : 1
    await api.switchUserStatus({ userId: row.id, status: newStatus })
    row.status = newStatus === 1 ? '启用' : '禁用'
    ElMessage.success(`${action}成功`)
  } catch (error) {
    // 用户取消
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除用户 ${row.name} 吗？删除后不可恢复。`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.deleteUser(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formData.role === 'doctor' && !formData.deptId) {
          ElMessage.error('医生用户必须选择所属科室')
          submitLoading.value = false
          return
        }
        const payload = {
          id: formData.id || undefined,
          username: formData.loginName,
          realName: formData.name,
          roleId: roleCodeToId.value[formData.role],
          department: formData.department,
          deptId: formData.role === 'doctor' ? formData.deptId : undefined,
          phone: formData.phone,
          email: formData.email,
          password: formData.password
        }
        if (formData.id) {
          await api.editUser(payload)
        } else {
          await api.addUser(payload)
        }
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

onMounted(async () => {
  try {
    roleList.value = await api.getRoleList() || []
    roleList.value.forEach(role => {
      roleCodeToId.value[role.roleCode] = role.id
      roleIdToCode.value[role.id] = role.roleCode
    })
    await loadDepartments()
  } catch (error) {
    console.error(error)
  }
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
.header-actions {
  margin-bottom: 20px;
}
.search-card {
  margin-bottom: 20px;
}
.table-card {
  margin-bottom: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
