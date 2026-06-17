<template>
  <div class="page-container">
    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>病历书写</span>
          <el-button type="primary" @click="handleBack">返回列表</el-button>
        </div>
      </template>

      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-divider content-position="left">患者基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="患者姓名">
              <el-input v-model="formData.patientName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学号">
              <el-input v-model="formData.studentNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="就诊日期">
              <el-input v-model="formData.visitDate" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">病情信息</el-divider>
        <el-form-item label="主诉" prop="chiefComplaint">
          <el-input v-model="formData.chiefComplaint" type="textarea" :rows="2" placeholder="患者自述主要症状及持续时间" />
        </el-form-item>
        <el-form-item label="现病史" prop="historyPresent">
          <el-input v-model="formData.historyPresent" type="textarea" :rows="3" placeholder="详细描述病情发展过程" />
        </el-form-item>
        <el-form-item label="既往史">
          <el-input v-model="formData.historyPast" type="textarea" :rows="2" placeholder="既往疾病史、过敏史等" />
        </el-form-item>
        <el-form-item label="体格检查" prop="physicalExam">
          <el-input v-model="formData.physicalExam" type="textarea" :rows="3" placeholder="体温、血压、心肺听诊等检查结果" />
        </el-form-item>

        <el-divider content-position="left">诊断与处方</el-divider>
        <el-form-item label="病种属性">
          <el-select
              v-model="formData.diseaseCode"
              placeholder="可选：从基础病种库选择"
              clearable
              filterable
              style="width: 100%"
              @change="handleDiseaseChange"
          >
            <el-option
                v-for="item in diseaseOptions"
                :key="item.code"
                :label="`${item.name}（${item.code}）`"
                :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="初步诊断" prop="diagnosis">
          <el-input v-model="formData.diagnosis" type="textarea" :rows="2" placeholder="填写诊断结果，选择病种后可自动带入" />
        </el-form-item>
        <el-form-item label="医嘱">
          <el-input v-model="formData.advice" type="textarea" :rows="2" placeholder="治疗建议、注意事项等" />
        </el-form-item>

        <el-form-item label="药品处方">
          <el-table :data="formData.medicines" border>
            <el-table-column label="药品名称" min-width="150">
              <template #default="{ row }">
                <el-select v-model="row.medicineId" placeholder="选择药品" filterable @change="(id) => handleDrugChange(row, id)">
                  <el-option
                      v-for="drug in drugOptions"
                      :key="drug.id"
                      :label="formatDrugLabel(drug)"
                      :value="drug.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="规格" width="120" prop="spec" />
            <el-table-column label="数量" width="100">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" :max="100" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="用法用量" min-width="180">
              <template #default="{ row }">
                <el-input v-model="row.usage" placeholder="如：口服，一日3次" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #default="{ $index }">
                <el-button type="danger" size="small" @click="handleDeleteMedicine($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" size="small" @click="handleAddMedicine" style="margin-top: 10px;">
            <el-icon><Plus /></el-icon>
            添加药品
          </el-button>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存病历</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref(null)
const submitLoading = ref(false)
const doctorId = ref(null)
const patientId = ref(null)
const appointmentId = ref(null)
const recordId = ref(null)
const deptId = ref(1)

const formData = reactive({
  patientName: '',
  studentNo: '',
  visitDate: new Date().toISOString().split('T')[0],
  chiefComplaint: '',
  historyPresent: '',
  historyPast: '',
  physicalExam: '',
  diseaseCode: '',
  diseaseName: '',
  diagnosis: '',
  advice: '',
  medicines: []
})

const rules = {
  chiefComplaint: [{ required: true, message: '请填写主诉', trigger: 'blur' }],
  historyPresent: [{ required: true, message: '请填写现病史', trigger: 'blur' }],
  physicalExam: [{ required: true, message: '请填写体格检查', trigger: 'blur' }],
  diagnosis: [{ required: true, message: '请填写诊断结果', trigger: 'blur' }]
}

const drugOptions = ref([])
const diseaseOptions = ref([])
const categoryNameMap = ref({})

