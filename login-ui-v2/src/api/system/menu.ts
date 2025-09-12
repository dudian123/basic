import request from '@/utils/request'

// 菜单查询参数
export interface MenuQuery {
  menuName?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

// 菜单信息
export interface MenuVO {
  menuId: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component?: string
  query?: string
  isFrame: string
  isCache: string
  menuType: string
  visible: string
  status: string
  perms?: string
  icon?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  children?: MenuVO[]
}

// 菜单表单
export interface MenuForm {
  menuId?: number
  parentId: number
  menuName: string
  orderNum: number
  path: string
  component?: string
  query?: string
  isFrame: string
  isCache: string
  menuType: string
  visible: string
  status: string
  perms?: string
  icon?: string
  remark?: string
}

// 路由信息
export interface RouterVO {
  name?: string
  path: string
  hidden?: boolean
  redirect?: string
  component?: string
  query?: string
  alwaysShow?: boolean
  meta?: {
    title: string
    icon?: string
    noCache?: boolean
    link?: string
  }
  children?: RouterVO[]
}

// 查询菜单列表
export function listMenu(query: MenuQuery) {
  return request({
    url: '/api/system/menu/list',
    method: 'get',
    params: query
  })
}

// 查询菜单详细
export function getMenu(menuId: number) {
  return request({
    url: '/api/system/menu/' + menuId,
    method: 'get'
  })
}

// 查询菜单下拉树结构
export function treeselect() {
  return request({
    url: '/api/system/menu/treeselect',
    method: 'get'
  })
}

// 根据角色ID查询菜单下拉树结构
export function roleMenuTreeselect(roleId: number) {
  return request({
    url: '/api/system/menu/roleMenuTreeselect/' + roleId,
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data: MenuForm) {
  return request({
    url: '/api/system/menu',
    method: 'post',
    data: data
  })
}

// 修改菜单
export function updateMenu(data: MenuForm) {
  return request({
    url: '/api/system/menu',
    method: 'put',
    data: data
  })
}

// 删除菜单
export function delMenu(menuId: number) {
  return request({
    url: '/api/system/menu/' + menuId,
    method: 'delete'
  })
}

// 获取路由信息
export function getRouters() {
  return request({
    url: '/api/system/menu/getRouters',
    method: 'get'
  })
}