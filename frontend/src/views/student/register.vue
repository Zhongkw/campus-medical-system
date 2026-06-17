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
        <el-form-item label="就诊科室">
          <el-select v-model="queryParams.department" placeholder="请选择科室" clearable>
            <el-option label="内科" value="内科" />
            <el-option label="外科" value="外科" />
            <el-option label="口腔科" value="口腔科" />
            <el-option label="眼科" value="眼科" />
            <el-option label="皮肤科" value="皮肤科" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询可预约号源</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>可预约医生列表</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="doctorName" label="医生姓名" />
        <el-table-column prop="department" label="科室" />
        <el-table-column prop="title" label="职称" />
        <el-table-column prop="visitDate" label="出诊日期" />
        <el-table-column prop="visitTime" label="出诊时段" />
        <el-table-column prop="totalNumber" label="总号源" width="100" align="center" />
        <el-table-column prop="remainNumber" label="剩余号源" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.remainNumber > 0 ? 'success' : 'danger'">{{ row.remainNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="small"
                @click="handleRegister(row)"
                :disabled="row.remainNumber <= 0"
            >
              预约
            </el-button>
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

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>我的预约</span>
        </div>
      </template>
      <el-table :data="myAppointments" stripe v-loading="myLoading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="appointmentNo" label="预约编号" />
        <el-table-column prop="doctorName" label="医生" />
        <el-table-column prop="deptName" label="科室" />
        <el-table-column prop="appointmentDate" label="就诊日期" />
        <el-table-column prop="timeSlot" label="时段" />
        <el-table-column prop="queueNo" label="排队号" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getAppointmentStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button
                v-if="row.canDirectCancel"
                type="danger"
                size="small"
                @click="handleDirectCancel(row)"
            >
              退号
            </el-button>
            <el-button
                v-else-if="row.canApplyCancel"
                type="warning"
                size="small"
                @click="handleApplyCancel(row)"
            >
              申请退号
            </el-button>
            <el-tag v-else-if="row.pendingCancelApproval" type="info">退号审核中</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="确认预约" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="医生">{{ currentRow.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ currentRow.department }}</el-descriptions-item>
        <el-descriptions-item label="日期">{{ currentRow.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="时段">{{ currentRow.visitTime }}</el-descriptions-item>
        <el-descriptions-item label="挂号费">¥{{ currentRow.registerFee || 10 }}</el-descriptions-item>
      </el-descriptions>
      <el-form style="margin-top: 15px;">
        <el-form-item label="主要症状">
          <el-input v-model="symptom" placeholder="请描述主要症状" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="confirmLoading">确认预约</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="cancelDialogVisible" title="申请退号" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="预约编号">{{ cancelRow.appointmentNo }}</el-descriptions-item>
        <el-descriptions-item label="医生">{{ cancelRow.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="就诊时间">{{ cancelRow.appointmentDate }} {{ cancelRow.timeSlot }}</el-descriptions-item>
      </el-descriptions>
      <el-form style="margin-top: 15px;">
        <el-form-item label="退号原因">
          <el-input v-model="cancelReason" type="textarea" :rows="3" placeholder="请填写退号原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmCancel" :loading="cancelLoading">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const loading = ref(false)
const myLoading = ref(false)
const confirmLoading = ref(false)
const cancelLoading = ref(false)
const dialogVisible = ref(false)
const cancelDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const symptom = ref('')

const queryParams = reactive({
  visitDate: new Date().toISOString().split('T')[0],
  department: ''
})

const tableData = ref([])
const myAppointments = ref([])
const cancelRow = ref({})
const cancelReason = ref('')

const getAppointmentStatusType = (status) => {
  const map = {
    '待确认': 'warning',
    '已确认': 'success',
    '待审批': 'info',
    '已完成': '',
    '已取消': 'info'
  }
  return map[status] || ''
}

const loadMyAppointments = async () => {
  myLoading.value = true
  try {
    myAppointments.value = await api.getMyAppointments() || []
  } catch (error) {
    ElMessage.error('加载我的预约失败')
  } finally {
    myLoading.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = { visitDate: queryParams.visitDate }
    if (queryParams.department) {
      params.deptName = queryParams.department
    }
    const list = await api.getScheduleDetailList(params)
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
  queryParams.department = ''
  handleSearch()
}

const handleRegister = (row) => {
  currentRow.value = row
  symptom.value = ''
  dialogVisible.value = true
}

const handleConfirm = async () => {
  confirmLoading.value = true
  try {
    const res = await api.submitRegister({
      scheduleId: currentRow.value.id,
      visitDate: currentRow.value.visitDate,
      visitTime: currentRow.value.visitTime,
      symptom: symptom.value || '待诊'
    })
    ElMessage.success(`预约成功！预约编号：${res.registerNo}，排队号：${res.queueNo}`)
    dialogVisible.value = false
    loadData()
    loadMyAppointments()
  } catch (error) {
    console.error(error)
  } finally {
    confirmLoading.value = false
  }
}

const handleDirectCancel = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要退号吗？预约编号：${row.appointmentNo}`,
        '退号确认',
        { type: 'warning' }
    )
    await api.cancelAppointment(row.id)
    ElMessage.success('退号成功')
    loadMyAppointments()
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleApplyCancel = (row) => {
  cancelRow.value = row
  cancelReason.value = ''
  cancelDialogVisible.value = true
}

const handleConfirmCancel = async () => {
  if (!cancelReason.value.trim()) {
    ElMessage.warning('请填写退号原因')
    return
  }
  cancelLoading.value = true
  try {
    await api.submitCancelRegister({
      appointmentId: cancelRow.value.id,
      reason: cancelReason.value
    })
    ElMessage.success('退号申请已提交，请等待审批')
    cancelDialogVisible.value = false
    loadMyAppointments()
  } catch (error) {
    console.error(error)
  } finally {
    cancelLoading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

onMounted(() => {
  loadData()
  loadMyAppointments()
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
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
