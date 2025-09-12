<template>
  <div class="p-4">
    <el-card>
      <template #header>
        <h3>Token 测试页面</h3>
      </template>
      
      <div class="mb-4">
        <h4>当前状态:</h4>
        <p><strong>Token:</strong> {{ authStore.token || '无' }}</p>
        <p><strong>用户信息:</strong> {{ authStore.userInfo ? JSON.stringify(authStore.userInfo) : '无' }}</p>
        <p><strong>权限:</strong> {{ authStore.permissions.length > 0 ? authStore.permissions.join(', ') : '无' }}</p>
        <p><strong>角色:</strong> {{ authStore.roles.length > 0 ? authStore.roles.join(', ') : '无' }}</p>
        <p><strong>登录状态:</strong> {{ authStore.isLoggedIn ? '已登录' : '未登录' }}</p>
      </div>
      
      <div class="mb-4">
        <h4>LocalStorage 内容:</h4>
        <p><strong>token:</strong> {{ localStorage.getItem('token') || '无' }}</p>
        <p><strong>userInfo:</strong> {{ localStorage.getItem('userInfo') || '无' }}</p>
        <p><strong>permissions:</strong> {{ localStorage.getItem('permissions') || '无' }}</p>
        <p><strong>roles:</strong> {{ localStorage.getItem('roles') || '无' }}</p>
      </div>
      
      <div class="mb-4">
        <h4>API 测试:</h4>
        <el-button type="primary" @click="testPostAPI" :loading="loading">测试岗位API</el-button>
        <el-button type="success" @click="testRoleAPI" :loading="loading">测试角色API</el-button>
        <el-button type="warning" @click="clearStorage">清除存储</el-button>
      </div>
      
      <div v-if="apiResult">
        <h4>API 响应:</h4>
        <pre>{{ JSON.stringify(apiResult, null, 2) }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { listPost } from '@/api/system/post'
import { listRole } from '@/api/system/role'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const loading = ref(false)
const apiResult = ref(null)

const testPostAPI = async () => {
  loading.value = true
  try {
    console.log('测试岗位API，当前token:', authStore.token)
    const response = await listPost({ pageNum: 1, pageSize: 10 })
    apiResult.value = response
    ElMessage.success('岗位API调用成功')
  } catch (error) {
    console.error('岗位API调用失败:', error)
    apiResult.value = { error: error.message, response: error.response?.data }
    ElMessage.error('岗位API调用失败')
  } finally {
    loading.value = false
  }
}

const testRoleAPI = async () => {
  loading.value = true
  try {
    console.log('测试角色API，当前token:', authStore.token)
    const response = await listRole({ pageNum: 1, pageSize: 10 })
    apiResult.value = response
    ElMessage.success('角色API调用成功')
  } catch (error) {
    console.error('角色API调用失败:', error)
    apiResult.value = { error: error.message, response: error.response?.data }
    ElMessage.error('角色API调用失败')
  } finally {
    loading.value = false
  }
}

const clearStorage = () => {
  localStorage.clear()
  location.reload()
}

onMounted(() => {
  console.log('TokenTest页面加载，当前auth状态:', {
    token: authStore.token,
    userInfo: authStore.userInfo,
    permissions: authStore.permissions,
    roles: authStore.roles,
    isLoggedIn: authStore.isLoggedIn
  })
})
</script>

<style scoped>
pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style>