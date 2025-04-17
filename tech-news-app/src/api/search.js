import request from '@/utils/request'

// 获取热门搜索
export const getHotSearch = () => {
  return request.get('/search/hot')
}

// 搜索文章
export const searchArticles = (params) => {
  return request.get('/search', { params })
}

// 获取搜索历史
export const getSearchHistory = () => {
  return request.get('/search/history')
}

// 清除搜索历史
export const clearSearchHistory = () => {
  return request.delete('/search/history')
}

// 添加搜索历史
export const addSearchHistory = (keyword) => {
  return request.post('/search/history', { keyword })
} 