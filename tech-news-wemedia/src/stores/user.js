import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/user'
import { setToken, getToken, removeToken, setUserInfo, getUserInfo, removeUserInfo } from '@/utils/auth'
import router from '@/router'
import { logout } from '@/api/login'

const USER_KEY = 'tech_news_user'

export const useUserStore = defineStore('user', {
    state: () => ({
        userInfo: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
        token: localStorage.getItem('token') || ''
    }),

    actions: {
        setUser(user) {
            this.userInfo = user
            this.token = user.token
            // 保存到本地存储
            localStorage.setItem(USER_KEY, JSON.stringify(user))
            localStorage.setItem('token', user.token)
        },

        getToken() {
            return this.token
        },

        clearUser() {
            this.userInfo = null
            this.token = ''
            localStorage.removeItem(USER_KEY)
            localStorage.removeItem('token')
        },

        async handleLogout() {
            try {
                await logout()
                this.clearUser()
                router.push('/login')
            } catch (error) {
                console.error('退出登录失败:', error)
                // 即使退出失败，也清除本地信息并跳转到登录页
                this.clearUser()
                router.push('/login')
            }
        }
    },

    getters: {
        isLoggedIn: (state) => !!state.token
    }
}) 