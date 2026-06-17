<template>
  <div class="page-container">
    <el-card class="chart-card">
      <template #header><span>疾病谱分析统计</span></template>
      <el-row :gutter="20">
        <el-col :span="12">
          <div id="diseasePieChart" style="width: 100%; height: 400px;"></div>
        </el-col>
        <el-col :span="12">
          <div id="diseaseBarChart" style="width: 100%; height: 400px;"></div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <template #header><span>疾病排行 TOP10</span></template>
      <el-table :data="tableData" stripe>
        <el-table-column type="index" label="排名" width="60" align="center" />
        <el-table-column prop="name" label="疾病名称" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="count" label="发病人次" align="center" />
        <el-table-column prop="rate" label="占比" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.rate" :color="'#e6a23c'" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { api } from '../../api'

const tableData = ref([])
let pieChart = null
let barChart = null

const loadData = async () => {
  try {
    const list = await api.getDiseaseTop10({}) || []
    tableData.value = list.map(item => ({
      name: item.diseaseName,
      category: item.category || '未分类',
      count: Number(item.count || 0),
      rate: Number(item.percentage || 0)
    }))
    initCharts()
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const initCharts = () => {
  const pieDom = document.getElementById('diseasePieChart')
  if (pieDom) {
    if (!pieChart) pieChart = echarts.init(pieDom)
    pieChart.setOption({
      title: { text: '疾病分类占比', left: 'center' },
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: '60%',
        data: tableData.value.map(item => ({ value: item.count, name: item.name }))
      }]
    })
  }

  const barDom = document.getElementById('diseaseBarChart')
  if (barDom) {
    if (!barChart) barChart = echarts.init(barDom)
    barChart.setOption({
      title: { text: '疾病发病排行', left: 'center' },
      xAxis: { type: 'category', data: tableData.value.map(i => i.name) },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: tableData.value.map(i => i.count), itemStyle: { color: '#2385bb' } }]
    })
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 0; }
.chart-card, .table-card { margin-bottom: 20px; }
</style>
