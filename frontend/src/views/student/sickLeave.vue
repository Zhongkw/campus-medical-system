<template>
  <div class="page-container">
    <div class="header-actions">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增病假申请
      </el-button>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>病假申请记录</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="applyNo" label="申请编号" />
        <el-table-column prop="visitDate" label="就诊日期" />
        <el-table-column prop="department" label="就诊科室" />
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="startDate" label="开始日期" />
        <el-table-column prop="endDate" label="结束日期" />
        <el-table-column prop="days" label="天数" width="80" align="center" />
        <el-table-column prop="status" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button
                v-if="row.status === '待审核'"
                type="danger"
                size="small"
                @click="handleCancel(row)"
            >
              撤销
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="就诊记录" prop="recordId">
          <el-select v-model="formData.recordId" placeholder="关联就诊记录" style="width: 100%" @change="handleRecordChange">
            <el-option
                v-for="item in recordOptions"
                :key="item.id"
                :label="`${item.visitDate} ${item.department} ${item.diagnosis}`"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                  v-model="formData.startDate"
                  type="date"
                  placeholder="选择日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                  v-model="formData.endDate"
                  type="date"
                  placeholder="选择日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="申请原因" prop="reason">
          <el-input
              v-model="formData.reason"
              type="textarea"
              :rows="3"
              placeholder="请填写请假原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="病假申请详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentRow.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="就诊日期">{{ currentRow.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="就诊科室">{{ currentRow.department }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ currentRow.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ currentRow.endDate }}</el-descriptions-item>
        <el-descriptions-item label="请假天数">{{ currentRow.days }}天</el-descriptions-item>
        <el-descriptions-item label="申请原因" :span="2">{{ currentRow.reason }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2" v-if="currentRow.approvalOpinion">
          {{ currentRow.approvalOpinion }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增病假申请')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const formRef = ref(null)

const formData = reactive({
  recordId: '',
  visitDate: '',
  department: '',
  diagnosis: '',
  startDate: '',
  endDate: '',
  reason: ''
})

const rules = {
  recordId: [{ required: true, message: '请选择就诊记录', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  reason: [{ required: true, message: '请填写请假原因', trigger: 'blur' }]
}

const recordOptions = ref([])
const tableData = ref([])

const statusConvert = (status) => {
  const map = { 1: '待审核', 2: '已通过', 3: '已驳回', 4: '已撤回' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    '待审核': 'warning',
    '已通过': 'success',
    '已驳回': 'danger',
    '已撤回': 'info'
  }
  return map[status] || ''
}

const formatItem = (item) => ({
  id: item.id,
  applyNo: item.leaveNo,
  visitDate: item.createTime ? item.createTime.substring(0, 10) : '',
  department: item.college || '',
  diagnosis: item.diagnosis,
  startDate: item.startDate,
  endDate: item.endDate,
  days: item.leaveDays || item.days,
  reason: item.reason || '',
  status: statusConvert(item.status),
  approvalOpinion: item.auditContent || item.auditRemark || ''
})

const loadRecords = async () => {
  try {
    const records = await api.getPatientRecords(userStore.userInfo.id)
    recordOptions.value = (records || []).map(r => ({
      id: r.id,
      visitDate: r.createTime ? r.createTime.substring(0, 10) : '',
      department: '校医院',
      diagnosis: r.diagnosis || '待诊断'
    }))
  } catch (error) {
    recordOptions.value = []
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const list = await api.getMySickLeaves(userStore.userInfo.id)
    tableData.value = (list || []).map(formatItem)
    total.value = tableData.value.length
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleRecordChange = (recordId) => {
  const record = recordOptions.value.find(r => r.id === recordId)
  if (record) {
    formData.visitDate = record.visitDate
    formData.department = record.department
    formData.diagnosis = record.diagnosis
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增病假申请'
  formData.recordId = ''
  formData.visitDate = ''
  formData.department = ''
  formData.diagnosis = ''
  formData.startDate = ''
  formData.endDate = ''
  formData.reason = ''
  dialogVisible.value = true
}

const handleView = (row) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要撤销该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.withdrawSickLeave(row.id, userStore.userInfo.id)
    ElMessage.success('撤销成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        await api.submitSickLeave({
          visitDate: formData.visitDate,
          department: formData.department,
          diagnosis: formData.diagnosis,
          startDate: formData.startDate,
          endDate: formData.endDate
        }, userStore.userInfo.id)
        ElMessage.success('提交成功，等待审批')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
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

onMounted(() => {
  loadRecords()
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
