import request from '@/utils/request'

// 获取验证码
export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get'
  })
}

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: data
  })
}

// 登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}