<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="处方状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待提交" value="草稿" />
            <el-option label="已提交" value="已提交" />
            <el-option label="待配药" value="待配药" />
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
        <div class="card-header"><span>处方列表</span></div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="prescriptionNo" label="处方编号" />
        <el-table-column prop="patientName" label="患者姓名" />
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="medicineCount" label="药品种类" width="100" align="center" />
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开具时间" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ formatStatus(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看详情</el-button>
            <el-button v-if="row.status === '草稿'" type="success" size="small" @click="handleSubmit(row)">提交药房</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="处方详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="处方编号">{{ currentRow.prescriptionNo }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ currentRow.patientName }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果" :span="2">{{ currentRow.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="开具时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ formatStatus(currentRow.status) }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="currentRow.medicines || []" stripe style="margin-top: 15px;">
        <el-table-column prop="name" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="price" label="单价" width="100" align="right" />
        <el-table-column prop="usage" label="用法用量" />
      </el-table>
      <div style="text-align: right; margin-top: 15px; font-size: 16px; font-weight: 600;">
        总计：¥{{ currentRow.totalAmount }}
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()
const loading = ref(false)
const detailDialogVisible = ref(false)
const currentRow = ref({})
const doctorId = ref(null)
const queryParams = reactive({ status: '' })
const tableData = ref([])

const formatStatus = (status) => {
  const map = { '草稿': '待提交', '已提交': '已提交', '待配药': '待配药', '配药中': '配药中', '已完成': '已完成' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { '草稿': 'info', '已提交': 'warning', '待配药': 'primary', '配药中': 'primary', '已完成': 'success' }
  return map[status] || ''
}

const loadData = async () => {
  if (!doctorId.value) return
  loading.value = true
  try {
    const params = { doctorId: doctorId.value }
    if (queryParams.status) params.status = queryParams.status
    tableData.value = await api.getPrescriptionDetailList(params) || []
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => { queryParams.status = ''; loadData() }

const handleView = async (row) => {
  try {
    currentRow.value = await api.getPrescriptionDetail(row.id)
    detailDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async (row) => {
  try {
    await ElMessageBox.confirm(`确定将处方 ${row.prescriptionNo} 提交到药房吗？`, '提交确认', { type: 'info' })
    await api.submitPrescription(row.id, doctorId.value)
    ElMessage.success('处方已提交，学生端将收到缴费订单')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(async () => {
  doctorId.value = await api.getDoctorIdByUserId(userStore.userInfo.id)
  loadData()
})
</script>

<style scoped>
.page-container { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; }
</style>
