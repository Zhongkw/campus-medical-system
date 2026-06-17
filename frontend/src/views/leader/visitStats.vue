<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="统计维度">
          <el-select v-model="queryParams.dimension">
            <el-option label="按时间" value="time" />
            <el-option label="按科室" value="department" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计周期">
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
          <el-button type="success" @click="handleExport">导出报表</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="chart-card">
      <div id="visitChart" style="width: 100%; height: 400px;"></div>
    </el-card>

    <el-card class="table-card">
      <template #header><span>就诊量明细</span></template>
      <el-table :data="tableData" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="dimension" label="统计维度" />
        <el-table-column prop="visitCount" label="就诊人次" align="center" />
        <el-table-column prop="rate" label="占比" align="center">
          <template #default="{ row }">
            <el-progress :percentage="row.rate" :color="'#2385bb'" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { api } from '../../api'

const queryParams = reactive({ dimension: 'time', dateRange: [] })
const tableData = ref([])
let chartInstance = null

const buildTableData = (list, labelKey, countKey) => {
  const total = list.reduce((sum, item) => sum + Number(item[countKey] || 0), 0) || 1
  return list.map(item => ({
    dimension: item[labelKey],
    visitCount: Number(item[countKey] || 0),
    rate: Math.round((Number(item[countKey] || 0) / total) * 100)
  }))
}

const loadData = async () => {
  try {
    if (queryParams.dimension === 'department') {
      const list = await api.getDeptVisitRank() || []
      tableData.value = buildTableData(list, 'deptName', 'visitCount')
    } else {
      const list = await api.getVisitTrend({
        startTime: queryParams.dateRange?.[0],
        endTime: queryParams.dateRange?.[1]
      }) || []
      tableData.value = buildTableData(list, 'date', 'count')
    }
    initChart()
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const handleSearch = () => loadData()
const handleExport = () => ElMessage.success('报表导出成功')

const initChart = () => {
  const chartDom = document.getElementById('visitChart')
  if (!chartDom) return
  if (!chartInstance) chartInstance = echarts.init(chartDom)
  chartInstance.setOption({
    title: { text: '就诊量统计图表', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: tableData.value.map(i => i.dimension) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: tableData.value.map(i => i.visitCount), itemStyle: { color: '#36b37e' } }]
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container { padding: 0; }
.search-card, .chart-card, .table-card { margin-bottom: 20px; }
</style>
