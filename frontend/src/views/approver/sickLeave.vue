<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
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
          <span>病假申请列表</span>
          <el-tag type="warning" style="margin-left: 20px;">待审核：{{ pendingCount }}</el-tag>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="applyNo" label="申请编号" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="department" label="就诊科室" />
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="days" label="请假天数" width="100" align="center" />
        <el-table-column prop="applyDate" label="申请日期" />
        <el-table-column prop="status" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button
                v-if="row.status === '待审核'"
                type="success"
                size="small"
                @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
                v-if="row.status === '待审核'"
                type="danger"
                size="small"
                @click="handleReject(row)"
            >
              驳回
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

    <el-dialog v-model="viewDialogVisible" title="病假申请详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentRow.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentRow.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="就诊科室">{{ currentRow.department }}</el-descriptions-item>
        <el-descriptions-item label="就诊日期">{{ currentRow.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果">{{ currentRow.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ currentRow.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ currentRow.endDate }}</el-descriptions-item>
        <el-descriptions-item label="请假天数">{{ currentRow.days }}天</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentRow.approvalOpinion || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="approvalDialogVisible" :title="approvalTitle" width="600px">
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="申请编号">
          <el-input v-model="currentRow.applyNo" disabled />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="currentRow.studentName" disabled />
        </el-form-item>
        <el-form-item label="请假天数">
          <el-input :value="`${currentRow.days}天`" disabled />
        </el-form-item>
        <el-form-item label="审批意见" prop="opinion">
          <el-input
              v-model="approvalForm.opinion"
              type="textarea"
              :rows="4"
              :placeholder="approvalType === 'approve' ? '请输入通过意见' : '请输入驳回原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button
            :type="approvalType === 'approve' ? 'success' : 'danger'"
            @click="handleApprovalSubmit"
            :loading="approvalLoading"
        >
          {{ approvalType === 'approve' ? '确认通过' : '确认驳回' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()
const loading = ref(false)
const approvalLoading = ref(false)
const viewDialogVisible = ref(false)
const approvalDialogVisible = ref(false)
const approvalTitle = ref('')
const approvalType = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const approvalFormRef = ref(null)

const queryParams = reactive({
  studentName: '',
  status: ''
})

const approvalForm = reactive({
  opinion: ''
})

const approvalRules = {
  opinion: [{ required: true, message: '请填写审批意见', trigger: 'blur' }]
}

const tableData = ref([])
const allData = ref([])

const pendingCount = computed(() => {
  return tableData.value.filter(item => item.status === '待审核').length
})

const getStatusType = (status) => {
  const map = {
    '待审核': 'warning',
    '已通过': 'success',
    '已驳回': 'danger'
  }
  return map[status] || ''
}

const formatItem = (item) => ({
  id: item.id,
  applyNo: item.leaveNo,
  studentName: item.studentName,
  studentNo: item.studentId,
  department: item.department || item.college || '',
  diagnosis: item.diagnosis,
  visitDate: item.visitDate || '',
  startDate: item.startDate,
  endDate: item.endDate,
  days: item.leaveDays,
  reason: '',
  applyDate: item.createTime ? item.createTime.substring(0, 10) : '',
  status: item.status,
  approvalOpinion: item.auditContent || ''
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (queryParams.status) {
      params.status = queryParams.status
    }
    const list = await api.getSickLeaveApprovalList(params)
    allData.value = (list || []).map(formatItem)
    let filtered = allData.value
    if (queryParams.studentName) {
      filtered = filtered.filter(item => item.studentName && item.studentName.includes(queryParams.studentName))
    }
    tableData.value = filtered
    total.value = filtered.length
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
  queryParams.studentName = ''
  queryParams.status = ''
  handleSearch()
}

const handleView = (row) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

const handleApprove = (row) => {
  currentRow.value = row
  approvalType.value = 'approve'
  approvalTitle.value = '审批通过'
  approvalForm.opinion = ''
  approvalDialogVisible.value = true
}

const handleReject = (row) => {
  currentRow.value = row
  approvalType.value = 'reject'
  approvalTitle.value = '审批驳回'
  approvalForm.opinion = ''
  approvalDialogVisible.value = true
}

const handleApprovalSubmit = async () => {
  if (!approvalFormRef.value) return
  await approvalFormRef.value.validate(async (valid) => {
    if (valid) {
      approvalLoading.value = true
      try {
        await api.auditSickLeave({
          applyId: currentRow.value.id,
          auditStatus: approvalType.value === 'approve' ? 'PASS' : 'REJECT',
          auditContent: approvalForm.opinion,
          auditorId: userStore.userInfo.id
        })
        ElMessage.success(approvalType.value === 'approve' ? '审批通过' : '已驳回')
        approvalDialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        approvalLoading.value = false
      }
    }
  })
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

onMounted(() => {
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
