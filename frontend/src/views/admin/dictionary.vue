<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="科室管理" name="department">
        <div class="tab-content">
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增科室
            </el-button>
          </div>
          <el-table :data="departmentData" stripe v-loading="loading" row-key="id">
            <el-table-column type="expand">
              <template #default="{ row }">
                <div class="expand-panel">
                  <div class="expand-header">
                    <span>{{ row.name }}（{{ row.code }}）下的医生</span>
                  </div>
                  <el-table :data="doctorMap[row.id] || []" border size="small" v-loading="doctorLoading[row.id]">
                    <el-table-column type="index" label="序号" width="60" align="center" />
                    <el-table-column prop="doctorName" label="医生姓名" />
                    <el-table-column prop="title" label="职称" />
                    <el-table-column prop="specialty" label="专长" show-overflow-tooltip />
                    <el-table-column prop="phone" label="联系电话" />
                    <el-table-column prop="status" label="状态" width="80" align="center">
                      <template #default="{ row: doctor }">
                        <el-tag :type="doctor.status === '启用' ? 'success' : 'danger'">{{ doctor.status }}</el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-table-column>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="code" label="科室编码" />
            <el-table-column prop="name" label="科室名称" />
            <el-table-column label="医生数量" width="100" align="center">
              <template #default="{ row }">
                {{ (doctorMap[row.id] || []).length }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="科室描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === '启用' ? 'success' : 'danger'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="药品分类" name="drugCategory">
        <div class="tab-content">
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增分类
            </el-button>
          </div>
          <el-table :data="drugCategoryData" stripe v-loading="loading" row-key="code">
            <el-table-column type="expand">
              <template #default="{ row }">
                <div class="expand-panel">
                  <div class="expand-header">
                    <span>{{ row.name }}（{{ row.code }}）下的药品</span>
                    <el-button type="primary" size="small" @click="handleAddMedicine(row)">
                      <el-icon><Plus /></el-icon>
                      新增药物
                    </el-button>
                  </div>
                  <el-table :data="medicineMap[row.code] || []" border size="small" v-loading="medicineLoading[row.code]">
                    <el-table-column type="index" label="序号" width="60" align="center" />
                    <el-table-column prop="medicineName" label="药品名称" />
                    <el-table-column prop="spec" label="药品规格" />
                    <el-table-column prop="description" label="药品作用说明" show-overflow-tooltip />
                    <el-table-column prop="unit" label="单位" width="80" align="center" />
                    <el-table-column prop="stock" label="库存" width="80" align="center" />
                    <el-table-column label="操作" width="160" align="center">
                      <template #default="{ row: med }">
                        <el-button type="primary" size="small" @click="handleEditMedicine(row, med)">编辑</el-button>
                        <el-button type="danger" size="small" @click="handleDeleteMedicine(med, row.code)">删除</el-button>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </template>
            </el-table-column>
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="code" label="分类编码" />
            <el-table-column prop="name" label="分类名称" />
            <el-table-column label="药品数量" width="100" align="center">
              <template #default="{ row }">
                {{ (medicineMap[row.code] || []).length }}
              </template>
            </el-table-column>
            <el-table-column prop="description" label="分类描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === '启用' ? 'success' : 'danger'">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <el-tab-pane label="病种管理" name="disease">
        <div class="tab-content">
          <div class="header-actions">
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增病种
            </el-button>
          </div>
          <el-alert
              title="病种数据用于：医生病历书写时可选「病种属性」；校领导疾病谱统计将优先按病种属性汇总。"
              type="info"
              :closable="false"
              show-icon
              style="margin-bottom: 16px;"
          />
          <el-table :data="diseaseData" stripe v-loading="loading">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="code" label="病种编码" />
            <el-table-column prop="name" label="病种名称" />
            <el-table-column prop="category" label="所属分类" />
            <el-table-column prop="description" label="病种描述" show-overflow-tooltip />
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入编码" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category" v-if="activeTab === 'disease'">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="内科疾病" value="内科疾病" />
            <el-option label="外科疾病" value="外科疾病" />
            <el-option label="口腔疾病" value="口腔疾病" />
            <el-option label="眼科疾病" value="眼科疾病" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="medicineDialogVisible" :title="medicineDialogTitle" width="600px">
      <el-form :model="medicineForm" :rules="medicineRules" ref="medicineFormRef" label-width="110px">
        <el-form-item label="所属分类">
          <el-input :model-value="`${currentCategory?.name}（${currentCategory?.code}）`" disabled />
        </el-form-item>
        <el-form-item label="药品名称" prop="medicineName">
          <el-input v-model="medicineForm.medicineName" placeholder="请输入药品名称" />
        </el-form-item>
        <el-form-item label="药品规格" prop="spec">
          <el-input v-model="medicineForm.spec" placeholder="如：0.5g*24粒" />
        </el-form-item>
        <el-form-item label="作用说明" prop="description">
          <el-input v-model="medicineForm.description" type="textarea" :rows="3" placeholder="请输入药品作用说明" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="medicineForm.unit" placeholder="如：盒" />
        </el-form-item>
        <el-form-item label="参考单价">
          <el-input-number v-model="medicineForm.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="medicineDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMedicineSubmit" :loading="medicineSubmitLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { api } from '../../api'

const loading = ref(false)
const submitLoading = ref(false)
const medicineSubmitLoading = ref(false)
const dialogVisible = ref(false)
const medicineDialogVisible = ref(false)
const dialogTitle = ref('新增')
const medicineDialogTitle = ref('新增药物')
const activeTab = ref('department')
const formRef = ref(null)
const medicineFormRef = ref(null)
const currentCategory = ref(null)
const medicineMap = ref({})
const medicineLoading = ref({})
const doctorMap = ref({})
const doctorLoading = ref({})

const formData = reactive({
  id: '',
  code: '',
  name: '',
  category: '',
  description: ''
})

const medicineForm = reactive({
  id: '',
  categoryCode: '',
  medicineName: '',
  spec: '',
  description: '',
  unit: '盒',
  price: 0
})

const rules = {
  code: [{ required: true, message: '请输入编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const medicineRules = {
  medicineName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  spec: [{ required: true, message: '请输入药品规格', trigger: 'blur' }],
  description: [{ required: true, message: '请输入药品作用说明', trigger: 'blur' }]
}

const departmentData = ref([])
const drugCategoryData = ref([])
const diseaseData = ref([])

const loadDoctorsForDepartments = async (departments) => {
  const map = { ...doctorMap.value }
  const loadingState = { ...doctorLoading.value }
  for (const dept of departments) {
    loadingState[dept.id] = true
    try {
      map[dept.id] = await api.listDoctorsByDepartment(dept.id) || []
    } catch (error) {
      map[dept.id] = []
    } finally {
      loadingState[dept.id] = false
    }
  }
  doctorMap.value = map
  doctorLoading.value = loadingState
}

const loadMedicinesForCategories = async (categories) => {
  const map = { ...medicineMap.value }
  const loadingState = { ...medicineLoading.value }
  for (const cat of categories) {
    loadingState[cat.code] = true
    try {
      map[cat.code] = await api.listMedicinesByCategory(cat.code) || []
    } catch (error) {
      map[cat.code] = []
    } finally {
      loadingState[cat.code] = false
    }
  }
  medicineMap.value = map
  medicineLoading.value = loadingState
}

const loadData = async () => {
  loading.value = true
  try {
    switch (activeTab.value) {
      case 'department': {
        const departments = await api.listDepartments() || []
        departmentData.value = departments
        await loadDoctorsForDepartments(departments)
        break
      }
      case 'drugCategory': {
        const categories = await api.listDrugCategories() || []
        drugCategoryData.value = categories
        await loadMedicinesForCategories(categories)
        break
      }
      case 'disease':
        diseaseData.value = await api.listDiseases() || []
        break
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const getDialogTitle = () => {
  const map = { department: '科室', drugCategory: '药品分类', disease: '病种' }
  return formData.id ? `编辑${map[activeTab.value]}` : `新增${map[activeTab.value]}`
}

const handleAdd = () => {
  Object.assign(formData, { id: '', code: '', name: '', category: '', description: '' })
  dialogTitle.value = getDialogTitle()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = getDialogTitle()
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleAddMedicine = (category) => {
  currentCategory.value = category
  Object.assign(medicineForm, {
    id: '',
    categoryCode: category.code,
    medicineName: '',
    spec: '',
    description: '',
    unit: '盒',
    price: 0
  })
  medicineDialogTitle.value = `新增药物 - ${category.name}`
  medicineDialogVisible.value = true
}

const handleEditMedicine = (category, medicine) => {
  currentCategory.value = category
  Object.assign(medicineForm, {
    id: medicine.id,
    categoryCode: category.code,
    medicineName: medicine.medicineName,
    spec: medicine.spec,
    description: medicine.description,
    unit: medicine.unit || '盒',
    price: Number(medicine.price) || 0
  })
  medicineDialogTitle.value = `编辑药物 - ${medicine.medicineName}`
  medicineDialogVisible.value = true
}

const handleMedicineSubmit = async () => {
  if (!medicineFormRef.value) return
  const valid = await medicineFormRef.value.validate()
  if (!valid) return
  medicineSubmitLoading.value = true
  try {
    await api.saveMedicine({ ...medicineForm })
    ElMessage.success('保存成功')
    medicineDialogVisible.value = false
    if (currentCategory.value) {
      medicineLoading.value[currentCategory.value.code] = true
      medicineMap.value[currentCategory.value.code] =
          await api.listMedicinesByCategory(currentCategory.value.code) || []
      medicineLoading.value[currentCategory.value.code] = false
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    medicineSubmitLoading.value = false
  }
}

const handleDeleteMedicine = async (medicine, categoryCode) => {
  try {
    await ElMessageBox.confirm(`确定要删除药品 ${medicine.medicineName} 吗？`, '删除确认', { type: 'warning' })
    await api.deleteMedicine(medicine.id)
    ElMessage.success('删除成功')
    medicineMap.value[categoryCode] = await api.listMedicinesByCategory(categoryCode) || []
  } catch (error) {
    // 用户取消
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除 ${row.name} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    if (activeTab.value === 'department') {
      await api.deleteDepartment(row.id)
    } else if (activeTab.value === 'drugCategory') {
      await api.deleteDrugCategory(row.id)
    } else {
      await api.deleteDisease(row.id)
    }
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (valid) {
    submitLoading.value = true
    try {
      const payload = {
        id: formData.id || undefined,
        code: formData.code,
        name: formData.name,
        description: formData.description,
        category: formData.category || undefined
      }
      if (activeTab.value === 'department') {
        await api.saveDepartment(payload)
      } else if (activeTab.value === 'drugCategory') {
        await api.saveDrugCategory(payload)
      } else {
        await api.saveDisease(payload)
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
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
.tab-content {
  padding: 20px 0;
}
.header-actions {
  margin-bottom: 20px;
}
.expand-panel {
  padding: 12px 24px 16px;
  background: #fafafa;
}
.expand-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
}
</style>
