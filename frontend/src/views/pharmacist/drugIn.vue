<template>
  <div class="page-container">
    <div class="header-actions">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增入库
      </el-button>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>入库记录</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="inNo" label="入库单号" />
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="batchNo" label="分类编号" />
        <el-table-column prop="quantity" label="入库数量" width="100" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.unitPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="supplier" label="供应商" />
        <el-table-column prop="inDate" label="入库日期" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button
                v-if="row.status === '待审核'"
                type="success"
                size="small"
                @click="handleApprove(row)"
            >
              审核入库
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="药品名称" prop="drugId">
          <el-select v-model="formData.drugId" placeholder="选择药品" filterable style="width: 100%" @change="handleDrugChange">
            <el-option
                v-for="drug in drugOptions"
                :key="drug.id"
                :label="`${drug.name} (${drug.spec})`"
                :value="drug.id"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="分类编号" prop="batchNo">
              <el-select v-model="formData.batchNo" placeholder="请选择分类编号" style="width: 100%">
                <el-option
                    v-for="cat in categoryOptions"
                    :key="cat.code"
                    :label="`${cat.code} - ${cat.name}`"
                    :value="cat.code"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入库数量" prop="quantity">
              <el-input-number v-model="formData.quantity" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="单价" prop="unitPrice">
              <el-input-number v-model="formData.unitPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总金额">
              <el-input v-model="totalAmount" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="formData.supplier" placeholder="请输入供应商" />
        </el-form-item>
        <el-form-item label="有效期" prop="expireDate">
          <el-date-picker
              v-model="formData.expireDate"
              type="date"
              placeholder="选择有效期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="可选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交入库</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="viewDialogVisible" title="入库详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="入库单号">{{ currentRow.inNo }}</el-descriptions-item>
        <el-descriptions-item label="药品名称">{{ currentRow.drugName }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ currentRow.spec }}</el-descriptions-item>
        <el-descriptions-item label="分类编号">{{ currentRow.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="入库数量">{{ currentRow.quantity }}</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ currentRow.unitPrice }}</el-descriptions-item>
        <el-descriptions-item label="总金额">
          <span style="color: #f56c6c; font-weight: 600;">¥{{ currentRow.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="供应商">{{ currentRow.supplier }}</el-descriptions-item>
        <el-descriptions-item label="入库日期">{{ currentRow.inDate }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRow.status)">{{ currentRow.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentRow.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()
const route = useRoute()

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增入库')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentRow = ref({})
const formRef = ref(null)

const formData = reactive({
  drugId: '',
  batchNo: '',
  quantity: 1,
  unitPrice: 0,
  supplier: '',
  expireDate: '',
  remark: ''
})

const rules = {
  drugId: [{ required: true, message: '请选择药品', trigger: 'change' }],
  batchNo: [{ required: true, message: '请选择分类编号', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  expireDate: [{ required: true, message: '请选择有效期', trigger: 'change' }]
}

const drugOptions = ref([])
const categoryOptions = ref([])

const totalAmount = computed(() => {
  return (formData.quantity * formData.unitPrice).toFixed(2)
})

const tableData = ref([])

const formatInstock = (item) => ({
  id: item.id,
  inNo: item.instockNo,
  drugName: item.medicineName || item.remark || '药品入库',
  spec: item.spec || '-',
  batchNo: item.batchNo || '-',
  quantity: item.quantity ?? '-',
  unitPrice: item.price ?? '-',
  totalAmount: item.totalAmount,
  supplier: '-',
  inDate: item.createTime ? String(item.createTime).substring(0, 10) : '',
  status: '已入库',
  remark: item.remark || ''
})

const getStatusType = (status) => {
  const map = {
    '待审核': 'warning',
    '已入库': 'success'
  }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const list = await api.getInstockList({ operatorId: userStore.userInfo.id }) || []
    tableData.value = list.map(formatInstock)
    total.value = tableData.value.length
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleDrugChange = (drugId) => {
  const drug = drugOptions.value.find(item => item.id === drugId)
  if (drug && drug.category) {
    formData.batchNo = drug.category
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增入库'
  formData.drugId = ''
  formData.batchNo = ''
  formData.quantity = 1
  formData.unitPrice = 0
  formData.supplier = ''
  formData.expireDate = ''
  formData.remark = ''
  dialogVisible.value = true
}

const handleView = (row) => {
  currentRow.value = row
  viewDialogVisible.value = true
}

const handleApprove = async (row) => {
  try {
    await ElMessageBox.confirm(`确定审核通过入库单 ${row.inNo} 吗？`, '审核确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    row.status = '已入库'
    ElMessage.success('审核通过，入库成功')
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
        const drug = drugOptions.value.find(d => d.id === formData.drugId)
        await api.instockMedicine({
          operatorId: userStore.userInfo.id,
          remark: formData.remark || `${drug?.name}入库`,
          items: [{
            medicineId: formData.drugId,
            medicineName: drug?.name,
            spec: drug?.spec,
            quantity: formData.quantity,
            price: formData.unitPrice,
            batchNo: formData.batchNo,
            expireDate: formData.expireDate
          }]
        })
        ElMessage.success('入库成功')
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('提交失败')
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
    const [medicines, categories] = await Promise.all([
      api.getMedicineList({}),
      api.listDrugCategories()
    ])
    drugOptions.value = (medicines || []).map(m => ({
      id: m.id,
      name: m.medicineName,
      spec: m.spec,
      category: m.category
    }))
    categoryOptions.value = categories || []
    if (route.query.openAdd === '1' && route.query.medicineId) {
      const medicineId = Number(route.query.medicineId)
      formData.drugId = medicineId
      handleDrugChange(medicineId)
      dialogTitle.value = '快速入库'
      dialogVisible.value = true
    }
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
.header-actions {
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
