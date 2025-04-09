import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

// 路由配置
const routes = [
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/login/index.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('@/views/layout/index.vue'),
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'home',
                component: () => import('@/views/home/index.vue'),
                meta: { title: '首页', icon: 'home', requiresAuth: true }
            },
            {
                path: 'content',
                name: 'content',
                meta: { title: '内容管理', icon: 'content', requiresAuth: true },
                children: [
                    {
                        path: 'index',
                        name: 'content-index',
                        component: () => import('@/views/content/index.vue'),
                        meta: { title: '内容管理', requiresAuth: true }
                    },
                    {
                        path: 'publish',
                        name: 'content-publish',
                        component: () => import('@/views/content/publish.vue'),
                        meta: { title: '发布文章', requiresAuth: true }
                    },
                    {
                        path: 'publish/:id',
                        name: 'content-edit',
                        component: () => import('@/views/content/publish.vue'),
                        meta: { title: '编辑文章', requiresAuth: true, hidden: true }
                    }
                ]
            },
            {
                path: 'material',
                name: 'material',
                component: () => import('@/views/material/index.vue'),
                meta: { title: '素材管理', icon: 'material', requiresAuth: true }
            },
            {
                path: 'statistics',
                name: 'statistics',
                component: () => import('@/views/statistics/index.vue'),
                meta: { title: '统计分析', icon: 'statistics', requiresAuth: true }
            },
            {
                path: 'fans',
                name: 'fans',
                meta: { title: '粉丝管理', icon: 'fans', requiresAuth: true },
                children: [
                    {
                        path: 'index',
                        name: 'fans-index',
                        component: () => import('@/views/fans/index.vue'),
                        meta: { title: '粉丝概况', requiresAuth: true }
                    },
                    {
                        path: 'portrait',
                        name: 'fans-portrait',
                        component: () => import('@/views/fans/portrait.vue'),
                        meta: { title: '粉丝画像', requiresAuth: true }
                    },
                    {
                        path: 'list',
                        name: 'fans-list',
                        component: () => import('@/views/fans/list.vue'),
                        meta: { title: '粉丝列表', requiresAuth: true }
                    },
                    {
                        path: 'message',
                        name: 'fans-message',
                        component: () => import('@/views/fans/message.vue'),
                        meta: { title: '私信管理', requiresAuth: true }
                    }
                ]
            },
            {
                path: 'comment',
                name: 'comment',
                meta: { title: '评论管理', icon: 'comment', requiresAuth: true },
                children: [
                    {
                        path: 'dashboard',
                        name: 'comment-dashboard',
                        component: () => import('@/views/comment/dashboard.vue'),
                        meta: { title: '评论概况', requiresAuth: true }
                    },
                    {
                        path: 'list',
                        name: 'comment-list',
                        component: () => import('@/views/comment/list.vue'),
                        meta: { title: '评论列表', requiresAuth: true }
                    }
                ]
            }
        ]
    },
    {
        path: '/404',
        name: 'NotFound',
        component: () => import('@/views/404.vue'),
        meta: { title: '404' }
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/404'
    }
]

// 创建路由实例
const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由前置守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    document.title = to.meta.title ? `${to.meta.title} - 科技新闻自媒体系统` : '科技新闻自媒体系统'

    // 如果是登录页面，则直接通过
    if (to.path === '/login') {
        next()
        return
    }

    // 判断是否有token
    const token = localStorage.getItem('token')
    if (!token) {
        next('/login')
        return
    }

    next()
})

export default router 