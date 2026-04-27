import request from '@/utils/request'

// 查询库存流水列表
export function listStock(query) {
  return request({
    url: '/warehouse/stock/list',
    method: 'get',
    params: query
  })
}

// 查询库存流水详情
export function getStock(stockFlowId) {
  return request({
    url: '/warehouse/stock/' + stockFlowId,
    method: 'get'
  })
}

// 登记入库
export function stockIn(data) {
  return request({
    url: '/warehouse/stock/in',
    method: 'post',
    data: data
  })
}

// 登记出库
export function stockOut(data) {
  return request({
    url: '/warehouse/stock/out',
    method: 'post',
    data: data
  })
}

// 登记库存调整
export function stockAdjust(data) {
  return request({
    url: '/warehouse/stock/adjust',
    method: 'post',
    data: data
  })
}
