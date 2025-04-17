import request from '@/utils/request'
import { setUser } from '@/utils/store'
import { API_GETPHONECODE, API_LOGIN, API_CAPTCHAS } from '@/constants/api'

export function loginByUsername(data) {
    return request({
        url: API_LOGIN,
        method: 'post',
        data
    }).then(result => {
        if (result.code === 0) {
            const temp = result.data
            setUser({ phone: temp.user.phone, photo: null, token: temp.token })
        }
        return result
    })
}

export function logout() {
    return request({
        url: '/wemedia/api/login/out',
        method: 'post'
    })
}

export function getMobileCode(mobile, params) {
    // 获取短信验证码
    return request({
        url: API_GETPHONECODE + mobile,
        method: 'get',
        params
    })
}

// 获取人机验证码
export function getCaptchas(mobile) {
    return request({
        url: API_CAPTCHAS + mobile,
        method: 'get'
    })
} 