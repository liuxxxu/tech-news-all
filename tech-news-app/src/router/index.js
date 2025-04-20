import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'

const routes = [
    {
        path: '/',
        component: () => import('@/views/layout/index.vue'),
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/layout/home.vue'),
                meta: {
                    title: '首页'
                }
            },
            {
                path: 'dynamic',
                name: 'Dynamic',
                component: () => import('@/views/layout/dynamic.vue'),
                meta: {
                    title: '动态'
                }
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/layout/profile.vue'),
                meta: {
                    title: '我的'
                }
            }
        ]
    },
    {
        path: '/category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: {
            title: '频道'
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login/login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/login/register.vue'),
        meta: {
            title: '注册'
        }
    },
    {
        path: '/search',
        name: 'Search',
        component: () => import('@/views/search/index.vue'),
        meta: {
            title: '搜索'
        }
    },
    {
        path: '/search/result',
        name: 'SearchResult',
        component: () => import('@/views/search/result.vue'),
        meta: {
            title: '搜索结果'
        }
    },
    // {
    //     path: '/article/:id',
    //     name: 'ArticleDetail',
    //     component: () => import('@/views/article/detail.vue'),
    //     meta: {
    //         title: '文章详情'
    //     }
    // },
    {
        path: '/article',
        name: 'ArticleDetail',
        component: () => import('@/views/detail/index.vue'),
        meta: {
            title: '文章详情'
        }
    },
    {
        path: '/article/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/article/detail.vue'),
        meta: {
            title: '文章详情'
        }
    },
    {
        path: '/user-profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: {
            title: '账号资料'
        }
    },
    {
        path: '/reset-password',
        name: 'ResetPassword',
        component: () => import('@/views/user/reset-password.vue'),
        meta: {
            title: '修改密码'
        }
    },
    {
        path: '/media-cert',
        name: 'MediaCert',
        component: () => import('@/views/user/media-cert.vue'),
        meta: {
            title: '媒体认证'
        }
    },
    {
        path: '/space',
        name: 'space',
        component: () => import('@/views/user/space.vue'),
        meta: {
            title: '我的空间'
        }
    },
    {
        path: '/post',
        name: 'post',
        component: () => import('@/views/post/index.vue'),
        meta: {
            title: '发布动态'
        }
    },
    {
        path: '/user/follows',
        name: 'follows',
        component: () => import('@/views/user/follows.vue'),
        meta: {
            title: '关注与粉丝'
        }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 不登录能访问的路由
const whiteList = ['/login', '/register', '/home']

// 路由守卫
router.beforeEach((to, from, next) => {
    document.title = to.meta.title ? `${to.meta.title} - 科技新闻` : '科技新闻'

    const userStore = useUserStore()
    const token = userStore.getToken()

    // 如果需要登录且没有token，重定向到登录页
    if (!whiteList.includes(to.path) && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router
