import request from '@/utils/request'

// 获取认证列表
export const findAuthList = (data) => {
    const requestData = {
        page: data.page,
        size: data.size
    }
    // 只有当 status 不为 0 时才添加到请求参数中
    if (data.status !== 0) {
        requestData.status = data.status
    }

    return request({
        url: '/admin/api/auth/list',
        method: 'post',
        data: requestData
    })
}

// 通过认证
export const authPass = (data) => {
    return request({
        url: '/admin/api/auth/authPass',
        method: 'put',
        data: {
            id: data.id,
            status: data.status
        }
    })
}

// 驳回认证
export const authFail = (data) => {
    return request({
        url: '/admin/api/auth/authFail',
        method: 'put',
        data: {
            id: data.id,
            status: data.status,
            reason: data.reason
        }
    })
<<<<<<< HEAD
}

// 获取认证详情
export const findAuthDetail = (id) => {
    return request({
        url: `/admin/api/auth/detail/${id}`,
        method: 'get'
    })
=======
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
} 