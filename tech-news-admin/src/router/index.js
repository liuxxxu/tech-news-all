import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'
import { message } from 'ant-design-vue'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/index.vue'),
        meta: { title: '登录', requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('@/views/layout/index.vue'),
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/home/index.vue'),
                meta: { title: '首页', requiresAuth: true }
            },
            // 频道管理
            {
                path: 'channel',
                name: 'Channel',
                component: () => import('@/views/channel/index.vue'),
                meta: { title: '频道管理', requiresAuth: true }
            },
            // 内容审核
            {
                path: 'news',
                name: 'News',
                component: () => import('@/views/news/index.vue'),
                meta: { title: '内容审核', requiresAuth: true }
            },
            {
                path: 'news/detail/:id',
                name: 'NewsDetail',
                component: () => import('@/views/news/detail.vue'),
                meta: { title: '文章详情', requiresAuth: true }
            },
            // 内容管理
            {
                path: 'news-published',
                name: 'NewsPublished',
                component: () => import('@/views/news-published/index.vue'),
                meta: { title: '内容管理', requiresAuth: true }
            },
            {
                path: 'news-published/detail/:id',
                name: 'NewsPublishedDetail',
                component: () => import('@/views/news-published/detail.vue'),
                meta: { title: '文章详情', requiresAuth: true }
            },
            // 敏感词管理
            // {
            //     path: 'sensitive',
            //     name: 'Sensitive',
            //     component: () => import('@/views/sensitive/index.vue'),
            //     meta: { title: '敏感词管理', requiresAuth: true }
            // },
            // 认证管理
            {
                path: 'auth',
                name: 'Auth',
                component: () => import('@/views/auth/index.vue'),
                meta: { title: '实名认证', requiresAuth: true }
            },
            {
                path: 'auth/detail',
                name: 'AuthDetail',
                component: () => import('@/views/auth/detail.vue'),
                meta: { title: '认证详情', requiresAuth: true }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/error/404.vue'),
        meta: { title: '404', requiresAuth: false }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
    // 设置页面标题
    document.title = to.meta.title ? `${to.meta.title} - 科技新闻管理系统` : '科技新闻管理系统'

    // 检查该路由是否需要登录权限
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 判断当前是否已登录
        const token = localStorage.getItem('token')
        if (!token) {
            message.warning('请先登录')
            // 将跳转的路由path作为参数，登录成功后跳转到该路由
            next({
                path: '/login',
                query: { redirect: to.fullPath }
            })
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router 