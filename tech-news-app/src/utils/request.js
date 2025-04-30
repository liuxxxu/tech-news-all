/* 封装axios用于发送请求 */
import axios from 'axios'
import { showToast, showLoadingToast } from './vant-ui'
import { useUserStore } from '@/stores'
import router from '@/router'
import JSONbig from 'json-bigint'

// 创建一个新的JSONbig实例，配置为将大整数转换为字符串
const JSONbigString = JSONbig({ storeAsString: true })

// 创建一个新的axios实例
const request = axios.create({
    baseURL: 'http://localhost:8888/',
    timeout: 5000,
    transformResponse: [function (data) {
        try {
            // 使用配置后的JSONbigString处理返回数据
            return JSONbigString.parse(data)
        } catch (err) {
            return data
        }
    }]
})

// 添加请求拦截器
request.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    // showLoadingToast({
    //     message: '请求中...',
    //     forbidClick: true,
    //     loadingType: 'spinner',
    //     duration: 0
    // })
    const userStore = useUserStore()
    const token = userStore.getToken()
    if (token) {
        config.headers['token'] = token
    }
    return config
}, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error)
})

// 添加响应拦截器
request.interceptors.response.use(function (response) {
    // 对响应数据做点什么
    const res = response.data
    if (res.code !== 0) {
        showToast(res.errorMessage || '请求失败')
        return Promise.reject(res.errorMessage)
    }
    // 清除 loading 中的效果
    // closeToast()
    return res
}, function (error) {
    // 对响应错误做点什么
    if (error.response && error.response.status === 401) {
        showToast('登录已过期，请重新登录')
        const userStore = useUserStore()
        userStore.clearToken()
        router.push('/login')
    } else {
        showToast(error.message || '网络错误')
    }
    return Promise.reject(error)
})

export default request
