import axios from 'axios'
import JSONbig from 'json-bigint'
import router from '@/router'
import { message, Modal } from 'ant-design-vue'
import { getUser, setUser, clearUser } from '@/utils/store'
import { API_USERIMAGES_ADD } from '@/constants/api'

// 创建axios实例
const service = axios.create({
    baseURL: 'http://localhost:9999/',
    timeout: 5000,
    transformResponse: [function (data) {
        try {
            // 使用json-bigint处理返回数据，避免大整数精度丢失
            return JSONbig.parse(data)
        } catch (err) {
            return data
        }
    }]
})

// 是否为图片上传请求
const isImgUpload = (config) => {
    return (config.url === API_USERIMAGES_ADD && config.method === 'post')
}

// 请求拦截器
service.interceptors.request.use(
    config => {
        const authorization = getUser()
        if (authorization && authorization.token) {
            // 如果当前缓存中存在用户令牌
            if (!isImgUpload(config)) {
                /** 在非图片上传场景下 给默认的上传格式 **/
                config.headers['Content-Type'] = 'application/json'
            } else {
                config.headers['Content-Type'] = 'multipart/form-data'
            }
            config.headers.token = authorization.token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 添加响应拦截器
service.interceptors.response.use(function (response) {
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

export default service 