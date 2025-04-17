import request from '@/utils/request'
import { API_CHANNEL_LIST, API_CHANNEL_SAVE, API_CHANNEL_UPDATE, API_CHANNEL_DEL } from '@/constants/api'

export function loadList(data) {
  return request({
    url: API_CHANNEL_LIST,
    method: 'post',
    data
  })
}

export function saveData(data) {
  return request({
    url: API_CHANNEL_SAVE,
    method: 'post',
    data
  })
}

export function updateData(data) {
  return request({
    url: API_CHANNEL_UPDATE,
    method: 'post',
    data
  })
}

export function delData(id) {
  return request({
    url: API_CHANNEL_DEL + '/' + id,
    method: 'get'
  })
} 