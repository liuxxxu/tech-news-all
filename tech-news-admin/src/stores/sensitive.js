import { defineStore } from 'pinia'
import { message } from 'ant-design-vue'
import { loadList, saveData, updateData, deleteData } from '@/api/sensitive'

export const useSensitiveStore = defineStore('sensitive', {
    state: () => ({
        list: [],
        total: 0,
        loading: false,
        query: {
            name: undefined,
            page: 1,
            size: 10
        }
    }),

    actions: {
        // 重置分页
        resetPage() {
            this.query.page = 1
            this.getList()
        },

        // 获取敏感词列表
        async getList() {
            if (this.query.name === '') {
                this.query.name = undefined
            }

            this.loading = true

            try {
                const res = await loadList(this.query)
                if (res.code === 0) {
                    this.list = res.data
                    this.total = res.total
                } else {
                    message.error(res.errorMessage || '获取敏感词列表失败')
                }
            } catch (error) {
                console.error('获取敏感词列表失败:', error)
                message.error('获取敏感词列表失败')
            } finally {
                this.loading = false
            }
        },

        // 保存敏感词
        async saveSensitive(data) {
            try {
                const res = await saveData(data)
                if (res.code === 0) {
                    message.success('保存成功')
                    this.getList()
                    return true
                } else {
                    message.error(res.errorMessage || '保存失败')
                    return false
                }
            } catch (error) {
                console.error('保存敏感词失败:', error)
                message.error('保存敏感词失败')
                return false
            }
        },

        // 更新敏感词
        async updateSensitive(data) {
            try {
                const res = await updateData(data)
                if (res.code === 0) {
                    message.success('更新成功')
                    this.getList()
                    return true
                } else {
                    message.error(res.errorMessage || '更新失败')
                    return false
                }
            } catch (error) {
                console.error('更新敏感词失败:', error)
                message.error('更新敏感词失败')
                return false
            }
        },

        // 删除敏感词
        async deleteSensitive(id) {
            try {
                const res = await deleteData(id)
                if (res.code === 0) {
                    message.success('删除成功')
                    this.getList()
                    return true
                } else {
                    message.error(res.errorMessage || '删除失败')
                    return false
                }
            } catch (error) {
                console.error('删除敏感词失败:', error)
                message.error('删除敏感词失败')
                return false
            }
        }
    }
}) 