import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详情
export function getOrder(orderId) {
  return request({
    url: '/order/' + orderId,
    method: 'get'
  })
}

// 新增订单
export function addOrder(data) {
  return request({
    url: '/order',
    method: 'post',
    data: data
  })
}

// 修改订单
export function updateOrder(data) {
  return request({
    url: '/order',
    method: 'put',
    data: data
  })
}

// 订单完工
export function completeOrder(orderId, data) {
  return request({
    url: '/order/' + orderId + '/complete',
    method: 'post',
    data: data
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: '/order/' + orderId + '/cancel',
    method: 'post'
  })
}
