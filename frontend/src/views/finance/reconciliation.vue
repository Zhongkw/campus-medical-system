
<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="对账日期">
          <el-date-picker
              v-model="queryParams.date"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="对账类型">
          <el-select v-model="queryParams.type" placeholder="请选择类型">
            <el-option label="日对账" value="daily" />
            <el-option label="月对账" value="monthly" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出对账单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #2385bb;">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ summaryData.totalIncome }}</div>
              <div class="stat-title">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #36b37e;">
              <el-icon :size="30"><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ summaryData.wechatIncome }}</div>
              <div class="stat-title">微信支付</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon :size="30"><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ summaryData.alipayIncome }}</div>
              <div class="stat-title">支付宝</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon :size="30"><CreditCard /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ summaryData.campusIncome }}</div>
              <div class="stat-title">校园卡</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>对账明细</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="payMethod" label="支付方式" width="120" align="center" />
        <el-table-column prop="orderCount" label="订单数量" width="100" align="center" />
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="refundCount" label="退费笔数" width="100" align="center" />
        <el-table-column prop="refundAmount" label="退费金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #909399;">¥{{ row.refundAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="actualAmount" label="实际收入" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #67c23a; font-weight: 600;">¥{{ row.actualAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="reconciliationStatus" label="对账状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.reconciliationStatus === '已对账' ? 'success' : 'warning'">
              {{ row.reconciliationStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleViewDetail(row)">查看明细</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download, Money, Wallet, Coin, CreditCard } from '@element-plus/icons-vue'
import { api } from '../../api'

const loading = ref(false)

const queryParams = reactive({
  date: new Date().toISOString().split('T')[0],
  type: 'daily'
})

const summaryData = reactive({
  totalIncome: '0.00',
  wechatIncome: '0.00',
  alipayIncome: '0.00',
  campusIncome: '0.00'
})

const tableData = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const data = await api.getFinanceReconciliation({ date: queryParams.date })
    if (data) {
      summaryData.totalIncome = Number(data.totalIncome || 0).toFixed(2)
      summaryData.wechatIncome = Number(data.wechatIncome || 0).toFixed(2)
      summaryData.alipayIncome = Number(data.alipayIncome || 0).toFixed(2)
      summaryData.campusIncome = Number(data.campusIncome || 0).toFixed(2)
      tableData.value = (data.details || []).map((item, index) => ({
        id: index + 1,
        payMethod: item.payMethod,
        orderCount: item.orderCount || 0,
        totalAmount: Number(item.totalAmount || 0).toFixed(2),
        refundCount: item.refundCount || 0,
        refundAmount: Number(item.refundAmount || 0).toFixed(2),
        actualAmount: Number(item.actualAmount || 0).toFixed(2),
        reconciliationStatus: item.reconciliationStatus || '已对账'
      }))
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadData()
}

const handleExport = () => {
  ElMessage.success('对账单导出成功')
}

const handleViewDetail = (row) => {
  ElMessage.info(`查看 ${row.payMethod} 的详细对账记录`)
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
.stats-row {
  margin-bottom: 20px;
}
.stat-content {
  display: flex;
  align-items: center;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.table-card {
  margin-bottom: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>
