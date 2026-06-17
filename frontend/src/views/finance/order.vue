// 收费订单管理
<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="订单编号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单编号" clearable />
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待缴费" value="待缴费" />
            <el-option label="已缴费" value="已缴费" />
            <el-option label="已退费" value="已退费" />
          </el-select>
        </el-form-item>
        <el-form-item label="开单时间">
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
          <span>收费订单列表</span>
          <div class="header-stats">
            <el-tag type="warning">待缴费：{{ pendingCount }}</el-tag>
            <el-tag type="success" style="margin-left: 10px;">已缴费：{{ paidCount }}</el-tag>
            <el-tag type="info" style="margin-left: 10px;">今日收入：¥{{ todayIncome }}</el-tag>
          </div>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
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
        <el-table-column prop="payMethod" label="支付方式" width="100" align="center" />
        <el-table-column prop="createTime" label="开单时间" />
        <el-table-column prop="payTime" label="缴费时间" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看详情</el-button>
            <el-button
                v-if="row.status === '已缴费'"
                type="warning"
                size="small"
                @click="handleRefund(row)"
            >
              退费
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

    <el-dialog v-model="detailDialogVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentRow.studentName }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ currentRow.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="收费项目">{{ currentRow.item }}</el-descriptions-item>
        <el-descriptions-item label="金额">
          <span style="color: #f56c6c; font-size: 18px; font-weight: 600;">¥{{ currentRow.amount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ currentRow.payMethod || '未支付' }}</el-descriptions-item>
        <el-descriptions-item label="开单时间">{{ currentRow.createTime }}</el-descriptions-item>
        <el-descriptions-item label="缴费时间">{{ currentRow.payTime || '未缴费' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'

const loading = ref(false)
const detailDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})

const queryParams = reactive({
  orderNo: '',
  studentName: '',
  status: '',
  dateRange: []
})

const tableData = ref([])

const statusMap = { 0: '待缴费', 1: '已缴费', 2: '已取消', 3: '已退费' }
const statusCodeMap = { '待缴费': 0, '已缴费': 1, '已退费': 3 }

const formatOrder = (item) => ({
  id: item.id,
  orderNo: item.orderNo,
  studentName: item.studentName,
  studentNo: item.studentKey || '',
  item: item.itemName || '诊疗费',
  amount: item.amount,
  payMethod: item.payTypeName || '',
  createTime: item.createTime ? String(item.createTime).replace('T', ' ').substring(0, 16) : '',
  payTime: item.payTime ? String(item.payTime).replace('T', ' ').substring(0, 16) : '',
  status: item.statusName || statusMap[item.status] || '待缴费'
})

const pendingCount = computed(() => {
  return tableData.value.filter(item => item.status === '待缴费').length
})

const paidCount = computed(() => {
  return tableData.value.filter(item => item.status === '已缴费').length
})

const todayIncome = ref('0.00')

const getStatusType = (status) => {
  const map = {
    '待缴费': 'warning',
    '已缴费': 'success',
    '已退费': 'info'
  }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      orderNo: queryParams.orderNo || undefined,
      studentKey: queryParams.studentName || undefined,
      orderStatus: queryParams.status ? statusCodeMap[queryParams.status] : undefined,
      startTime: queryParams.dateRange?.[0] || undefined,
      endTime: queryParams.dateRange?.[1] || undefined
    }
    const res = await api.getFinanceOrderList(params)
    tableData.value = (res.records || []).map(formatOrder)
    total.value = res.total || tableData.value.length
    const dashboard = await api.getFinanceDashboard()
    if (dashboard) {
      todayIncome.value = Number(dashboard.todayIncome || 0).toFixed(2)
    }
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
  queryParams.orderNo = ''
  queryParams.studentName = ''
  queryParams.status = ''
  queryParams.dateRange = []
  handleSearch()
}

const handleView = (row) => {
  currentRow.value = row
  detailDialogVisible.value = true
}

const handleRefund = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要退费订单 ${row.orderNo} 吗？退费金额：¥${row.amount}`, '退费确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.auditRefund({
      orderId: row.id,
      auditStatus: '3',
      auditRemark: '财务退费'
    })
    row.status = '已退费'
    ElMessage.success('退费成功')
    loadData()
  } catch (error) {
    // 用户取消
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
  justify-content: space-between;
  align-items: center;
}
.header-stats {
  display: flex;
  align-items: center;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
