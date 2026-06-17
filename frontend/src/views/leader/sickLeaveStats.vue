<template>
  <div class="page-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #2385bb;">
              <el-icon :size="30"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ summary.total }}</div>
              <div class="stat-title">病假申请总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #36b37e;">
              <el-icon :size="30"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ summary.approved }}</div>
              <div class="stat-title">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon :size="30"><CloseBold /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ summary.rejected }}</div>
              <div class="stat-title">已驳回</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <template #header><span>病假统计趋势</span></template>
      <div id="sickLeaveChart" style="width: 100%; height: 400px;"></div>
    </el-card>

    <el-card class="table-card">
      <template #header><span>各学院病假统计</span></template>
      <el-table :data="tableData" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="department" label="学院" />
        <el-table-column prop="totalCount" label="申请总数" align="center" />
        <el-table-column prop="approvedCount" label="通过数" align="center" />
        <el-table-column prop="rate" label="占比" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.rate" :color="'#67c23a'" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Tickets, CircleCheck, CloseBold } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { api } from '../../api'

const summary = reactive({ total: 0, approved: 0, rejected: 0 })
const tableData = ref([])
let chartInstance = null

const loadData = async () => {
  try {
    const [summaryData, list] = await Promise.all([
      api.getSickLeaveSummary(),
      api.getCollegeSickStat()
    ])
    if (summaryData) {
      summary.total = Number(summaryData.total || 0)
      summary.approved = Number(summaryData.approved || 0)
      summary.rejected = Number(summaryData.rejected || 0)
    }
    tableData.value = (list || []).map(item => ({
      department: item.college,
      totalCount: Number(item.applyCount || item.count || 0),
      approvedCount: Number(item.approvedCount || 0),
      rate: Math.round(Number(item.percentage || 0))
    }))
    initChart()
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const initChart = () => {
  const chartDom = document.getElementById('sickLeaveChart')
  if (!chartDom) return
  if (!chartInstance) chartInstance = echarts.init(chartDom)
  chartInstance.setOption({
    title: { text: '各学院病假分布', left: 'center' },
    xAxis: { type: 'category', data: tableData.value.map(i => i.department) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: tableData.value.map(i => i.totalCount), itemStyle: { color: '#2385bb' } }]
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 0; }
.stats-row { margin-bottom: 20px; }
.stat-content { display: flex; align-items: center; }
.stat-icon { width: 60px; height: 60px; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #fff; margin-right: 15px; }
.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: 600; }
.stat-title { font-size: 14px; color: #909399; margin-top: 5px; }
.chart-card, .table-card { margin-bottom: 20px; }
</style>
