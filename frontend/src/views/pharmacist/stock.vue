<template>
  <div class="page-container">
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="药品名称">
          <el-input v-model="queryParams.drugName" placeholder="请输入药品名称" clearable />
        </el-form-item>
        <el-form-item label="药品分类">
          <el-select v-model="queryParams.category" placeholder="请选择分类" clearable>
            <el-option
                v-for="cat in categoryOptions"
                :key="cat.code"
                :label="cat.name"
                :value="cat.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-select v-model="queryParams.stockStatus" placeholder="请选择状态" clearable>
            <el-option label="正常" value="正常" />
            <el-option label="库存不足" value="库存不足" />
            <el-option label="缺货" value="缺货" />
          </el-select>
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
          <span>药品库存列表</span>
          <el-tag type="danger" style="margin-left: 20px;">库存预警：{{ warningCount }}</el-tag>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="drugName" label="药品名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="stock" label="当前库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStockType(row.stock, row.minStock)">{{ row.stock }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="100" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥{{ row.unitPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="supplier" label="供应商" />
        <el-table-column prop="expireDate" label="有效期" />
        <el-table-column prop="stockStatus" label="库存状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.stockStatus)">{{ row.stockStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
                v-if="row.stockStatus === '库存不足' || row.stockStatus === '缺货'"
                type="success"
                size="small"
                @click="handleStockIn(row)"
            >
              快速入库
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

    <el-dialog v-model="editDialogVisible" title="编辑库存信息" width="600px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="药品名称">
          <el-input v-model="editForm.drugName" disabled />
        </el-form-item>
        <el-form-item label="当前库存" prop="stock">
          <el-input-number v-model="editForm.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number v-model="editForm.minStock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期" prop="expireDate">
          <el-date-picker
              v-model="editForm.expireDate"
              type="date"
              placeholder="选择有效期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="editLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api } from '../../api'

const router = useRouter()

const loading = ref(false)
const editLoading = ref(false)
const editDialogVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const editFormRef = ref(null)

const queryParams = reactive({
  drugName: '',
  category: '',
  stockStatus: ''
})

const editForm = reactive({
  id: '',
  drugName: '',
  stock: 0,
  minStock: 10,
  expireDate: '',
  remark: ''
})

const editRules = {
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  minStock: [{ required: true, message: '请输入最低库存', trigger: 'blur' }],
  expireDate: []
}

const tableData = ref([])
const categoryOptions = ref([])

const formatMedicine = (item) => {
  let stockStatus = '正常'
  if (item.stock === 0) stockStatus = '缺货'
  else if (item.stock < item.minStock) stockStatus = '库存不足'
  return {
    id: item.id,
    drugName: item.medicineName,
    spec: item.spec,
    category: item.category,
    stock: item.stock,
    minStock: item.minStock,
    unitPrice: item.price,
    supplier: item.manufacturer || '-',
    expireDate: item.expireDate || '-',
    stockStatus,
    remark: ''
  }
}

const warningCount = computed(() => {
  return tableData.value.filter(item => item.stockStatus === '库存不足' || item.stockStatus === '缺货').length
})

const getStockType = (stock, minStock) => {
  if (stock === 0) return 'danger'
  if (stock < minStock) return 'warning'
  return 'success'
}

const getStatusType = (status) => {
  const map = {
    '正常': 'success',
    '库存不足': 'warning',
    '缺货': 'danger'
  }
  return map[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {}
    if (queryParams.category) {
      params.category = queryParams.category
    }
    const list = await api.getMedicineList(params) || []
    let filtered = list.map(formatMedicine).filter(item => {
      if (queryParams.drugName && !item.drugName.includes(queryParams.drugName)) return false
      if (queryParams.stockStatus && item.stockStatus !== queryParams.stockStatus) return false
      return true
    })
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

const handleReset = () => {
  queryParams.drugName = ''
  queryParams.category = ''
  queryParams.stockStatus = ''
  handleSearch()
}

const handleEdit = (row) => {
  Object.assign(editForm, {
    ...row,
    expireDate: row.expireDate && row.expireDate !== '-' ? row.expireDate : ''
  })
  editDialogVisible.value = true
}

const handleStockIn = (row) => {
  router.push({
    path: '/pharmacist/drugIn',
    query: {
      medicineId: String(row.id),
      openAdd: '1'
    }
  })
}

const handleEditSubmit = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      editLoading.value = true
      try {
        await api.updateMedicineInfo({
          medicineId: editForm.id,
          stock: editForm.stock,
          minStock: editForm.minStock,
          expireDate: editForm.expireDate || undefined
        })
        ElMessage.success('保存成功')
        editDialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('保存失败')
      } finally {
        editLoading.value = false
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
    categoryOptions.value = await api.listDrugCategories() || []
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
  display: flex;
  align-items: center;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
