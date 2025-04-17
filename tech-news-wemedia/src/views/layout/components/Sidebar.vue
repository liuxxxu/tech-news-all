const menus = [
  {
    id: 'home',
    title: '首页',
    path: '/',
    icon: 'home-outlined'
  },
  {
    id: 'content',
    title: '内容管理',
    icon: 'read-outlined',
    children: [
      {
        id: 'publish',
        title: '发布文章',
        path: '/publish'
      },
      {
        id: 'article',
        title: '内容列表',
        path: '/article'
      },
      {
        id: 'material',
        title: '素材管理',
        path: '/material'
      }
    ]
  },
  {
    id: 'statistics',
    title: '数据统计',
    path: '/statistics/index',
    icon: 'bar-chart-outlined'
  },
  {
    id: 'fans',
    title: '粉丝管理',
    icon: 'team-outlined',
    children: [
      {
        id: 'fans-index',
        title: '粉丝概况',
        path: '/fans/index'
      },
      {
        id: 'fans-portrait',
        title: '粉丝画像',
        path: '/fans/portrait'
      },
      {
        id: 'fans-list',
        title: '粉丝列表',
        path: '/fans/list'
      }
    ]
  },
  {
    id: 'comment',
    title: '评论管理',
    icon: 'comment-outlined',
    children: [
      {
        id: 'comment-dashboard',
        title: '评论概览',
        path: '/comment/dashboard'
      },
      {
        id: 'comment-list',
        title: '评论列表',
        path: '/comment/list'
      }
    ]
  }
] 