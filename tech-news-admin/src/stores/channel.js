import { defineStore } from 'pinia'
import { loadList, saveData, updateData, delData } from '@/api/channel'
import { message } from 'ant-design-vue'

export const useChannelStore = defineStore('channel', {
    state: () => ({
        list: [],
        total: 0,
        loading: false,
        query: {
            name: undefined,
            status: undefined,
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

        // 获取频道列表
        async getList() {
            // 处理清空时值为空字符串的情况
            if (this.query.name === '') {
                this.query.name = undefined
            }
            if (this.query.status === '') {
                this.query.status = undefined
            }

            this.loading = true

            try {
                const res = await loadList(this.query)
                if (res.code === 0) {
                    this.list = res.data
                    this.total = res.total
                } else {
                    message.error(res.errorMessage || '加载失败')
                }
            } catch (error) {
                console.error('获取频道列表失败:', error)
            } finally {
                this.loading = false
            }
        },

        // 保存频道
        async saveChannel(data) {
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
                console.error('保存频道失败:', error)
                message.error('保存频道失败')
                return false
            }
        },

        // 更新频道
        async updateChannel(data) {
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
                console.error('更新频道失败:', error)
                message.error('更新频道失败')
                return false
            }
        },

        // 更新频道状态
        async updateChannelStatus(id, name, status) {
            return this.updateChannel({ id, name, status })
        },

        // 删除频道
        async deleteChannel(id) {
            try {
                const res = await delData(id)
                if (res.code === 0) {
                    message.success('删除成功')
                    this.getList()
                    return true
                } else {
                    message.error(res.errorMessage || '删除失败')
                    return false
                }
            } catch (error) {
                console.error('删除频道失败:', error)
                return false
            }
        }
    }
}) 