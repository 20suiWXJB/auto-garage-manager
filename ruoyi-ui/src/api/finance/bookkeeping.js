import request from '@/utils/request'

// 查询记账单列表
export function listBookkeeping(query) {
  return request({
    url: '/finance/bookkeeping/list',
    method: 'get',
    params: query
  })
}

// 查询记账单统计
export function statsBookkeeping() {
  return request({
    url: '/finance/bookkeeping/stats',
    method: 'get'
  })
}

// 查询记账单详情
export function getBookkeeping(bookkeepingId) {
  return request({
    url: '/finance/bookkeeping/' + bookkeepingId,
    method: 'get'
  })
}

// 新增记账单
export function addBookkeeping(data) {
  return request({
    url: '/finance/bookkeeping',
    method: 'post',
    data: data
  })
}

// 修改记账单
export function updateBookkeeping(data) {
  return request({
    url: '/finance/bookkeeping',
    method: 'put',
    data: data
  })
}

// 删除记账单
export function delBookkeeping(bookkeepingId) {
  return request({
    url: '/finance/bookkeeping/' + bookkeepingId,
    method: 'delete'
  })
}

// 登记收款
export function receiptBookkeeping(data) {
  return request({
    url: '/finance/bookkeeping/receipt',
    method: 'post',
    data: data
  })
}
