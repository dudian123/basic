import { useAuthStore } from '@/stores/auth'

/**
 * 获取用户token
 * @returns {string} token
 */
export const getToken = () => {
  const authStore = useAuthStore()
  return authStore.token || localStorage.getItem('token') || ''
}

/**
 * 设置token
 * @param {string} token 
 */
export const setToken = (token) => {
  const authStore = useAuthStore()
  authStore.token = token
  localStorage.setItem('token', token)
}

/**
 * 移除token
 */
export const removeToken = () => {
  const authStore = useAuthStore()
  authStore.token = ''
  localStorage.removeItem('token')
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export const isLoggedIn = () => {
  const authStore = useAuthStore()
  return authStore.isLoggedIn
}