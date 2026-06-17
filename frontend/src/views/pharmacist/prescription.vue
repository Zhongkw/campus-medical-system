<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="处方编号">
          <el-input v-model="queryParams.prescriptionNo" placeholder="请输入处方编号" clearable />
        </el-form-item>
        <el-form-item label="配药状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待配药" value="待配药" />
            <el-option label="配药中" value="配药中" />
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
          <span>待配药处方列表</span>
          <el-tag type="warning" style="margin-left: 20px;">待配药：{{ pendingCount }}</el-tag>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="prescriptionNo" label="处方编号" />
        <el-table-column prop="patientName" label="患者姓名" />
        <el-table-column prop="doctorName" label="开具医生" />
        <el-table-column prop="medicineCount" label="药品种类" width="100" align="center" />
        <el-table-column prop="createTime" label="开具时间" />
        <el-table-column prop="status" label="配药状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看处方</el-button>
            <el-button v-if="row.status === '待配药'" type="success" size="small" @click="handleDispense(row)">开始配药</el-button>
            <el-button v-if="row.status === '配药中'" type="primary" size="small" @click="handleComplete(row)">完成发药</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="处方详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="处方编号">{{ currentRow.prescriptionNo }}</el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ currentRow.patientName }}</el-descriptions-item>
        <el-descriptions-item label="开具医生">{{ currentRow.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="开具时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果" :span="2">{{ currentRow.diagnosis }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="currentRow.medicines || []" stripe style="margin-top: 15px;">
        <el-table-column prop="name" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="usage" label="用法用量" />
        <el-table-column label="配药状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.dispensed ? 'success' : 'warning'">{{ row.dispensed ? '已配' : '未配' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentRow = ref({})
const queryParams = reactive({ prescriptionNo: '', status: '' })
const tableData = ref([])
const allData = ref([])

const pendingCount = computed(() => allData.value.filter(item => item.status === '待配药').length)

const getStatusType = (status) => {
  const map = { '待配药': 'warning', '配药中': 'primary', '已完成': 'success' }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const statuses = queryParams.status ? [queryParams.status] : ['待配药', '配药中', '已完成']
    const results = await Promise.all(
        statuses.map(s => api.getPrescriptionDetailList({ status: s, prescriptionNo: queryParams.prescriptionNo || undefined }))
    )
    allData.value = results.flat().filter((item, index, arr) =>
        arr.findIndex(i => i.id === item.id) === index
    )
    tableData.value = allData.value
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => loadData()
const handleReset = () => { queryParams.prescriptionNo = ''; queryParams.status = ''; loadData() }

const handleView = async (row) => {
  currentRow.value = await api.getPrescriptionDetail(row.id)
  detailDialogVisible.value = true
}

const handleDispense = async (row) => {
  try {
    await ElMessageBox.confirm(`确定开始配药 ${row.prescriptionNo} 吗？`, '配药确认', { type: 'info' })
    await api.startDispense(row.id)
    ElMessage.success('已开始配药')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定完成发药 ${row.prescriptionNo} 吗？`, '完成确认', { type: 'success' })
    await api.completeDispense(row.id)
    ElMessage.success('发药完成')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; display: flex; align-items: center; }
</style>
