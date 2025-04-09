export const API_USERAUTH = '/admin/login/in' // 用户认证

// 用户审核
export const API_AUTH_LIST = '/user/api/auth/list'
export const API_AUTH_PASS = '/user/api/auth/authPass'
export const API_AUTH_FAIL = '/user/api/auth/authFail'

// 频道管理
export const API_CHANNEL_LIST = '/admin/api/channel/list'
export const API_CHANNEL_SAVE = '/admin/api/channel/save'
export const API_CHANNEL_UPDATE = '/admin/api/channel/update'
export const API_CHANNEL_DEL = '/admin/api/channel/del'

// 内容审核
export const API_NEWS_LIST = '/admin/api/news/listByStatus'
export const API_NEWS_ONE = '/admin/api/news/auth_one/'
export const API_NEWS_PASS = '/admin/api/news/auth_pass'
export const API_NEWS_FAIL = '/admin/api/news/auth_fail'

// 内容管理
export const API_NEWS_PUBLISHED = '/admin/api/news/findPublished'
export const API_NEWS_DOWN_OR_UP = '/admin/api/news/down_or_up'

// 敏感词设置
export const API_SENSITIVE_LIST = '/admin/api/sensitive/list'
export const API_SENSITIVE_SAVE = '/admin/api/sensitive/save'
export const API_SENSITIVE_UPDATE = '/admin/api/sensitive/update'
export const API_SENSITIVE_DELETE = '/admin/api/sensitive/del/'

export const API_COMMON_LIST = '/api/admin/common/list' // 通用的列表加载器
export const API_COMMON_UPDATE = '/api/admin/common/update' // 通用的修改
export const API_COMMON_DELETE = '/api/admin/common/delete' // 通用的删除
