import request from '@/utils/request'
import {
    API_ARTICLES_INFO,
    API_STATISTICS_NEWS,
    API_ARTICLES_UPDOWN,
    API_SEARCHARTICELS,
    API_ARTICLES_DELETE,
    API_ARTICLES,
    API_CHANNELS
} from '@/constants/api'

// 获取文章统计数据
export function getNewsStatistics(data) {
    return request({
        url: API_STATISTICS_NEWS,
        method: 'post',
        params: {},
        data: data
    })
}

// 获取文章频道
export function getChannels() {
    return request({
        url: '/wemedia/api/channels/all',
        method: 'get'
    })
}

// 获取文章详情
export function getArticleById(id) {
    return request({
        url: `${API_ARTICLES_INFO}/${id}`,
        method: 'get'
    })
}

// 获取文章列表
export function searchArticle(data) {
    return request({
        url: API_SEARCHARTICELS,
        method: 'post',
        data: data
    })
}

// 删除文章
export function deleteArticles(id) {
    return request({
        url: `${API_ARTICLES_DELETE}/${id}`,
        method: 'get'
    })
}

// 上下架文章
export function upDownArticle(data) {
    return request({
        url: API_ARTICLES_UPDOWN,
        method: 'post',
        data,
        params: {}
    })
}

// 发布或编辑文章
export function submitArticle(data) {
    return request({
        url: '/wemedia/api/news/submit',
        method: 'post',
        data
    })
} 