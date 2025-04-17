export const API_GETPHONECODE = 'sms/codes/' // 获取短信验证码
export const API_CAPTCHAS = 'captchas/' // 人机验证码
export const API_USERIMAGES_LIST = '/wemedia/api/uploadImage/material/list' // 用户图片素材列表
export const API_USERIMAGES_ADD = '/wemedia/api/material/upload' // 用户图片素材上传
export const API_CHANNELS = '/wemedia/api/channels/all' // 获取文章频道
export const API_ARTICLES = '/wemedia/api/news/submit' // post文章(新建)  get拉取文章列表
export const API_SEARCHARTICELS = '/wemedia/api/news/listByUser' // 检索文章
export const API_ARTICLES_UPDOWN = '/wemedia/api/news/down_or_up' // 上下架
export const API_ARTICLES_DELETE = 'wemedia/api/news/del_news' // 删除文章
export const API_ARTICLES_INFO = '/wemedia/api/news/one' // 获取文章
export const API_COMMENTS = 'comments' // 获取评论或者评论回复
export const API_COMMENT_LIST = '/api/comment/list' // 获取评论或者评论回复

export const API_CLOSECOMMENTS = 'comments/status' // 关闭或者打开评论
export const API_ADMIRECOMMENT = 'comment/likings' // 对评论或回复点赞
export const API_CANCELADMIRECOMMENT = 'comment/likings/' // 对评论或回复取消点赞
export const API_MODIFYIMAGE_COL = '/wemedia/api/material/collect' // 收藏用户素材
export const API_MODIFYIMAGE_COL_CANCEL = '/wemedia/api/material/cancel_collect' // 取消收藏用户素材
export const API_MODIFYIMAGE_DELETE = '/wemedia/api/material/del_picture' // 删除图片
export const API_USERPROFILE = 'user/profile' // 获取用户个人资料
export const API_USERUPDATEPROFILE = 'user/updateprofile' // 修改用户个人资料
export const API_FANS = '/mp_0/followers'
export const API_FOLLOWER_PORTRAIT = '/mp_0/followers/portrait'
export const API_HEAD = 'user/photo' // 编辑用户信息
export const API_FANS_AVATAR = '/mp_0/followers/avatars'
export const API_CHANGE_FOLLOW_STATE = '/mp_0/followers/state'
export const API_GET_FANS_STATISTIC = '/mp_0/statistics/followers'

export const API_STATISTICS_NEWS = '/mp_0/statistics/news' // 图文统计
export const API_STATISTICS_PORTRAIT = '/mp_0/statistics/portrait' // 画像统计
export const API_STATISTICS_RANK = '/mp_0/statistics/article/rank'

// 评论管理
export const API_GET_COMMENTS_LIST = '/mp_0/comments'
export const API_COMMENT_STATE = '/mp_0/comments/status'
export const API_COMMENT_REPULSE = '/mp_0/comments'
export const API_COMMENT_STATISTIC = '/mp_0/statistics/comments'
export const API_COMMENT_TOP_LIST = '/mp_0/comments/top'

// 用户相关
export const API_LOGIN = '/wemedia/api/login/in' // 用户登录
export const API_LOGOUT = '/wemedia/api/login/out' // 用户登出
export const API_USER_PROFILE = '/wemedia/api/user/profile' // 获取用户信息
