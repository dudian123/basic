import request from '@/utils/request'

// 字典类型API

// 查询字典类型列表
export function listDictType(query) {
  return request({
    url: '/api/system/dict/type/list',
    method: 'get',
    params: query
  })
}

// 查询字典类型详细
export function getDictType(dictId) {
  return request({
    url: '/api/system/dict/type/' + dictId,
    method: 'get'
  })
}

// 新增字典类型
export function addDictType(data) {
  return request({
    url: '/api/system/dict/type',
    method: 'post',
    data: data
  })
}

// 修改字典类型
export function updateDictType(data) {
  return request({
    url: '/api/system/dict/type',
    method: 'put',
    data: data
  })
}

// 删除字典类型
export function delDictType(dictIds) {
  return request({
    url: '/api/system/dict/type/' + dictIds,
    method: 'delete'
  })
}

// 导出字典类型
export function exportDictType(query) {
  return request({
    url: '/api/system/dict/type/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  }).then(response => {
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '字典类型.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  })
}

// 刷新字典缓存
export function refreshCache() {
  return request({
    url: '/api/system/dict/type/refreshCache',
    method: 'delete'
  })
}

// 获取字典选择框列表
export function optionselectDictType() {
  return request({
    url: '/api/system/dict/type/optionselect',
    method: 'get'
  })
}

// 校验字典类型是否唯一
export function checkDictTypeUnique(dictType) {
  return request({
    url: '/api/system/dict/type/checkDictTypeUnique',
    method: 'get',
    params: { dictType }
  })
}

// 字典数据API

// 查询字典数据列表
export function listDictData(query) {
  return request({
    url: '/api/system/dict/data/list',
    method: 'get',
    params: query
  })
}

// 查询字典数据详细
export function getDictData(dictCode) {
  return request({
    url: '/api/system/dict/data/' + dictCode,
    method: 'get'
  })
}

// 根据字典类型查询字典数据信息
export function getDictDataByType(dictType) {
  return request({
    url: '/api/system/dict/data/type/' + dictType,
    method: 'get'
  })
}

// 新增字典数据
export function addDictData(data) {
  return request({
    url: '/api/system/dict/data',
    method: 'post',
    data: data
  })
}

// 修改字典数据
export function updateDictData(data) {
  return request({
    url: '/api/system/dict/data',
    method: 'put',
    data: data
  })
}

// 删除字典数据
export function delDictData(dictCodes) {
  return request({
    url: '/api/system/dict/data/' + dictCodes,
    method: 'delete'
  })
}

// 导出字典数据
export function exportDictData(query) {
  return request({
    url: '/api/system/dict/data/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  }).then(response => {
    const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '字典数据.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  })
}

// 根据字典类型和字典键值查询字典数据信息
export function getDictLabel(dictType, dictValue) {
  return request({
    url: '/api/system/dict/data/label',
    method: 'get',
    params: { dictType, dictValue }
  })
}