import request from '@/utils/request'

// 通知公告查询参数
export interface NoticeQuery {
  pageNum?: number
  pageSize?: number
  noticeTitle?: string
  createBy?: string
  noticeType?: string
  status?: string
}

// 通知公告信息
export interface NoticeVO {
  noticeId: number
  noticeTitle: string
  noticeType: string
  noticeContent: string
  status: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 通知公告表单
export interface NoticeForm {
  noticeId?: number
  noticeTitle: string
  noticeType: string
  noticeContent: string
  status: string
  remark?: string
}

// 查询通知公告列表
export function listNotice(query: NoticeQuery) {
  return request({
    url: '/api/system/notice/list',
    method: 'get',
    params: query
  })
}

// 查询通知公告详细
export function getNotice(noticeId: number) {
  return request({
    url: '/api/system/notice/' + noticeId,
    method: 'get'
  })
}

// 新增通知公告
export function addNotice(data: NoticeForm) {
  return request({
    url: '/api/system/notice',
    method: 'post',
    data: data
  })
}

// 修改通知公告
export function updateNotice(data: NoticeForm) {
  return request({
    url: '/api/system/notice',
    method: 'put',
    data: data
  })
}

// 删除通知公告
export function delNotice(noticeId: number | number[]) {
  return request({
    url: '/api/system/notice/' + noticeId,
    method: 'delete'
  })
}