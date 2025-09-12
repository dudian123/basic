<template>
  <div class="auth-role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户角色授权</span>
          <el-button type="text" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>
      
      <div class="user-info">
        <h3>用户信息</h3>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名称">{{ userInfo.username }}</el-descriptions-item>
          <el-descriptions-item label="用户昵称">{{ userInfo.nickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号码">{{ userInfo.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-divider />
      
      <div class="role-assignment">
        <h3>角色分配</h3>
        <el-form ref="formRef" :model="form" label-width="100px">
          <el-form-item label="已分配角色">
            <el-transfer
              v-model="form.roleIds"
              :data="roleOptions"
              :titles="['可选角色', '已选角色']"
              :button-texts="['移除', '添加']"
              :format="{
                noChecked: '${total}',
                hasChecked: '${checked}/${total}'
              }"
              filterable
              filter-placeholder="请输入角色名称"
            />
          </el-form-item>
        </el-form>
      </div>
      
      <div class="form-actions">
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
        <el-button @click="goBack">
          <el-icon><Close /></el-icon>
          取消
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Check,
  Close
} from '@element-plus/icons-vue'
import { getAuthRole, updateAuthRole } from '@/api/system/user'
import { optionselect } from '@/api/system/role'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const userInfo = ref({})
const roleOptions = ref([])
const form = reactive({
  userId: '',
  roleIds: []
})

// 获取用户信息和角色
const fetchUserInfo = async () => {
  try {
    loading.value = true
    const userId = route.params.userId
    console.log('获取到的userId参数:', userId)
    
    // 检查userId是否有效
    if (!userId || userId === ':userId' || isNaN(parseInt(userId))) {
      ElMessage.error('用户ID参数无效，请从用户管理页面正确进入')
      router.push('/system/user')
      return
    }
    
    const response = await getAuthRole(userId)
    if (response.code === 200) {
      userInfo.value = response.data.user
      form.userId = userId
      // 设置已选角色
      form.roleIds = response.data.roles.map(role => role.roleId)
      // 设置角色选项
      roleOptions.value = response.data.roles.concat(response.data.unallocatedRoles || []).map(role => ({
        key: role.roleId,
        label: role.roleName,
        disabled: false
      }))
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

// 获取角色列表
const fetchRoleList = async () => {
  try {
    const response = await optionselect()
    if (response.code === 200) {
      const allRoles = response.data.map(role => ({
        key: role.roleId,
        label: role.roleName,
        disabled: false
      }))
      // 合并到现有角色选项中
      roleOptions.value = allRoles
    }
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    if (!form.userId) {
      ElMessage.error('用户ID不能为空')
      return
    }
    
    loading.value = true
    const response = await updateAuthRole({
      userId: form.userId,
      roleIds: form.roleIds.join(',')
    })
    
    if (response.code === 200) {
      ElMessage.success('角色分配成功')
      goBack()
    } else {
      ElMessage.error(response.msg || '角色分配失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('角色分配失败')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 组件挂载时初始化
onMounted(() => {
  fetchUserInfo()
  fetchRoleList()
})
</script>

<style lang="css" scoped>
.auth-role-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  margin-bottom: 20px;
}

.user-info h3 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.role-assignment {
  margin-bottom: 20px;
}

.role-assignment h3 {
  margin-bottom: 16px;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.form-actions {
  text-align: center;
  padding-top: 20px;
}

.form-actions .el-button {
  margin: 0 10px;
}

:deep(.el-transfer) {
  text-align: center;
}

:deep(.el-transfer-panel) {
  width: 250px;
}
</style>