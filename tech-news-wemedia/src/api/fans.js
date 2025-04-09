import request from '@/utils/request'
import {
    API_FANS,
    API_FANS_AVATAR,
    API_GET_FANS_STATISTIC,
    API_FOLLOWER_PORTRAIT,
    API_CHANGE_FOLLOW_STATE
} from '@/constants/api'

/**
 * 获取粉丝列表
 * @param {Object} params 
 * @returns 
 */
export function getFansList(params) {
    return request({
        url: API_FANS,
        method: 'get',
        params
    })
}

/**
 * 获取粉丝统计数据
 * @param {Object} params 
 * @returns 
 */
export function getFansStatistic(params) {
    return request({
        url: API_GET_FANS_STATISTIC,
        method: 'get',
        params
    })
}

/**
 * 获取粉丝头像
 * @returns 
 */
export function getFansAvatar() {
    return request({
        url: API_FANS_AVATAR,
        method: 'get'
    })
}

/**
 * 获取粉丝画像数据
 * @returns 
 */
export function getFansPortrait() {
    return request({
        url: API_FOLLOWER_PORTRAIT,
        method: 'get'
    })
}

/**
 * 改变粉丝关注状态
 * @param {Number} id 粉丝ID
 * @param {Object} data 
 * @returns 
 */
export function changeFollowState(id, data) {
    return request({
        url: `${API_CHANGE_FOLLOW_STATE}/${id}`,
        method: 'put',
        data
    })
} 