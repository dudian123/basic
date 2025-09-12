import request from '@/utils/request'

// 用户查询参数
export interface UserQuery {
  pageNum?: number
  pageSize?: number
  userName?: string
  nickName?: string
  phonenumber?: string
  status?: string
  deptId?: number
  roleId?: number
  userIds?: string
}

// 用户信息
export interface UserVO {
  userId: number
  tenantId?: string
  deptId?: number
  userName: string
  nickName: string
  userType?: string
  email?: string
  phonenumber?: string
  sex?: string
  avatar?: string
  status: string
  delFlag?: string
  loginIp?: string
  loginDate?: string
  remark?: string
  deptName?: string
  roles?: any[]
  roleIds?: number[]
  postIds?: number[]
  roleId?: number
  admin?: boolean
  createTime?: string
  updateTime?: string
}

// 用户表单
export interface UserForm {
  userId?: number
  deptId?: number
  userName: string
  nickName?: string
  password?: string
  phonenumber?: string
  email?: string
  sex?: string
  status: string
  remark?: string
  postIds: number[]
  roleIds: number[]
}

// 用户详细信息
export interface UserInfoVO {
  user: UserVO
  roles: any[]
  roleIds: number[]
  posts: any[]
  postIds: number[]
  roleGroup?: string
  postGroup?: string
}

// 部门树
export interface DeptTreeVO {
  id: number
  label: string
  children?: DeptTreeVO[]
}

/**
 * 查询用户列表
 */
export function listUser(query: UserQuery) {
  return request({
    url: '/api/system/user/list',
    method: 'get',
    params: query
  })
}

/**
 * 通过用户ids查询用户
 */
export function optionSelect(userIds: (number | string)[]) {
  return request({
    url: '/api/system/user/optionselect?userIds=' + userIds,
    method: 'get'
  })
}

/**
 * 获取用户详情
 */
export function getUser(userId?: string | number) {
  return request({
    url: '/api/system/user/' + (userId || ''),
    method: 'get'
  })
}

/**
 * 新增用户
 */
export function addUser(data: UserForm) {
  return request({
    url: '/api/system/user',
    method: 'post',
    data: data
  })
}

/**
 * 修改用户
 */
export function updateUser(data: UserForm) {
  return request({
    url: '/api/system/user',
    method: 'put',
    data: data
  })
}

/**
 * 删除用户
 */
export function delUser(userId: Array<string | number> | string | number) {
  return request({
    url: '/api/system/user/' + userId,
    method: 'delete'
  })
}

/**
 * 用户密码重置
 */
export function resetUserPwd(userId: string | number, password: string) {
  const data = {
    userId,
    password
  }
  return request({
    url: '/api/system/user/resetPwd',
    method: 'put',
    headers: {
      isEncrypt: true,
      repeatSubmit: false
    },
    data: data
  })
}

/**
 * 用户状态修改
 */
export function changeUserStatus(userId: number | string, status: string) {
  const data = {
    userId,
    status
  }
  return request({
    url: '/api/system/user/changeStatus',
    method: 'put',
    data: data
  })
}

/**
 * 获取用户个人信息
 */
export function getUserProfile() {
  return request({
    url: '/api/system/user/profile',
    method: 'get'
  })
}

/**
 * 修改用户个人信息
 */
export function updateUserProfile(data: UserForm) {
  return request({
    url: '/api/system/user/profile',
    method: 'put',
    data: data
  })
}

/**
 * 用户密码重置
 */
export function updateUserPwd(oldPassword: string, newPassword: string) {
  const data = {
    oldPassword,
    newPassword
  }
  return request({
    url: '/api/system/user/profile/updatePwd',
    method: 'put',
    headers: {
      isEncrypt: true,
      repeatSubmit: false
    },
    data: data
  })
}

/**
 * 用户头像上传
 */
export function uploadAvatar(data: FormData) {
  return request({
    url: '/api/system/user/profile/avatar',
    method: 'post',
    data: data
  })
}

/**
 * 查询授权角色
 */
export function getAuthRole(userId: string | number) {
  return request({
    url: '/api/system/user/authRole/' + userId,
    method: 'get'
  })
}

/**
 * 保存授权角色
 */
export function updateAuthRole(data: { userId: string; roleIds: string }) {
  return request({
    url: '/api/system/user/authRole',
    method: 'put',
    params: data
  })
}

/**
 * 根据部门ID查询用户
 */
export function listUserByDeptId(deptId: string | number) {
  return request({
    url: '/api/system/user/list/dept/' + deptId,
    method: 'get'
  })
}

/**
 * 查询部门下拉树结构
 */
export function deptTreeSelect() {
  return request({
    url: '/api/system/dept/treeselect',
    method: 'get'
  })
}

/**
 * 导出用户
 */
export function exportUser(query: UserQuery) {
  return request({
    url: '/api/system/user/export',
    method: 'post',
    data: query
  })
}

/**
 * 下载用户导入模板
 */
export function importTemplate() {
  return request({
    url: '/api/system/user/importTemplate',
    method: 'post'
  })
}

export default {
  listUser,
  getUser,
  optionSelect,
  addUser,
  updateUser,
  delUser,
  resetUserPwd,
  changeUserStatus,
  getUserProfile,
  updateUserProfile,
  updateUserPwd,
  uploadAvatar,
  getAuthRole,
  updateAuthRole,
  deptTreeSelect,
  listUserByDeptId,
  exportUser,
  importTemplate
}