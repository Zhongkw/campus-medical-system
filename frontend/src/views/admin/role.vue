<template>
  <div class="page-container">
    <div class="header-actions">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增角色
      </el-button>
    </div>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
        </div>
      </template>
      <el-table :data="tableData" stripe v-loading="loading" row-key="id">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="角色描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleConfigMenu(row)">配置菜单</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" placeholder="请输入角色编码" :disabled="!!formData.id" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="menuDialogVisible" title="配置菜单权限" width="800px" @opened="onMenuDialogOpened">
      <el-tree
          ref="treeRef"
          :data="menuTreeData"
          show-checkbox
          node-key="id"
          :default-expanded-keys="defaultExpandedKeys"
          :props="{ label: 'name', children: 'children' }"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit" :loading="menuLoading">保存配置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '../../api'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const submitLoading = ref(false)
const menuLoading = ref(false)
const dialogVisible = ref(false)
const menuDialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref(null)
const treeRef = ref(null)
const currentRoleId = ref(null)

const formData = reactive({
  id: '',
  roleName: '',
  roleCode: '',
  description: ''
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const tableData = ref([])
const menuTreeData = ref([])
const defaultExpandedKeys = ref([100, 200, 300, 400, 500, 600, 700])

const formatRole = (item) => ({
  id: item.id,
  roleName: item.roleName,
  roleCode: item.roleCode,
  description: item.description || '',
  status: item.status === 1 ? '启用' : '禁用'
})

const loadMenuTree = async () => {
  try {
    menuTreeData.value = await api.getMenuTree() || []
  } catch (error) {
    console.error(error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const list = await api.getRoleList() || []
    tableData.value = list.map(formatRole)
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(formData, { id: '', roleName: '', roleCode: '', description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, {
    id: row.id,
    roleName: row.roleName,
    roleCode: row.roleCode,
    description: row.description
  })
  dialogVisible.value = true
}

const handleConfigMenu = (row) => {
  currentRoleId.value = row.id
  menuDialogVisible.value = true
}

const onMenuDialogOpened = async () => {
  if (!currentRoleId.value || !treeRef.value) return
  try {
    const menuIds = await api.getRoleMenuIds(currentRoleId.value) || []
    treeRef.value.setCheckedKeys(menuIds)
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色 ${row.roleName} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    // 用户取消或删除失败
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
        roleName: formData.roleName,
        roleCode: formData.roleCode,
        description: formData.description
      }
      if (formData.id) {
        await api.editRole(payload)
      } else {
        await api.addRole(payload)
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

const handleMenuSubmit = async () => {
  if (!treeRef.value || !currentRoleId.value) return
  menuLoading.value = true
  try {
    const checkedKeys = treeRef.value.getCheckedKeys()
    const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
    await api.assignRoleMenus({
      roleId: currentRoleId.value,
      menuIds: [...checkedKeys, ...halfCheckedKeys]
    })
    ElMessage.success('菜单权限配置成功')
    menuDialogVisible.value = false
  } catch (error) {
    ElMessage.error('配置失败')
  } finally {
    menuLoading.value = false
  }
}

onMounted(() => {
  loadMenuTree()
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
</style>
