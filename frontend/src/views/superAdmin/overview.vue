//全院数据总览
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
              <div class="stat-value">523</div>
              <div class="stat-title">系统用户总数</div>
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
              <div class="stat-value">3,254</div>
              <div class="stat-title">本月就诊人次</div>
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
              <div class="stat-value">¥45,680</div>
              <div class="stat-title">本月收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon :size="30"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">256</div>
              <div class="stat-title">药品种类</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span>全院就诊趋势</span></template>
          <div id="visitTrendChart" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header><span>科室就诊分布</span></template>
          <div id="deptPieChart" style="width: 100%; height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>系统运行状态</span></template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统状态">
              <el-tag type="success">正常运行</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="服务器 CPU">45%</el-descriptions-item>
            <el-descriptions-item label="服务器内存">62%</el-descriptions-item>
            <el-descriptions-item label="数据库连接">正常</el-descriptions-item>
            <el-descriptions-item label="最近备份">2026-05-08 02:00:00</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>实时数据</span></template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="当前在线用户">156</el-descriptions-item>
            <el-descriptions-item label="今日挂号数">168</el-descriptions-item>
            <el-descriptions-item label="待接诊患者">12</el-descriptions-item>
            <el-descriptions-item label="待配药处方">5</el-descriptions-item>
            <el-descriptions-item label="待审批申请">8</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { User, TrendCharts, Money, Box } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const initCharts = () => {
  // 就诊趋势图
  const visitDom = document.getElementById('visitTrendChart')
  if (visitDom) {
    const visitChart = echarts.init(visitDom)
    visitChart.setOption({
      title: { text: '近 7 天就诊趋势', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'axis' },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['05-02', '05-03', '05-04', '05-05', '05-06', '05-07', '05-08'] },
      yAxis: { type: 'value' },
      series: [{
        name: '就诊人次',
        type: 'line',
        data: [125, 138, 120, 165, 132, 148, 156],
        smooth: true,
        itemStyle: { color: '#2385bb' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(35, 133, 187, 0.3)' },
              { offset: 1, color: 'rgba(35, 133, 187, 0.05)' }
            ]
          }
        }
      }]
    })
  }

  // 科室分布饼图
  const deptDom = document.getElementById('deptPieChart')
  if (deptDom) {
    const deptChart = echarts.init(deptDom)
    deptChart.setOption({
      title: { text: '科室就诊分布', left: 'center', textStyle: { fontSize: 14 } },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: [
          { value: 856, name: '内科' },
          { value: 642, name: '外科' },
          { value: 523, name: '口腔科' },
          { value: 412, name: '眼科' },
          { value: 321, name: '皮肤科' }
        ]
      }]
    })
  }
}

onMounted(() => {
  setTimeout(initCharts, 100)
})
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
</style>
