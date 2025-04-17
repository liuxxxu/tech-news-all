import request from '@/utils/request'
import {
    API_STATISTICS_NEWS,
    API_STATISTICS_PORTRAIT,
    API_STATISTICS_RANK
} from '@/constants/api'

/**
 * 获取图文数据统计
 * @param {Object} params 
 * @returns 
 */
export function getNewsStatistics(params) {
    return request({
        url: API_STATISTICS_NEWS,
        method: 'get',
        params
    })
}

/**
 * 获取用户画像数据
 * @param {Object} params 
 * @returns 
 */
export function getPortraitStatistics(params) {
    return request({
        url: API_STATISTICS_PORTRAIT,
        method: 'get',
        params
    })
}

/**
 * 获取文章排行数据
 * @param {Object} params 
 * @returns 
 */
export function getArticleRank(params) {
    return request({
        url: API_STATISTICS_RANK,
        method: 'get',
        params
    })
} 