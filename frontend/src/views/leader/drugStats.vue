<template>
  <div class="page-container">
    <el-card class="chart-card">
      <template #header><span>药品消耗排行统计</span></template>
      <div id="drugChart" style="width: 100%; height: 400px;"></div>
    </el-card>

    <el-card class="table-card">
      <template #header><span>药品消耗明细</span></template>
      <el-table :data="tableData" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="consumption" label="消耗量" align="center" />
        <el-table-column prop="amount" label="消耗金额" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.amount }}</span>
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
let chartInstance = null

const loadData = async () => {
  try {
    const list = await api.getDrugConsumeRank({}) || []
    tableData.value = list.map(item => ({
      name: item.medicineName,
      spec: '-',
      consumption: Number(item.consumeCount || 0),
      amount: Number(item.consumeAmount || 0)
    }))
    initChart()
  } catch (error) {
    ElMessage.error('加载数据失败')
  }
}

const initChart = () => {
  const chartDom = document.getElementById('drugChart')
  if (!chartDom) return
  if (!chartInstance) chartInstance = echarts.init(chartDom)
  chartInstance.setOption({
    title: { text: '药品消耗 TOP10', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: tableData.value.map(i => i.name).reverse() },
    series: [{
      type: 'bar',
      data: tableData.value.map(i => i.consumption).reverse(),
      itemStyle: { color: '#36b37e' }
    }]
  })
}

onMounted(() => loadData())
</script>

<style scoped>
.page-container { padding: 0; }
.chart-card, .table-card { margin-bottom: 20px; }
</style>
