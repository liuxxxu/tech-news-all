import { Modal } from 'ant-design-vue'

export default {
    methods: {
        // 返回上级
        goBackPage() {
            this.$router.go(-1)
        },

        // 删除确认弹窗
        async showDeleteConfirm(msg) {
            return new Promise((resolve, reject) => {
                Modal.confirm({
                    title: '删除',
                    content: `删除后${msg}将不可找回，确认将${msg}删除吗？`,
                    okText: '删除',
                    okType: 'danger',
                    cancelText: '取消',
                    onOk() {
                        resolve()
                    },
                    onCancel() {
                        reject(new Error('取消删除'))
                    }
                })
            })
        },

        // 退出登录确认弹窗
        async showLogoutConfirm() {
            return new Promise((resolve, reject) => {
                Modal.confirm({
                    title: '温馨提示',
                    content: '确认要退出登录吗？',
                    okText: '确定',
                    okType: 'danger',
                    cancelText: '取消',
                    onOk() {
                        resolve()
                    },
                    onCancel() {
                        reject(new Error('取消退出'))
                    }
                })
            })
        }
    }
} 