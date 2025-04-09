import request from '@/utils/request'
import {
    API_GET_COMMENTS_LIST,
    API_COMMENT_STATE,
    API_COMMENT_REPULSE,
    API_COMMENT_STATISTIC,
    API_COMMENT_TOP_LIST
} from '@/constants/api'

/**
 * 获取评论列表
 * @param {Object} params 
 * @returns 
 */
export function getCommentsList(params) {
    return request({
        url: API_GET_COMMENTS_LIST,
        method: 'get',
        params
    })
}

/**
 * 修改评论状态（通过/拒绝）
 * @param {Number} id 评论ID
 * @param {Object} data 
 * @returns 
 */
export function changeCommentState(id, data) {
    return request({
        url: `${API_COMMENT_STATE}/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除评论（撤销）
 * @param {Number} id 评论ID
 * @returns 
 */
export function deleteComment(id) {
    return request({
        url: `${API_COMMENT_REPULSE}/${id}`,
        method: 'delete'
    })
}

/**
 * 获取评论统计数据
 * @returns 
 */
export function getCommentStatistic() {
    return request({
        url: API_COMMENT_STATISTIC,
        method: 'get'
    })
}

/**
 * 获取热门评论列表
 * @param {Object} params 
 * @returns 
 */
export function getTopCommentsList(params) {
    return request({
        url: API_COMMENT_TOP_LIST,
        method: 'get',
        params
    })
}
