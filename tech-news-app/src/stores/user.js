import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserInfoDetail } from '@/api/user'

// 用户相关的状态管理
export const useUserStore = defineStore('user', () => {
    // 用户的token
    const token = ref('')
    // 用户信息
    const userInfo = ref({})

    // 保存token
    const setToken = (newToken) => {
        token.value = newToken
        localStorage.setItem('TOKEN', newToken)
    }

    // 获取token
    const getToken = () => {
        if (!token.value) {
            token.value = localStorage.getItem('TOKEN') || ''
        }
        return token.value
    }

    // 清除token
    const clearToken = () => {
        token.value = ''
        localStorage.removeItem('TOKEN')
    }

    // 设置用户信息
    const setUserInfo = (info) => {
        userInfo.value = info
        localStorage.setItem('USER_INFO', JSON.stringify(info))
    }

    // 获取用户信息
    const getUserInfo = async () => {
        if (getToken()) {
            try {
                const res = await getUserInfoDetail()
                if (res.data) {
                    setUserInfo(res.data)
                }
                return res.data
            } catch (err) {
                console.log('获取用户信息失败', err)
                return null
            }
        }
        return null
    }

    // 清除用户信息
    const clearUserInfo = () => {
        userInfo.value = {}
        localStorage.removeItem('USER_INFO')
    }

    // 登出
    const logout = () => {
        clearToken()
        clearUserInfo()
    }

    // 初始化用户信息
    const initUserInfo = () => {
        const savedInfo = localStorage.getItem('USER_INFO')
        if (savedInfo) {
            userInfo.value = JSON.parse(savedInfo)
        }
    }

    // 初始化
    initUserInfo()

    return {
        token,
        userInfo,
        setToken,
        getToken,
        clearToken,
        setUserInfo,
        getUserInfo,
        clearUserInfo,
        logout
    }
}) 