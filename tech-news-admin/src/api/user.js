import request from '@/utils/request'
import { API_USERAUTH } from '@/constants/api'

// 登录接口
export function loginApi(data) {
    return request({
        url: API_USERAUTH,
        method: 'post',
        data
    })
}

// 密码登录
export const loginByPassword = (data) => {
    return request({
        url: '/admin/api/login/in',
        method: 'post',
        data
    })
}

// 获取用户信息
export function getUserInfo() {
    return request({
        url: '/api/user/info',
        method: 'get'
    })
}

// 退出登录
export const logout = () => {
    return request({
        url: '/admin/api/login/out',
        method: 'post'
    })
} 