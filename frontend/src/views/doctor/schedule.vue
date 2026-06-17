<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="排班月份">
          <el-date-picker
              v-model="queryParams.month"
              type="month"
              placeholder="选择月份"
              value-format="YYYY-MM"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增排班
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>排班管理</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="scheduleDate" label="排班日期" />
        <el-table-column prop="weekDay" label="星期" width="100" align="center" />
        <el-table-column prop="timeSlot" label="时段" width="150" align="center" />
        <el-table-column prop="totalNumber" label="总号源" width="100" align="center" />
        <el-table-column prop="usedNumber" label="已用号源" width="100" align="center" />
        <el-table-column prop="remainNumber" label="剩余号源" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.remainNumber > 0 ? 'success' : 'danger'">{{ row.remainNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
                v-if="row.status === '未开始'"
                type="danger"
                size="small"
                @click="handleDelete(row)"
            >
              删除
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="排班日期" prop="scheduleDate">
          <el-date-picker
              v-model="formData.scheduleDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="时段" prop="timeSlot">
          <el-select v-model="formData.timeSlot" placeholder="选择时段" style="width: 100%">
            <el-option label="上午08:30-11:30" value="上午08:30-11:30" />
            <el-option label="下午14:30-17:30" value="下午14:30-17:30" />
          </el-select>
        </el-form-item>
        <el-form-item label="总号源" prop="totalNumber">
          <el-input-number v-model="formData.totalNumber" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="可选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()
const doctorId = ref(null)
const deptId = ref(null)

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增排班')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const formRef = ref(null)

const queryParams = reactive({
  month: new Date().toISOString().slice(0, 7)
})

const formData = reactive({
  id: '',
  scheduleDate: '',
  timeSlot: '',
  totalNumber: 20,
  remark: ''
})

const rules = {
  scheduleDate: [{ required: true, message: '请选择排班日期', trigger: 'change' }],
  timeSlot: [{ required: true, message: '请选择时段', trigger: 'change' }],
  totalNumber: [{ required: true, message: '请输入总号源', trigger: 'blur' }]
}

const tableData = ref([])

const getStatus = (date) => {
  const today = new Date().toISOString().split('T')[0]
  if (date < today) return '已结束'
  if (date > today) return '未开始'
  return '进行中'
}

const formatSchedule = (item) => {
  const total = item.totalNumber || 0
  const remain = item.remainNumber || 0
  const usedNumber = Math.max(0, total - remain)
  return {
    id: item.id,
    scheduleDate: item.visitDate,
    weekDay: weekDayMap[new Date(item.visitDate).getDay()],
    timeSlot: item.visitTime,
    totalNumber: item.totalNumber,
    usedNumber,
    remainNumber: item.remainNumber,
    status: getStatus(item.visitDate),
    remark: ''
  }
}

const weekDayMap = {
  0: '星期日',
  1: '星期一',
  2: '星期二',
  3: '星期三',
  4: '星期四',
  5: '星期五',
  6: '星期六'
}

const getStatusType = (status) => {
  const map = {
    '未开始': 'info',
    '进行中': 'primary',
    '已结束': 'success'
  }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    if (!doctorId.value) {
      doctorId.value = await api.getDoctorIdByUserId(userStore.userInfo.id)
    }
    const list = await api.getScheduleDetailList({ doctorId: doctorId.value }) || []
    let filtered = list.map(formatSchedule)
    if (queryParams.month) {
      filtered = filtered.filter(item => item.scheduleDate && item.scheduleDate.startsWith(queryParams.month))
    }
    tableData.value = filtered
    total.value = filtered.length
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

const handleAdd = () => {
  dialogTitle.value = '新增排班'
  formData.id = ''
  formData.scheduleDate = ''
  formData.timeSlot = ''
  formData.totalNumber = 20
  formData.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑排班'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.scheduleDate} ${row.timeSlot} 的排班吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formData.id) {
          await api.updateSchedule({
            id: formData.id,
            scheduleDate: formData.scheduleDate,
            timeSlot: formData.timeSlot,
            maxNum: formData.totalNumber
          })
        } else {
          await api.createSchedule({
            doctorId: doctorId.value,
            deptId: deptId.value,
            scheduleDate: formData.scheduleDate,
            timeSlot: formData.timeSlot,
            maxNum: formData.totalNumber,
            remainNum: formData.totalNumber,
            status: 1
          })
        }
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadData()
}

onMounted(async () => {
  try {
    const profile = await api.getDoctorProfile(userStore.userInfo.id)
    doctorId.value = profile.doctorId
    deptId.value = profile.deptId
  } catch (error) {
    console.error(error)
  }
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
