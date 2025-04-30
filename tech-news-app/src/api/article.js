import request from '@/utils/request'

// 获取文章详情
export const getArticleDetail = (id) => {
  return request.get(`/article/${id}`)
}

// 获取文章评论
export const getArticleComments = (id, params) => {
  return request.get(`/article/comments/${id}`, { params })
}

// 发表评论
export const addComment = (id, data) => {
  return request.post(`/article/comments/${id}`, data)
}

// 点赞文章
export const likeArticle = (id) => {
  return request.post(`/article/like/${id}`)
}

// 取消点赞
export const unlikeArticle = (id) => {
  return request.delete(`/article/like/${id}`)
}

// 收藏文章
export const favoriteArticle = (id) => {
  return request.post(`/article/favorite/${id}`)
}

// 取消收藏
export const unfavoriteArticle = (id) => {
  return request.delete(`/article/favorite/${id}`)
}

// 分享文章
export const shareArticle = (id) => {
  return request.post(`/article/share/${id}`)
} 