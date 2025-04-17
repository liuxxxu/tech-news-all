import request from '@/utils/request'
import { API_LOGIN, API_LOGOUT, API_USER_PROFILE } from '@/constants/api'

/**
 * 用户登录
 * @param {Object} data - 登录参数
 * @param {string} data.mobile - 手机号
 * @param {string} data.code - 验证码
 * @returns {Promise} 请求结果
 */
export function login(data) {
    return request({
        url: API_LOGIN,
        method: 'POST',
        data
    })
}

/**
 * 用户登出
 * @returns {Promise} 请求结果
 */
export function logout() {
    return request({
        url: API_LOGOUT,
        method: 'POST'
    })
}

/**
 * 获取用户信息
 * @returns {Promise} 请求结果
 */
export function getUserProfile() {
    return request({
        url: API_USER_PROFILE,
        method: 'GET'
    })
} 