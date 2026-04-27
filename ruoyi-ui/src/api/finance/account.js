import request from '@/utils/request'

// 查询资金账户列表
export function listAccount(query) {
  return request({
    url: '/finance/account/list',
    method: 'get',
    params: query
  })
}

// 查询资金账户选项
export function listAccountOptions() {
  return request({
    url: '/finance/account/options',
    method: 'get'
  })
}

// 查询资金账户详情
export function getAccount(fundAccountId) {
  return request({
    url: '/finance/account/' + fundAccountId,
    method: 'get'
  })
}

// 新增资金账户
export function addAccount(data) {
  return request({
    url: '/finance/account',
    method: 'post',
    data: data
  })
}

// 修改资金账户
export function updateAccount(data) {
  return request({
    url: '/finance/account',
    method: 'put',
    data: data
  })
}

// 删除资金账户
export function delAccount(fundAccountId) {
  return request({
    url: '/finance/account/' + fundAccountId,
    method: 'delete'
  })
}
