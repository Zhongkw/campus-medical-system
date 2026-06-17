<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="就诊日期">
          <el-date-picker
              v-model="queryParams.visitDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="队列状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="候诊中" value="候诊中" />
            <el-option label="就诊中" value="就诊中" />
            <el-option label="已完成" value="已完成" />
          </el-select>
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
          <span>候诊队列列表</span>
          <el-tag type="success" style="margin-left: 20px;">当前队列人数：{{ queueCount }}</el-tag>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="queueNo" label="排队号" width="100" align="center" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="department" label="就诊科室" />
        <el-table-column prop="symptom" label="主要症状" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="waitTime" label="等待时长" width="120" />
        <el-table-column label="操作" width="260" align="center">
          <template #default="{ row }">
            <el-button
                v-if="row.status === '候诊中'"
                type="primary"
                size="small"
                @click="handleCall(row)"
            >
              叫号接诊
            </el-button>
            <el-button
                v-if="row.status === '就诊中'"
                type="success"
                size="small"
                @click="handleWriteRecord(row)"
            >
              书写病历
            </el-button>
            <el-button type="info" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除记录</el-button>
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

    <el-dialog v-model="viewDialogVisible" title="患者信息" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="排队号">{{ currentRow.queueNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentRow.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="就诊科室">{{ currentRow.department }}</el-descriptions-item>
        <el-descriptions-item label="主要症状" :span="2">{{ currentRow.symptom }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="等待时长">{{ currentRow.waitTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const viewDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const doctorId = ref(null)

const queryParams = reactive({
  visitDate: new Date().toISOString().split('T')[0],
  status: ''
})

const tableData = ref([])

const queueCount = computed(() => {
  return tableData.value.filter(item => item.status === '候诊中').length
})

const getStatusType = (status) => {
  const map = {
    '候诊中': 'warning',
    '就诊中': 'primary',
    '已完成': 'success'
  }
  return map[status] || ''
}

const initDoctorId = async () => {
  try {
    doctorId.value = await api.getDoctorIdByUserId(userStore.userInfo.id)
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  if (!doctorId.value) return
  loading.value = true
  try {
    const params = { queueDate: queryParams.visitDate }
    if (queryParams.status) {
      params.status = queryParams.status
    }
    const list = await api.getQueueDetail(doctorId.value, params)
    tableData.value = list || []
    total.value = tableData.value.length
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
  queryParams.visitDate = new Date().toISOString().split('T')[0]
  queryParams.status = ''
  handleSearch()
}

const handleCall = async (row) => {
  try {
    await ElMessageBox.confirm(`确定叫号 ${row.queueNo} ${row.studentName} 吗？`, '叫号确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    await api.confirmAppointment(row.appointmentId, doctorId.value)
    ElMessage.success(`已叫号：${row.queueNo}`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleWriteRecord = (row) => {
  router.push({
    path: '/doctor/medicalRecord',
    query: {
      patientId: row.userId,
      appointmentId: row.appointmentId,
      deptId: row.deptId
    }
  })
}

const handleView = (row) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该候诊记录吗？删除后不可恢复。', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.deleteQueueRecord(row.id, doctorId.value)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

onMounted(async () => {
  await initDoctorId()
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 0;
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
  display: flex;
  align-items: center;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
