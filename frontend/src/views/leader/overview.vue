<template>
  <div class="page-container">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #2385bb;">
              <el-icon :size="30"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.monthVisit }}</div>
              <div class="stat-title">本月就诊人次</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #36b37e;">
              <el-icon :size="30"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayVisit }}</div>
              <div class="stat-title">今日就诊</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon :size="30"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.monthIncome }}</div>
              <div class="stat-title">本月收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon :size="30"><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.sickLeaveCount }}</div>
              <div class="stat-title">病假申请</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>全院数据总览</span>
        </div>
      </template>
      <div id="overviewChart" style="width: 100%; height: 400px;"></div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>科室就诊排行</span></template>
          <el-table :data="deptRanking" size="small">
            <el-table-column type="index" label="排名" width="60" align="center" />
            <el-table-column prop="name" label="科室" />
            <el-table-column prop="count" label="就诊人次" align="center" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>药品消耗排行</span></template>
          <el-table :data="drugRanking" size="small">
            <el-table-column type="index" label="排名" width="60" align="center" />
            <el-table-column prop="name" label="药品" />
            <el-table-column prop="count" label="消耗量" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, TrendCharts, Money, Tickets } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { api } from '../../api'

const stats = reactive({
  monthVisit: 0,
  todayVisit: 0,
  monthIncome: '0',
  sickLeaveCount: 0
})
const deptRanking = ref([])
const drugRanking = ref([])
let chartInstance = null

const loadData = async () => {
  try {
    const [visitTrend, deptRank, financeDashboard, sickStat, drugRank] = await Promise.all([
      api.getVisitTrend({}),
      api.getDeptVisitRank(),
      api.getFinanceDashboard(),
      api.getCollegeSickStat(),
      api.getDrugConsumeRank({})
    ])
    const trend = visitTrend || []
    stats.monthVisit = trend.reduce((sum, item) => sum + Number(item.count || 0), 0)
    stats.todayVisit = trend.length ? Number(trend[trend.length - 1].count || 0) : 0
    stats.monthIncome = Number(financeDashboard?.monthIncome || 0).toFixed(0)
    stats.sickLeaveCount = (sickStat || []).reduce((sum, item) => sum + Number(item.applyCount || item.count || 0), 0)
    deptRanking.value = (deptRank || []).map(item => ({
      name: item.deptName,
      count: Number(item.visitCount || 0)
    }))
    drugRanking.value = (drugRank || []).slice(0, 5).map(item => ({
      name: item.medicineName,
      count: Number(item.consumeCount || 0)
    }))
    initChart(trend)
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const initChart = (trend) => {
  const chartDom = document.getElementById('overviewChart')
  if (!chartDom) return
  if (!chartInstance) chartInstance = echarts.init(chartDom)
  chartInstance.setOption({
    title: { text: '就诊趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: trend.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{
      name: '就诊人次',
      type: 'bar',
      data: trend.map(i => Number(i.count || 0)),
      itemStyle: { color: '#2385bb' }
    }]
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
.stat-value { font-size: 24px; font-weight: 600; color: #303133; }
.stat-title { font-size: 14px; color: #909399; margin-top: 5px; }
.chart-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; }
</style>
