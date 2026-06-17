<template>
  <div class="page-container">
    <el-card class="backup-card">
      <template #header>
        <div class="card-header">
          <span>数据备份管理</span>
          <el-button type="primary" @click="handleBackup">
            <el-icon><Download /></el-icon>
            立即备份
          </el-button>
        </div>
      </template>
      <el-table :data="backupList" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="backupNo" label="备份编号" />
        <el-table-column prop="backupTime" label="备份时间" />
        <el-table-column prop="backupType" label="备份类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.backupType === '自动' ? 'success' : 'primary'">{{ row.backupType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dataSize" label="数据大小" width="120" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '成功' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleDownload(row)">下载</el-button>
            <el-button type="warning" size="small" @click="handleRestore(row)">恢复</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card class="restore-card">
      <template #header>
        <div class="card-header">
          <span>数据恢复记录</span>
        </div>
      </template>
      <el-table :data="restoreList" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="restoreTime" label="恢复时间" />
        <el-table-column prop="backupSource" label="恢复来源" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '成功' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operator" label="操作人" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Download } from '@element-plus/icons-vue'

const loading = ref(false)

const backupList = ref([
  { id: 1, backupNo: 'BK20260508001', backupTime: '2026-05-08 02:00:00', backupType: '自动', dataSize: '256MB', status: '成功', operator: '系统' },
  { id: 2, backupNo: 'BK20260507001', backupTime: '2026-05-07 02:00:00', backupType: '自动', dataSize: '253MB', status: '成功', operator: '系统' },
  { id: 3, backupNo: 'BK20260506001', backupTime: '2026-05-06 15:30:00', backupType: '手动', dataSize: '251MB', status: '成功', operator: '管理员' }
])

const restoreList = ref([
  { id: 1, restoreTime: '2026-05-06 16:00:00', backupSource: 'BK20260505001', status: '成功', operator: '管理员', remark: '系统异常后恢复' }
])

const handleBackup = async () => {
  try {
    await ElMessageBox.confirm('确定要立即备份数据吗？', '备份确认', { type: 'info' })
    loading.value = true
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('数据备份成功')
    loading.value = false
  } catch (error) {
    // 用户取消操作
  }
}

const handleDownload = (row) => {
  ElMessage.success(`开始下载备份文件：${row.backupNo}`)
}

const handleRestore = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要恢复到备份 ${row.backupNo} 吗？此操作将覆盖当前数据！`, '恢复确认', { type: 'warning' })
    ElMessage.success('数据恢复成功，系统将在 5 秒后重启')
  } catch (error) {
    // 用户取消操作
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除备份 ${row.backupNo} 吗？`, '删除确认', { type: 'warning' })
    ElMessage.success('删除成功')
  } catch (error) {
    // 用户取消操作
  }
}

onMounted(() => {
  loading.value = true
  setTimeout(() => { loading.value = false }, 500)
})
</script>

<style scoped>
.page-container { padding: 0; }
.backup-card, .restore-card { margin-bottom: 20px; }
.card-header { font-size: 16px; font-weight: 600; display: flex; justify-content: space-between; align-items: center; }
</style>
