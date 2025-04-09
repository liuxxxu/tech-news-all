import request from '@/utils/request'
import {
    API_USERIMAGES_LIST,
    API_USERIMAGES_ADD,
    API_MODIFYIMAGE_COL,
    API_MODIFYIMAGE_COL_CANCEL,
    API_MODIFYIMAGE_DELETE
} from '@/constants/api'

// 获取素材列表
export const getAllImgData = (params) => {
    return request({
        url: '/wemedia/api/material/list',
        method: 'post',
        data: {
            page: params.page,
            size: params.size,
            isCollection: params.isCollection === '1' ? 1 : null
        }
    })
}

// 上传图片
export function uploadImg(data) {
    return request({
        url: '/wemedia/api/material/upload',
        method: 'post',
        data
    })
}

// 删除图片素材
export function delImg(id) {
    return request({
        url: API_MODIFYIMAGE_DELETE + '/' + id,
        method: 'get'
    })
}

// 收藏或取消收藏方法
export function collectOrCancel(id, data) {
    const collect = data.isCollected
    let url = API_MODIFYIMAGE_COL
    if (collect === 0) {
        url = API_MODIFYIMAGE_COL_CANCEL
    }
    return request({
        url: url + '/' + id,
        method: 'get'
    })
}

// 收藏素材
export const collectMaterial = (materialId) => {
    return request({
        url: `/wemedia/api/material/collect/${materialId}`,
        method: 'get'
    })
}

// 取消收藏素材
export const cancelCollectMaterial = (materialId) => {
    return request({
        url: `/wemedia/api/material/cancel_collect/${materialId}`,
        method: 'get'
    })
}

// 删除素材
export const deleteMaterial = (materialId) => {
    return request({
        url: `/wemedia/api/material/delete/${materialId}`,
        method: 'get'
    })
} 