import { defineStore } from 'pinia'
import { message } from 'ant-design-vue'
import { authList, authOne, authPass, authFail, findPublished, downOrUp } from '@/api/news'

export const useNewsStore = defineStore('news', {
    state: () => ({
        // 内容审核相关状态
        auditList: [],
        auditTotal: 0,
        auditLoading: false,
        auditQuery: {
            title: undefined,
            status: undefined,
            page: 1,
            size: 10
        },

        // 已发布内容相关状态
        publishedList: [],
        publishedTotal: 0,
        publishedLoading: false,
        publishedQuery: {
            status: 9,
            title: undefined,
            enable: undefined,
            page: 1,
            size: 10,
            beginDate: undefined,
            endDate: undefined
        },

        // 当前文章详情
        currentNews: {
            content: '[]'
        }
    }),

    actions: {
        // 重置审核列表分页
        resetAuditPage() {
            this.auditQuery.page = 1
            this.getAuditList()
        },

        // 获取待审核列表
        async getAuditList() {
            if (this.auditQuery.title === '') {
                this.auditQuery.title = undefined
            }
            if (this.auditQuery.status === '') {
                this.auditQuery.status = undefined
            }

            this.auditLoading = true

            try {
                const query = {
                    ...this.auditQuery,
                    status: this.auditQuery.status === undefined ? null : this.auditQuery.status,
                    page: this.auditQuery.page || 1,
                    size: this.auditQuery.size || 10
                }
                const res = await authList(query)
                if (res.code === 0) {
                    this.auditList = res.data
                    this.auditTotal = res.total
                }
            } catch (error) {
                console.error('获取审核列表失败:', error)
            } finally {
                this.auditLoading = false
            }
        },

        // 获取文章详情
        async getNewsDetail(id) {
            try {
                const res = await authOne(id)
                if (res.code === 0) {
                    this.currentNews = res.data
                    return res.data
                }
                return null
            } catch (error) {
                console.error('获取文章详情失败:', error)
                return null
            }
        },

        // 审核通过
        async passNews(data) {
            try {
                const res = await authPass(data)
                if (res.code === 0) {
                    message.success('审核通过成功')
                    return true
                }
                return false
            } catch (error) {
                console.error('审核通过失败:', error)
                return false
            }
        },

        // 审核驳回
        async failNews(data) {
            try {
                const res = await authFail(data)
                if (res.code === 0) {
                    message.success('审核驳回成功')
                    return true
                }
                return false
            } catch (error) {
                console.error('审核驳回失败:', error)
                return false
            }
        },

        // 重置已发布列表分页
        resetPublishedPage() {
            this.publishedQuery.page = 1
            this.getPublishedList()
        },

        // 获取已发布列表
        async getPublishedList() {
            if (this.publishedQuery.title === '') {
                this.publishedQuery.title = undefined
            }
            if (this.publishedQuery.enable === '') {
                this.publishedQuery.enable = undefined
            }

            this.publishedLoading = true

            try {
                const res = await findPublished(this.publishedQuery)
                if (res.code === 0) {
                    this.publishedList = res.data
                    this.publishedTotal = res.total
                }
            } catch (error) {
                console.error('获取已发布列表失败:', error)
            } finally {
                this.publishedLoading = false
            }
        },

        // 设置日期范围
        setDateRange(dateRange) {
            this.publishedQuery.beginDate = dateRange ? dateRange[0] : undefined
            this.publishedQuery.endDate = dateRange ? dateRange[1] : undefined
            this.resetPublishedPage()
        },

        // 上架/下架
        async changeNewsStatus(id, enable) {
            try {
                const res = await downOrUp({ id, enable })
                if (res.code === 0) {
                    message.success(enable === 1 ? '上架成功' : '下架成功')
                    return true
                }
                return false
            } catch (error) {
                console.error(enable === 1 ? '上架失败' : '下架失败', error)
                return false
            }
        }
    }
}) 