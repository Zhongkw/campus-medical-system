<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="医生姓名">
          <el-input v-model="queryParams.doctorName" placeholder="请输入医生姓名" clearable />
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="queryParams.deptId" placeholder="请选择科室" clearable style="width: 160px">
            <el-option
                v-for="dept in departmentList"
                :key="dept.id"
                :label="dept.name"
                :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排班日期">
          <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
          />
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
          <span>医生排班管理</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="doctorName" label="医生姓名" />
        <el-table-column prop="department" label="科室" />
        <el-table-column prop="scheduleDate" label="排班日期" />
        <el-table-column prop="timeSlot" label="时段" />
        <el-table-column prop="totalNumber" label="总号源" width="100" align="center" />
        <el-table-column prop="usedNumber" label="已用号源" width="100" align="center" />
        <el-table-column prop="remainingNumber" label="剩余号源" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.remainingNumber > 0 ? 'success' : 'danger'">{{ row.remainingNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="row.status === '已结束'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑排班" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="医生姓名">
          <el-input v-model="formData.doctorName" disabled />
        </el-form-item>
        <el-form-item label="排班日期">
          <el-date-picker
              v-model="formData.scheduleDate"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="时段">
          <el-select v-model="formData.timeSlot" style="width: 100%">
            <el-option label="上午08:30-11:30" value="上午08:30-11:30" />
            <el-option label="下午14:30-17:30" value="下午14:30-17:30" />
          </el-select>
        </el-form-item>
        <el-form-item label="总号源">
          <el-input-number v-model="formData.totalNumber" :min="formData.usedNumber || 1" :max="50" />
          <div class="form-tip">已用号源：{{ formData.usedNumber }}，修改总号源将同步剩余号源</div>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
let updateInterval = null
const departmentList = ref([])

const queryParams = reactive({
  doctorName: '',
  deptId: '',
  dateRange: []
})

const formData = reactive({
  id: null,
  doctorName: '',
  department: '',
  scheduleDate: '',
  timeSlot: '',
  totalNumber: 20,
  usedNumber: 0,
  remainingNumber: 20
})

const tableData = ref([])

const getStatusType = (status) => {
  const map = {
    '进行中': 'primary',
    '未开始': 'warning',
    '已结束': 'success'
  }
  return map[status] || ''
}

const getStatus = (scheduleDate) => {
  const today = new Date().toISOString().split('T')[0]
  if (scheduleDate < today) return '已结束'
  if (scheduleDate > today) return '未开始'
  return '进行中'
}

const formatSchedule = (item) => {
  const total = item.totalNumber || 0
  const remain = item.remainNumber || 0
  const usedNumber = Math.max(0, total - remain)
  return {
    id: item.id,
    doctorId: item.doctorId,
    deptId: item.deptId,
    doctorName: item.doctorName,
    department: item.department,
    scheduleDate: item.visitDate,
    timeSlot: item.visitTime,
    totalNumber: total,
    usedNumber,
    remainingNumber: remain,
    status: getStatus(item.visitDate)
  }
}

const buildQueryParams = () => {
  const params = {}
  if (queryParams.doctorName) params.doctorName = queryParams.doctorName
  if (queryParams.deptId) params.deptId = queryParams.deptId
  if (queryParams.dateRange && queryParams.dateRange.length === 2) {
    params.startDate = queryParams.dateRange[0]
    params.endDate = queryParams.dateRange[1]
  }
  return params
}

const loadData = async () => {
  loading.value = true
  try {
    const list = await api.getScheduleDetailList(buildQueryParams())
    tableData.value = (list || []).map(formatSchedule)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => {
  queryParams.doctorName = ''
  queryParams.deptId = ''
  queryParams.dateRange = []
  loadData()
}

const handleEdit = (row) => {
  Object.assign(formData, {
    id: row.id,
    doctorName: row.doctorName,
    department: row.department,
    scheduleDate: row.scheduleDate,
    timeSlot: row.timeSlot,
    totalNumber: row.totalNumber,
    usedNumber: row.usedNumber,
    remainingNumber: row.remainingNumber
  })
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.doctorName} ${row.scheduleDate} 的排班吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  submitLoading.value = true
  try {
    await api.updateSchedule({
      id: formData.id,
      scheduleDate: formData.scheduleDate,
      timeSlot: formData.timeSlot,
      maxNum: formData.totalNumber
    })
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '保存失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  try {
    departmentList.value = await api.listDepartments() || []
  } catch (error) {
    console.error(error)
  }
  loadData()
  updateInterval = setInterval(loadData, 60000)
})

onUnmounted(() => {
  if (updateInterval) {
    clearInterval(updateInterval)
  }
})
</script>

<style scoped>
.page-container { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; }
.form-tip { font-size: 12px; color: #909399; margin-top: 6px; }
</style>
