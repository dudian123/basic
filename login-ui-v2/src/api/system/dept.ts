import request from '@/utils/request'

// 部门查询参数
export interface DeptQuery {
  deptName?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

// 部门信息
export interface DeptVO {
  deptId: number
  parentId: number
  ancestors?: string
  deptName: string
  orderNum: number
  leader?: string
  phone?: string
  email?: string
  status: string
  delFlag?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  children?: DeptVO[]
}

// 部门表单
export interface DeptForm {
  deptId?: number
  parentId: number
  deptName: string
  orderNum: number
  leader?: string
  phone?: string
  email?: string
  status: string
}

// 查询部门列表
export function listDept(query: DeptQuery) {
  return request({
    url: '/api/system/dept/list',
    method: 'get',
    params: query
  })
}

// 查询部门列表（排除节点）
export function listDeptExcludeChild(deptId: number) {
  return request({
    url: '/api/system/dept/list/exclude/' + deptId,
    method: 'get'
  })
}

// 查询部门详细
export function getDept(deptId: number) {
  return request({
    url: '/api/system/dept/' + deptId,
    method: 'get'
  })
}

// 新增部门
export function addDept(data: DeptForm) {
  return request({
    url: '/api/system/dept',
    method: 'post',
    data: data
  })
}

// 修改部门
export function updateDept(data: DeptForm) {
  return request({
    url: '/api/system/dept',
    method: 'put',
    data: data
  })
}

// 删除部门
export function delDept(deptId: number) {
  return request({
    url: '/api/system/dept/' + deptId,
    method: 'delete'
  })
}

// 查询部门下拉树结构
export function deptTreeSelect() {
  return request({
    url: '/api/system/dept/treeselect',
    method: 'get'
  })
}