import { defineStore } from 'pinia'
import { message } from 'ant-design-vue'

// 用户信息存储的键名
const USER_KEY = 'tech_news_user'

export const useUserStore = defineStore('user', {
    state: () => {
        // 从本地存储获取用户信息
        const userInfo = localStorage.getItem(USER_KEY)
        return {
            userInfo: userInfo ? JSON.parse(userInfo) : null,
            token: userInfo ? JSON.parse(userInfo).token : ''
        }
    },

    getters: {
        isLoggedIn: (state) => !!state.token,
        username: (state) => state.userInfo?.name || '未登录'
    },

    actions: {
        // 设置用户信息
        setUser(user) {
            this.userInfo = user
            this.token = user.token
            localStorage.setItem(USER_KEY, JSON.stringify(user))
        },

        // 更新token
        updateToken(token) {
            if (this.userInfo) {
                this.userInfo.token = token
                this.token = token
                localStorage.setItem(USER_KEY, JSON.stringify(this.userInfo))
            }
        },

        // 登出
        logout() {
            this.userInfo = null
            this.token = ''
            localStorage.removeItem(USER_KEY)
            localStorage.removeItem('token')
        }
    }
}) 