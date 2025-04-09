import request from '@/utils/request'
import { API_NEWS_LIST, API_NEWS_ONE, API_NEWS_PASS, API_NEWS_FAIL, API_NEWS_PUBLISHED, API_NEWS_DOWN_OR_UP } from '@/constants/api'

// 内容审核列表
export function authList(data) {
    return request({
        url: API_NEWS_LIST,
        method: 'post',
        data
    })
}

// 获取单个新闻详情
export function authOne(id) {
    return request({
        url: API_NEWS_ONE + id,
        method: 'get'
    })
}

// 审核通过
export function authPass(data) {
    return request({
        url: API_NEWS_PASS,
        method: 'post',
        data
    })
}

// 审核驳回
export function authFail(data) {
    return request({
        url: API_NEWS_FAIL,
        method: 'post',
        data
    })
}

// 获取已发布文章列表
export function findPublished(data) {
    return request({
        url: API_NEWS_LIST,
        method: 'post',
        data
    })
}

// 文章上架&下架
export function downOrUp(data) {
    return request({
        url: API_NEWS_DOWN_OR_UP,
        method: 'post',
        data
    })
}

/**
 * 获取文章详情
 * @param {string|number} id 文章ID
 * @returns Promise
 */
export function getNewsDetail(id) {
    return request({
        url: `/admin/api/news/auth_one/${id}`,
        method: 'get'
    })
} 