const formatDrugLabel = (drug) => {
  const category = categoryNameMap.value[drug.category] || drug.category || ''
  return `${drug.medicineName}（${drug.spec}）${category ? ' [' + category + ']' : ''}`
}

const handleDiseaseChange = (code) => {
  const disease = diseaseOptions.value.find(item => item.code === code)
  formData.diseaseName = disease ? disease.name : ''
  if (disease && !formData.diagnosis) {
    formData.diagnosis = disease.name
  }
}

const handleDrugChange = (row, medicineId) => {
  const drug = drugOptions.value.find(d => d.id === medicineId)
  if (drug) {
    row.spec = drug.spec
    row.medicineName = drug.medicineName
    row.price = Number(drug.price) || 0
    row.description = drug.description || ''
    if (!row.usage && drug.description) {
      row.usage = drug.description
    }
  }
}

const handleBack = () => {
  router.push('/doctor/queue')
}

const handleAddMedicine = () => {
  formData.medicines.push({
    medicineId: '',
    medicineName: '',
    spec: '',
    quantity: 1,
    usage: '',
    price: 0
  })
}

const handleDeleteMedicine = (index) => {
  formData.medicines.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const payload = {
          appointmentId: appointmentId.value,
          userId: patientId.value,
          doctorId: doctorId.value,
          deptId: deptId.value,
          chiefComplaint: formData.chiefComplaint,
          presentIllness: formData.historyPresent,
          pastHistory: formData.historyPast,
          physicalExamination: formData.physicalExam,
          diagnosis: formData.diagnosis,
          diseaseCode: formData.diseaseCode || undefined,
          diseaseName: formData.diseaseName || undefined,
          advice: formData.advice
        }
        let record
        if (recordId.value) {
          record = await api.updateRecord({ id: recordId.value, ...payload })
        } else {
          record = await api.receivePatient(payload)
          recordId.value = record.id
        }

        const validMedicines = formData.medicines.filter(m => m.medicineId)
        if (validMedicines.length > 0) {
          await api.createPrescription({
            recordId: record.id,
            userId: patientId.value,
            doctorId: doctorId.value,
            advice: formData.advice,
            items: validMedicines.map(m => ({
              medicineId: m.medicineId,
              medicineName: m.medicineName,
              usage: m.usage || '口服',
              frequency: '一日3次',
              quantity: m.quantity,
              price: m.price
            }))
          })
        } else {
          await api.createRegistrationOrder({
            recordId: record.id,
            doctorId: doctorId.value
          })
        }

        ElMessage.success('病历保存成功')
        router.push('/doctor/queue')
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

onMounted(async () => {
  try {
    doctorId.value = await api.getDoctorIdByUserId(userStore.userInfo.id)
    drugOptions.value = await api.getMedicineList({}) || []
    const categories = await api.listDrugCategories() || []
    categoryNameMap.value = categories.reduce((map, item) => {
      map[item.code] = item.name
      return map
    }, {})
    diseaseOptions.value = await api.listDiseases() || []

    patientId.value = route.query.patientId ? Number(route.query.patientId) : null
    appointmentId.value = route.query.appointmentId ? Number(route.query.appointmentId) : null
    deptId.value = route.query.deptId ? Number(route.query.deptId) : 1

    if (patientId.value) {
      const user = await api.getCommonUserInfo(patientId.value)
      formData.patientName = user.realName
      formData.studentNo = user.username
      formData.visitDate = new Date().toISOString().split('T')[0]
    }
    if (appointmentId.value) {
      const existing = await api.getRecordByAppointment(appointmentId.value)
      if (existing) {
        recordId.value = existing.id
        formData.chiefComplaint = existing.chiefComplaint || ''
        formData.historyPresent = existing.presentIllness || ''
        formData.historyPast = existing.pastHistory || ''
        formData.physicalExam = existing.physicalExamination || ''
        formData.diseaseCode = existing.diseaseCode || ''
        formData.diseaseName = existing.diseaseName || ''
        formData.diagnosis = existing.diagnosis || ''
        formData.advice = existing.advice || ''
      }
    }
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
.form-card {
  margin-bottom: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
