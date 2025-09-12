import request from '@/utils/request'

// 角色查询参数
export interface RoleQuery {
  pageNum?: number
  pageSize?: number
  roleName?: string
  roleKey?: string
  status?: string
  createTime?: string[]
}

// 角色信息
export interface RoleVO {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  status: string
  delFlag?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  flag?: boolean
  menuIds?: number[]
  deptIds?: number[]
  permissions?: string[]
  admin?: boolean
}

// 角色表单
export interface RoleForm {
  roleId?: number
  roleName: string
  roleKey: string
  roleSort: number
  status: string
  menuIds?: number[]
  deptIds?: number[]
  menuCheckStrictly?: boolean
  deptCheckStrictly?: boolean
  dataScope?: string
  remark?: string
}

// 数据权限
export interface DataScope {
  roleId: number
  roleName: string
  roleKey: string
  dataScope: string
  deptIds?: number[]
  deptCheckStrictly?: boolean
}

// 查询角色列表
export function listRole(query: RoleQuery) {
  return request({
    url: '/api/system/role/list',
    method: 'get',
    params: query
  })
}

// 查询角色详细
export function getRole(roleId: number) {
  return request({
    url: '/api/system/role/' + roleId,
    method: 'get'
  })
}

// 新增角色
export function addRole(data: RoleForm) {
  return request({
    url: '/api/system/role',
    method: 'post',
    data: data
  })
}

// 修改角色
export function updateRole(data: RoleForm) {
  return request({
    url: '/api/system/role',
    method: 'put',
    data: data
  })
}

// 角色数据权限
export function dataScope(data: DataScope) {
  return request({
    url: '/api/system/role/dataScope',
    method: 'put',
    data: data
  })
}

// 角色状态修改
export function changeRoleStatus(roleId: number, status: string) {
  const data = {
    roleId,
    status
  }
  return request({
    url: '/api/system/role/changeStatus',
    method: 'put',
    data: data
  })
}

// 删除角色
export function delRole(roleId: number | number[]) {
  return request({
    url: '/api/system/role/' + roleId,
    method: 'delete'
  })
}

// 查询角色选择框列表
export function optionselect() {
  return request({
    url: '/api/system/role/optionselect',
    method: 'get'
  })
}

// 查询已分配用户角色列表
export function allocatedUserList(query: any) {
  return request({
    url: '/api/system/role/authUser/allocatedList',
    method: 'get',
    params: query
  })
}

// 查询未分配用户角色列表
export function unallocatedUserList(query: any) {
  return request({
    url: '/api/system/role/authUser/unallocatedList',
    method: 'get',
    params: query
  })
}

// 取消用户授权角色
export function authUserCancel(data: any) {
  return request({
    url: '/api/system/role/authUser/cancel',
    method: 'put',
    params: data
  })
}

// 批量取消用户授权角色
export function authUserCancelAll(data: any) {
  return request({
    url: '/api/system/role/authUser/cancelAll',
    method: 'put',
    params: data
  })
}

// 授权用户选择
export function authUserSelectAll(data: any) {
  return request({
    url: '/api/system/role/authUser/selectAll',
    method: 'put',
    params: data
  })
}

// 根据角色ID查询部门树结构
export function deptTreeSelect(roleId: number) {
  return request({
    url: '/api/system/role/deptTree/' + roleId,
    method: 'get'
  })
}

// 导出角色
export function exportRole(query: RoleQuery) {
  return request({
    url: '/api/system/role/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}