import request from '@/utils/request'

// 获取首页文章列表
export const getHomeArticles = (params) => {
  return request.get('/home/articles', { params })
}

// 获取热门文章
export const getHotArticles = () => {
  return request.get('/home/hot')
}

// 获取推荐作者
export const getRecommendedAuthors = () => {
  return request.get('/home/recommended-authors')
}

// 获取文章分类
export const getCategories = () => {
  return request.get('/home/categories')
}

// 获取轮播图
export const getBanners = () => {
  return request.get('/home/banners')
}

// 获取每日推荐
export const getDailyRecommend = () => {
  return request.get('/home/daily-recommend')
} 