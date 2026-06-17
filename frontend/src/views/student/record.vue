<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="就诊日期">
          <el-date-picker
              v-model="queryParams.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="就诊科室">
          <el-select v-model="queryParams.department" placeholder="请选择科室" clearable>
            <el-option label="内科" value="内科" />
            <el-option label="外科" value="外科" />
            <el-option label="口腔科" value="口腔科" />
            <el-option label="眼科" value="眼科" />
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
          <span>历史就诊记录</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="visitDate" label="就诊日期" />
        <el-table-column prop="department" label="就诊科室" />
        <el-table-column prop="doctorName" label="接诊医生" />
        <el-table-column prop="symptom" label="主要症状" show-overflow-tooltip />
        <el-table-column prop="diagnosis" label="诊断结果" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看详情</el-button>
            <el-button type="success" size="small" @click="handleViewPrescription(row)">查看处方</el-button>
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

    <el-dialog v-model="detailDialogVisible" title="就诊详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="就诊日期">{{ currentRow.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="就诊科室">{{ currentRow.department }}</el-descriptions-item>
        <el-descriptions-item label="接诊医生">{{ currentRow.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="主要症状" :span="2">{{ currentRow.symptom }}</el-descriptions-item>
        <el-descriptions-item label="诊断结果" :span="2">{{ currentRow.diagnosis }}</el-descriptions-item>
        <el-descriptions-item label="医嘱" :span="2">{{ currentRow.advice }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="prescriptionDialogVisible" title="处方详情" width="700px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="处方编号">{{ currentRow.prescriptionNo }}</el-descriptions-item>
        <el-descriptions-item label="开具医生">{{ currentRow.prescribeDoctorName || currentRow.doctorName }}</el-descriptions-item>
        <el-descriptions-item label="开具日期">{{ currentRow.prescribeDate || currentRow.visitDate }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="currentRow.medicines" stripe style="margin-top: 15px;">
        <el-table-column prop="name" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="quantity" label="数量" width="80" align="center" />
        <el-table-column prop="usage" label="用法用量" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()

const loading = ref(false)
const detailDialogVisible = ref(false)
const prescriptionDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})

const queryParams = reactive({
  dateRange: [],
  department: ''
})

const tableData = ref([])

const formatRecord = (record) => ({
  id: record.id,
  prescriptionId: record.prescriptionId,
  visitDate: record.visitDate || '',
  department: record.department || '校医院',
  doctorName: record.doctorName || '',
  symptom: record.symptom || '',
  diagnosis: record.diagnosis || '',
  status: record.status || '已完成',
  advice: record.advice || '',
  prescriptionNo: record.prescriptionNo || '',
  prescribeDoctorName: record.prescribeDoctorName || record.doctorName || '',
  prescribeDate: record.prescribeDate || record.visitDate || '',
  medicines: (record.medicines || []).map(item => ({
    name: item.name,
    spec: item.spec,
    quantity: item.quantity,
    usage: item.usage
  }))
})

const getStatusType = (status) => {
  const map = {
    '待就诊': 'warning',
    '已接诊': 'primary',
    '已完成': 'success'
  }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const list = await api.getPatientRecordDetails(userStore.userInfo.id) || []
    tableData.value = list.map(formatRecord)
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
  queryParams.dateRange = []
  queryParams.department = ''
  handleSearch()
}

const handleViewDetail = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleViewPrescription = async (row) => {
  try {
    if (row.prescriptionId) {
      const detail = await api.getPrescriptionDetail(row.prescriptionId)
      currentRow.value = {
        ...row,
        prescriptionNo: detail.prescriptionNo || row.prescriptionNo,
        prescribeDoctorName: detail.doctorName || row.prescribeDoctorName,
        prescribeDate: detail.createTime || row.prescribeDate,
        medicines: (detail.medicines || []).map(item => ({
          name: item.name,
          spec: item.spec,
          quantity: item.quantity,
          usage: item.usage
        }))
      }
    } else {
      currentRow.value = row
    }
    prescriptionDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载处方详情失败')
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
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
