/**
 * 认证相关工具函数
 */

const TOKEN_KEY = 'hm_token'
const USER_INFO_KEY = 'hm_user_info'

/**
 * 获取Token
 * @returns {string} token值
 */
export function getToken() {
    return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置Token
 * @param {string} token - 登录后的token值
 */
export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除Token
 */
export function removeToken() {
    localStorage.removeItem(TOKEN_KEY)
}

/**
 * 获取用户信息
 * @returns {Object} 用户信息对象
 */
export function getUserInfo() {
    const userInfo = localStorage.getItem(USER_INFO_KEY)
    return userInfo ? JSON.parse(userInfo) : null
}

/**
 * 设置用户信息
 * @param {Object} userInfo - 用户信息对象
 */
export function setUserInfo(userInfo) {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
}

/**
 * 移除用户信息
 */
export function removeUserInfo() {
    localStorage.removeItem(USER_INFO_KEY)
}

/**
 * 清除所有认证信息
 */
export function clearAuth() {
    removeToken()
    removeUserInfo()
} 