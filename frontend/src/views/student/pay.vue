<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="订单状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="待缴费" value="待支付" />
            <el-option label="已缴费" value="已支付" />
            <el-option label="已取消" value="已取消" />
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
          <span>缴费订单列表</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单编号" />
        <el-table-column prop="item" label="收费项目" />
        <el-table-column prop="amount" label="缴费金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开单时间" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button
                v-if="row.rawStatus === '待支付'"
                type="primary"
                size="small"
                @click="handlePay(row)"
            >
              缴费
            </el-button>
            <el-button
                v-else-if="row.rawStatus === '已支付' && !row.refundPending"
                type="warning"
                size="small"
                @click="handleRefund(row)"
            >
              申请退费
            </el-button>
            <el-tag v-else-if="row.refundPending" type="info">退费审核中</el-tag>
            <el-button v-else type="info" size="small" disabled>已完成</el-button>
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

    <el-dialog v-model="payDialogVisible" title="确认缴费" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单编号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="收费项目">{{ currentRow.item }}</el-descriptions-item>
        <el-descriptions-item label="缴费金额">
          <span style="color: #f56c6c; font-size: 18px; font-weight: 600;">¥{{ currentRow.amount }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-alert
          title="请选择支付方式"
          type="info"
          :closable="false"
          style="margin-top: 20px;"
      />
      <el-radio-group v-model="payMethod" style="margin-top: 15px;">
        <el-radio label="wechat">微信支付</el-radio>
        <el-radio label="alipay">支付宝</el-radio>
        <el-radio label="campus">校园卡</el-radio>
      </el-radio-group>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmPay" :loading="payLoading">确认支付</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="refundDialogVisible" title="申请退费" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="订单编号">{{ currentRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="收费项目">{{ currentRow.item }}</el-descriptions-item>
        <el-descriptions-item label="退费金额">
          <span style="color: #f56c6c; font-weight: 600;">¥{{ currentRow.amount }}</span>
        </el-descriptions-item>
      </el-descriptions>
      <el-form style="margin-top: 15px;">
        <el-form-item label="退费原因">
          <el-input v-model="refundReason" type="textarea" :rows="3" placeholder="请填写退费原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmRefund" :loading="refundLoading">提交申请</el-button>
      </template>
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
const payLoading = ref(false)
const refundLoading = ref(false)
const payDialogVisible = ref(false)
const refundDialogVisible = ref(false)
const refundReason = ref('')
const pendingRefundOrderIds = ref(new Set())
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const payMethod = ref('wechat')

const queryParams = reactive({
  status: '',
  dateRange: []
})

const tableData = ref([])

const statusMap = {
  '待支付': '待缴费',
  '已支付': '已缴费',
  '已取消': '已取消',
  '已退费': '已退费'
}

const getStatusType = (status) => {
  const map = {
    '待缴费': 'warning',
    '已缴费': 'success',
    '已取消': 'info',
    '已退费': 'danger'
  }
  return map[status] || ''
}

const formatOrder = (order) => ({
  id: order.id,
  orderNo: order.orderNo,
  item: order.prescriptionId ? '挂号费+药品费' : '挂号费',
  amount: order.totalAmount,
  createTime: order.createTime ? order.createTime.replace('T', ' ').substring(0, 16) : '',
  status: statusMap[order.status] || order.status,
  rawStatus: order.status,
  refundPending: pendingRefundOrderIds.value.has(order.id)
})

const loadRefundApplications = async () => {
  try {
    const list = await api.getMyRefundApplications() || []
    pendingRefundOrderIds.value = new Set(
        list.filter(item => item.status === '待审核').map(item => item.businessId)
    )
  } catch (error) {
    pendingRefundOrderIds.value = new Set()
  }
}

const loadData = async () => {
  loading.value = true
  try {
    await loadRefundApplications()
    const params = { userId: userStore.userInfo.id }
    if (queryParams.status) {
      params.status = queryParams.status
    }
    if (queryParams.dateRange && queryParams.dateRange.length === 2) {
      params.startDate = queryParams.dateRange[0]
      params.endDate = queryParams.dateRange[1]
    }
    const list = await api.getOrderList(params)
    tableData.value = (list || []).map(formatOrder)
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
  queryParams.status = ''
  queryParams.dateRange = []
  handleSearch()
}

const handlePay = (row) => {
  currentRow.value = row
  payMethod.value = 'wechat'
  payDialogVisible.value = true
}

const handleRefund = (row) => {
  currentRow.value = row
  refundReason.value = ''
  refundDialogVisible.value = true
}

const handleConfirmRefund = async () => {
  if (!refundReason.value.trim()) {
    ElMessage.warning('请填写退费原因')
    return
  }
  refundLoading.value = true
  try {
    await api.submitRefund({
      orderId: currentRow.value.id,
      reason: refundReason.value
    })
    ElMessage.success('退费申请已提交，请等待财务审核')
    refundDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    refundLoading.value = false
  }
}

const handleConfirmPay = async () => {
  payLoading.value = true
  try {
    await api.payOrder({
      orderId: currentRow.value.id,
      payType: payMethod.value
    })
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    payLoading.value = false
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
