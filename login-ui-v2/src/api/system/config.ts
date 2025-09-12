import request from '@/utils/request'

// 参数查询参数
export interface ConfigQuery {
  pageNum?: number
  pageSize?: number
  configName?: string
  configKey?: string
  configType?: string
}

// 参数信息
export interface ConfigVO {
  configId: number
  configName: string
  configKey: string
  configValue: string
  configType: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
}

// 参数表单
export interface ConfigForm {
  configId?: number
  configName: string
  configKey: string
  configValue: string
  configType: string
  remark?: string
}

// 查询参数设置列表
export function listConfig(query: ConfigQuery) {
  return request({
    url: '/api/system/config/list',
    method: 'get',
    params: query
  })
}

// 查询参数设置详细
export function getConfig(configId: number) {
  return request({
    url: '/api/system/config/' + configId,
    method: 'get'
  })
}

// 根据参数键名查询参数值
export function getConfigKey(configKey: string) {
  return request({
    url: '/api/system/config/configKey/' + configKey,
    method: 'get'
  })
}

// 新增参数设置
export function addConfig(data: ConfigForm) {
  return request({
    url: '/api/system/config',
    method: 'post',
    data: data
  })
}

// 修改参数设置
export function updateConfig(data: ConfigForm) {
  return request({
    url: '/api/system/config',
    method: 'put',
    data: data
  })
}

// 删除参数设置
export function delConfig(configId: number | number[]) {
  return request({
    url: '/api/system/config/' + configId,
    method: 'delete'
  })
}

// 刷新参数缓存
export function refreshCache() {
  return request({
    url: '/api/system/config/refreshCache',
    method: 'delete'
  })
}

// 导出参数设置
export function exportConfig(query: ConfigQuery) {
  return request({
    url: '/api/system/config/export',
    method: 'post',
    data: query
  })
}