//未缴费催缴
<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="超期天数">
          <el-select v-model="queryParams.overdueDays" placeholder="请选择超期天数" clearable>
            <el-option label="3天以上" value="3" />
            <el-option label="7天以上" value="7" />
            <el-option label="15天以上" value="15" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="warning" @click="handleBatchRemind">
            <el-icon><Bell /></el-icon>
            批量催缴
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>未缴费订单列表</span>
          <el-tag type="danger" style="margin-left: 20px;">待缴费：{{ pendingCount }}</el-tag>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单编号" />
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="item" label="收费项目" show-overflow-tooltip />
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开单时间" />
        <el-table-column prop="overdueDays" label="超期天数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getOverdueType(row.overdueDays)">{{ row.overdueDays }}天</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reminderCount" label="催缴次数" width="100" align="center" />
        <el-table-column prop="lastReminderTime" label="最后催缴时间" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleRemind(row)">催缴</el-button>
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

    <el-dialog v-model="detailDialogVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentRow.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="收费项目">{{ currentRow.item }}</el-descriptions-item>
        <el-descriptions-item label="金额">
          <span style="color: #f56c6c; font-size: 18px; font-weight: 600;">¥{{ currentRow.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="开单时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="超期天数">
          <el-tag :type="getOverdueType(currentRow.overdueDays)">{{ currentRow.overdueDays }}天</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="催缴次数">{{ currentRow.reminderCount }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { api } from '../../api'
import { Bell } from '@element-plus/icons-vue'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const selectedRows = ref([])

const queryParams = reactive({
  studentName: '',
  overdueDays: ''
})

const tableData = ref([])

const calcOverdueDays = (createTime) => {
  if (!createTime) return 0
  const created = new Date(String(createTime).replace('T', ' '))
  const diff = Date.now() - created.getTime()
  return Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)))
}

const formatReminder = (item) => ({
  id: item.id,
  orderNo: item.orderNo,
  studentName: item.studentName,
  studentNo: item.studentKey || '',
  item: item.itemName || '诊疗费',
  amount: item.amount,
  createTime: item.createTime ? String(item.createTime).replace('T', ' ').substring(0, 16) : '',
  overdueDays: calcOverdueDays(item.createTime),
  reminderCount: 0,
  lastReminderTime: ''
})

const pendingCount = computed(() => tableData.value.length)

const getOverdueType = (days) => {
  if (days >= 15) return 'danger'
  if (days >= 7) return 'warning'
  return 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getFinanceOrderList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      orderStatus: 0,
      studentKey: queryParams.studentName || undefined,
      minOverdueDays: queryParams.overdueDays ? parseInt(queryParams.overdueDays) : undefined
    })
    const list = (res.records || []).map(formatReminder)
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
  queryParams.overdueDays = ''
  handleSearch()
}

const handleSelectionChange = (rows) => {
  selectedRows.value = rows
}

const handleView = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleRemind = async (row) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    row.reminderCount++
    row.lastReminderTime = new Date().toLocaleString('zh-CN', { hour12: false })
    ElMessage.success(`已向 ${row.studentName} 发送催缴通知`)
  } catch (error) {
    ElMessage.error('催缴失败')
  }
}

const handleBatchRemind = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先选择要催缴的订单')
    return
  }
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    selectedRows.value.forEach(row => {
      row.reminderCount++
      row.lastReminderTime = new Date().toLocaleString('zh-CN', { hour12: false })
    })
    ElMessage.success(`已向 ${selectedRows.value.length} 位学生发送催缴通知`)
  } catch (error) {
    ElMessage.error('批量催缴失败')
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
