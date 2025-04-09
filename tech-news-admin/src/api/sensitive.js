import request from '@/utils/request'
import { API_SENSITIVE_LIST, API_SENSITIVE_SAVE, API_SENSITIVE_UPDATE, API_SENSITIVE_DELETE } from '@/constants/api'

// 获取敏感词列表
export function loadList(data) {
  return request({
    url: API_SENSITIVE_LIST,
    method: 'post',
    data
  })
}

// 保存敏感词
export function saveData(data) {
  return request({
    url: API_SENSITIVE_SAVE,
    method: 'post',
    data
  })
}

// 更新敏感词
export function updateData(data) {
  return request({
    url: API_SENSITIVE_UPDATE,
    method: 'post',
    data
  })
}

// 删除敏感词
export function deleteData(id) {
  return request({
    url: API_SENSITIVE_DELETE + id,
    method: 'delete'
  })
} 