import request from '@/utils/request'

// 获取菜单列表
export function getMenus() {
  return request({
    url: '/system/menu/getRouters',
    method: 'get'
  })
}

// 获取路由菜单
export function getRouters() {
  return request({
    url: '/getRouters',
    method: 'get'
  })
}