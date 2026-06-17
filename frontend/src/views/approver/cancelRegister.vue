<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="申请日期">
          <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
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
          <span>退号申请列表</span>
          <el-tag type="warning" style="margin-left: 20px;">待审核：{{ pendingCount }}</el-tag>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="applyNo" label="申请编号" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="registerNo" label="挂号单号" />
        <el-table-column prop="doctorName" label="医生姓名" />
        <el-table-column prop="registerFee" label="挂号费" width="100" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.registerFee }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reason" label="退号原因" show-overflow-tooltip />
        <el-table-column prop="applyDate" label="申请日期" />
        <el-table-column prop="status" label="审批状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button
                v-if="row.status === 'pending'"
                type="success"
                size="small"
                @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
                v-if="row.status === 'pending'"
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

    <el-dialog v-model="viewDialogVisible" title="退号申请详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ currentRow.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentRow.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="挂号单号">{{ currentRow.registerNo }}</el-descriptions-item>
        <el-descriptions-item label="医生姓名">{{ currentRow.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="挂号费">
          <span style="color: #f56c6c; font-weight: 600;">¥{{ currentRow.registerFee }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="退号原因" :span="2">{{ currentRow.reason }}</el-descriptions-item>
        <el-descriptions-item label="申请日期">{{ currentRow.applyDate }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ getStatusText(currentRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ currentRow.approvalOpinion || '暂无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="approvalDialogVisible" :title="approvalTitle" width="600px">
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="申请编号">
          <el-input :value="currentRow.applyNo" disabled />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input :value="currentRow.studentName" disabled />
        </el-form-item>
        <el-form-item label="挂号单号">
          <el-input :value="currentRow.registerNo" disabled />
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input :value="`¥${currentRow.registerFee}`" disabled />
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
          {{ approvalType === 'approve' ? '确认通过并退款' : '确认驳回' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

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
  dateRange: [],
  status: ''
})

const approvalForm = reactive({
  opinion: ''
})

const approvalRules = {
  opinion: [{ required: true, message: '请填写审批意见', trigger: 'blur' }]
}

const tableData = ref([])

const statusToKey = { 0: 'pending', 1: 'approved', 2: 'rejected' }
const keyToAuditStatus = { approve: '已通过', reject: '已驳回' }

const formatItem = (item) => ({
  id: item.id,
  applyNo: 'CR' + item.id,
  studentName: item.studentName,
  studentNo: item.studentNo || '',
  registerNo: item.appointmentNo,
  doctorName: item.doctorName || '',
  registerFee: item.amount,
  reason: item.reason,
  applyDate: item.createTime ? item.createTime.substring(0, 10) : '',
  status: statusToKey[item.status] || 'pending',
  approvalOpinion: ''
})

const pendingCount = computed(() => {
  return tableData.value.filter(item => item.status === 'pending').length
})

const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || ''
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回'
  }
  return textMap[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      startTime: queryParams.dateRange?.[0],
      endTime: queryParams.dateRange?.[1]
    }
    if (queryParams.status === 'pending') params.auditStatus = 0
    else if (queryParams.status === 'approved') params.auditStatus = 1
    else if (queryParams.status === 'rejected') params.auditStatus = 2
    const res = await api.getCancelRegList(params)
    let list = (res.records || []).map(formatItem)
    if (queryParams.studentName) {
      list = list.filter(item => item.studentName && item.studentName.includes(queryParams.studentName))
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
  queryParams.studentName = ''
  queryParams.dateRange = []
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
  const valid = await approvalFormRef.value.validate()
  if (valid) {
    approvalLoading.value = true
    try {
      await api.auditCancelReg({
        cancelId: currentRow.value.id,
        auditStatus: keyToAuditStatus[approvalType.value],
        remark: approvalForm.opinion
      })
      ElMessage.success(approvalType.value === 'approve' ? '审批通过，退款已处理' : '已驳回')
      approvalDialogVisible.value = false
      loadData()
    } catch (error) {
      ElMessage.error('操作失败')
    } finally {
      approvalLoading.value = false
    }
  }
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
