import request from '@/utils/request'

// 查询配件档案列表
export function listPart(query) {
  return request({
    url: '/warehouse/part/list',
    method: 'get',
    params: query
  })
}

// 查询配件下拉选项
export function listPartOptions() {
  return request({
    url: '/warehouse/part/options',
    method: 'get'
  })
}

// 查询配件档案详情
export function getPart(partId) {
  return request({
    url: '/warehouse/part/' + partId,
    method: 'get'
  })
}

// 新增配件档案
export function addPart(data) {
  return request({
    url: '/warehouse/part',
    method: 'post',
    data: data
  })
}

// 修改配件档案
export function updatePart(data) {
  return request({
    url: '/warehouse/part',
    method: 'put',
    data: data
  })
}

// 删除配件档案
export function delPart(partId) {
  return request({
    url: '/warehouse/part/' + partId,
    method: 'delete'
  })
}
