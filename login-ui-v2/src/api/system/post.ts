import request from '@/utils/request'

// 岗位查询参数
export interface PostQuery {
  pageNum?: number
  pageSize?: number
  postCode?: string
  postName?: string
  status?: string
}

// 岗位信息
export interface PostVO {
  postId: number
  postCode: string
  postName: string
  postSort: number
  status: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 岗位表单
export interface PostForm {
  postId?: number
  postCode: string
  postName: string
  postSort: number
  status: string
  remark?: string
}

// 查询岗位列表
export function listPost(query: PostQuery) {
  return request({
    url: '/api/system/post/list',
    method: 'get',
    params: query
  })
}

// 查询岗位详细
export function getPost(postId: number) {
  return request({
    url: '/api/system/post/' + postId,
    method: 'get'
  })
}

// 新增岗位
export function addPost(data: PostForm) {
  return request({
    url: '/api/system/post',
    method: 'post',
    data: data
  })
}

// 修改岗位
export function updatePost(data: PostForm) {
  return request({
    url: '/api/system/post',
    method: 'put',
    data: data
  })
}

// 删除岗位
export function delPost(postId: number | number[]) {
  return request({
    url: '/api/system/post/' + postId,
    method: 'delete'
  })
}

// 导出岗位
export function exportPost(query: PostQuery) {
  return request({
    url: '/api/system/post/export',
    method: 'post',
    data: query
  })
}

// 获取岗位选择框列表
export function optionselect() {
  return request({
    url: '/api/system/post/optionselect',
    method: 'get'
  })
}

// 岗位状态修改
export function changePostStatus(postId: number, status: string) {
  const data = {
    postId,
    status
  }
  return request({
    url: '/api/system/post/changeStatus',
    method: 'put',
    data: data
  })
}