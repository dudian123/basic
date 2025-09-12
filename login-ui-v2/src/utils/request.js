import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '', // 统一使用各API文件内的完整路径（如以 /api 开头），避免与代理规则叠加
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const authStore = useAuthStore()
    
    // 如果有token，添加到请求头
    if (authStore.token) {
      config.headers['Authorization'] = `Bearer ${authStore.token}`
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果是文件下载等特殊情况，直接返回
    if (response.config.responseType === 'blob') {
      return response
    }
    
    // 正常响应处理
    if (res.code === 200) {
      return res
    }
    
    // 处理业务错误
    if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      const authStore = useAuthStore()
      authStore.clearUserData()
      router.push('/login')
      return Promise.reject(new Error(res.msg || '登录已过期'))
    }
    
    // 其他错误
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  error => {
    console.error('响应错误:', error)
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service