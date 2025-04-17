import request from '@/utils/request'

// 获取用户详情
export const getUserInfo = () => {
    return request.get('/api/user/info')
}

// 获取个人信息
export const getUserInfoDetail = () => {
    return request.get('/api/user/profile')
}

// 更新用户信息
export const updateUserInfo = (data) => {
    return request.put('/api/user/info', data)
}

// 密码登录
export const loginByPassword = (data) => {
    return request.post('/api/login/in', data)
}

// 验证码登录
export const loginByCode = (data) => {
    return request.post('/api/login/in', data)
}

// 获取短信验证码
export const getVerifyCode = (mobile) => {
    return request.get(`/api/login/code?mobile=${mobile}`)
}

// 注册
export const register = (data) => {
    return request.post('/api/login/register', data)
}

// 重置密码
export const resetPassword = (data) => {
    return request.post('/api/login/reset-password', data)
}

// 退出登录
export const logout = () => {
    return request.post('/api/login/out')
}

// 上传图片
export const upload = (file, fileName, prefix) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('fileName', fileName)
    formData.append('prefix', prefix)
    return request.post('/api/user/upload', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

// 更新用户头像
export const uploadImage = async (file, prefix) => {
    // 验证文件类型
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']
    if (!allowedTypes.includes(file.type)) {
        throw new Error('只支持jpg、jpeg和png格式的图片')
    }

    // 验证文件大小（限制为2MB）
    if (file.size > 2 * 1024 * 1024) {
        throw new Error('图片大小不能超过2MB')
    }

    // 上传文件
    const res = await upload(file, file.name, prefix)
    if (res.code === 0) {
        return res.data
    }
    throw new Error('头像上传失败')
}

// 提交实名认证
export const submitMediaCert = (data) => {
    return request.post('/api/auth/submit', data)
}
