/* 封装axios用于发送请求 */
import axios from 'axios'
import { message } from 'ant-design-vue'
import { useUserStore } from '@/stores'
import JSONbig from 'json-bigint'

// 创建一个新的axios实例
const request = axios.create({
    baseURL: 'http://localhost:7777',
    timeout: 50000,
    transformResponse: [function (data) {
        try {
            // 使用json-bigint处理返回数据，避免大整数精度丢失
            return JSONbig.parse(data)
        } catch (err) {
            return data
        }
    }]
})

// 添加请求拦截器
request.interceptors.request.use(function (config) {
    // 在发送请求之前做些什么
    // 从 localStorage 直接获取 token
    const token = localStorage.getItem('token')
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
        message.error(res.errorMessage || '请求失败')
        return Promise.reject(res.errorMessage)
    }
    return res
}, function (error) {
    // 对响应错误做点什么
    message.error(error.message || '网络错误')
    return Promise.reject(error)
})

export default request
