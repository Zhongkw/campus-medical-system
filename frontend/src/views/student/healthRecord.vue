<template>
  <div class="page-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人健康档案</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="学号">{{ userInfo.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo.gender }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ userInfo.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="所属学院">{{ userInfo.department }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ userInfo.classname }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="血型">{{ userInfo.bloodType }}</el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">{{ userInfo.allergy || '无' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2">{{ userInfo.history || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>就诊历史</span>
        </div>
      </template>
      <el-table :data="visitHistory" stripe>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="visitDate" label="就诊日期" />
        <el-table-column prop="department" label="科室" />
        <el-table-column prop="doctor" label="医生" />
        <el-table-column prop="diagnosis" label="诊断" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center">
          <template #default>
            <el-button type="primary" size="small">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '../../store/user'
import { api } from '../../api'

const userStore = useUserStore()

const userInfo = ref({
  name: '',
  studentNo: '',
  gender: '未知',
  birthDate: '-',
  department: '',
  classname: '',
  phone: '',
  bloodType: '-',
  allergy: '无',
  history: '无'
})

const visitHistory = ref([])

onMounted(async () => {
  try {
    const commonUser = await api.getCommonUserInfo(userStore.userInfo.id)
    userInfo.value = {
      name: commonUser.realName,
      studentNo: commonUser.username,
      gender: '未知',
      birthDate: '-',
      department: commonUser.department || '',
      classname: commonUser.className || '',
      phone: commonUser.phone || '',
      bloodType: '-',
      allergy: '无',
      history: '无'
    }
    const profile = await api.getHealthProfile(userStore.userInfo.id)
    if (profile) {
      userInfo.value.bloodType = profile.bloodType || '-'
      userInfo.value.allergy = profile.allergy || '无'
      userInfo.value.history = profile.pastHistory || '无'
    }
    const records = await api.getPatientRecordDetails(userStore.userInfo.id) || []
    visitHistory.value = records.map(r => ({
      id: r.id,
      visitDate: r.visitDate || '',
      department: r.department || '校医院',
      doctor: r.doctorName || r.prescribeDoctorName || '',
      diagnosis: r.diagnosis || ''
    }))
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.page-container {
  padding: 0;
}
.profile-card,
.table-card {
  margin-bottom: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: 600;
}
</style>
