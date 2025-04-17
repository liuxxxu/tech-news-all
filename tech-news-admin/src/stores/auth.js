import { defineStore } from 'pinia'
import { message } from 'ant-design-vue'
import { findAuthList, authPass, authFail } from '@/api/auth'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        list: [],
        total: 0,
        loading: false,
        host: '',
        query: {
            status: undefined,
            page: 1,
            size: 10
        },
        statusList: [
            { label: undefined, value: '全部' },
            { label: 0, value: '创建中' },
            { label: 1, value: '待审核' },
            { label: 2, value: '审核失败' },
            { label: 9, value: '审核通过' }
        ]
    }),

    actions: {
        // 重置分页
        resetPage() {
            this.query.page = 1
            this.getList()
        },

        // 获取认证列表
        async getList() {
            this.loading = true

            try {
                const res = await findAuthList(this.query)
                if (res.code === 0) {
                    this.list = res.data
                    this.host = res.host
                    this.total = res.total
                } else {
                    message.error(res.errorMessage || '获取认证列表失败')
                }
            } catch (error) {
                console.error('获取认证列表失败:', error)
                message.error('获取认证列表失败')
            } finally {
                this.loading = false
            }
        },

        // 通过认证
        async passAuth(id) {
            try {
                const res = await authPass({ id })
                if (res.code === 0) {
                    message.success('操作成功')
                    this.getList()
                    return { status: res.data?.status, success: true }
                } else {
                    message.error(res.errorMessage || '操作失败')
                    return { success: false }
                }
            } catch (error) {
                console.error('认证通过操作失败:', error)
                message.error('操作失败')
                return { success: false }
            }
        },

        // 驳回认证
        async failAuth(id, msg) {
            try {
                const res = await authFail({ id, msg })
                if (res.code === 0) {
                    message.success('操作成功')
                    this.getList()
                    return {
                        status: res.data?.status,
                        reason: res.data?.reason,
                        success: true
                    }
                } else {
                    message.error(res.errorMessage || '操作失败')
                    return { success: false }
                }
            } catch (error) {
                console.error('认证驳回操作失败:', error)
                message.error('操作失败')
                return { success: false }
            }
        }
    }
}) 