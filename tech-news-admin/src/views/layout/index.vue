<template>
  <a-layout class="layout-container">
    <a-layout-header class="header">
      <div class="logo">tech-news 管理系统</div>
      <div class="user-info">
        <!-- <span class="welcome">欢迎，{{ userStore.username }}</span> -->
        <span class="welcome">欢迎，管理员</span>
        <a-button type="link" @click="handleLogout">退出</a-button>
      </div>
    </a-layout-header>
    <a-layout>
      <a-layout-sider width="200" style="background: #fff">
        <a-menu
          mode="inline"
          :selectedKeys="[selectedKey]"
          style="height: 100%"
          @select="handleMenuSelect"
        >
          <a-menu-item key="home">
            <template #icon><HomeOutlined /></template>
            <span>首页</span>
          </a-menu-item>
          <a-menu-item key="channel">
            <template #icon><AppstoreOutlined /></template>
            <span>频道管理</span>
          </a-menu-item>
          <a-menu-item key="news">
            <template #icon><AuditOutlined /></template>
            <span>内容审核</span>
          </a-menu-item>
          <a-menu-item key="news-published">
            <template #icon><FileTextOutlined /></template>
            <span>内容管理</span>
          </a-menu-item>
          <!-- <a-menu-item key="sensitive">
            <template #icon><AlertOutlined /></template>
            <span>敏感词管理</span>
          </a-menu-item> -->
          <a-menu-item key="auth">
            <template #icon><IdcardOutlined /></template>
            <span>实名认证</span>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { HomeOutlined, AppstoreOutlined, AuditOutlined, FileTextOutlined, AlertOutlined, IdcardOutlined } from '@ant-design/icons-vue'
import { Modal, message } from 'ant-design-vue'
import { useUserStore } from '@/stores'
import { logout } from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 当前选中的菜单项
const selectedKey = ref('home')

// 更新选中的菜单项函数
const updateSelectedKey = () => {
  const path = route.path.substring(1) // 去掉前导斜杠
  
  // 处理详情页路径映射到列表页
  if (path === 'news/detail') {
    selectedKey.value = 'news'
    return
  } else if (path === 'news-published/detail') {
    selectedKey.value = 'news-published'
    return
  } else if (path === 'auth/detail') {
    selectedKey.value = 'auth'
    return
  }
  
  // 处理其他路径
  if (path.includes('/')) {
    // 提取主路径部分
    selectedKey.value = path.split('/')[0]
  } else {
    selectedKey.value = path || 'home'
  }
}

// 监听路由变化，更新选中的菜单项
onMounted(() => {
  updateSelectedKey()
})

// 监听路由变化
watch(() => route.path, () => {
  updateSelectedKey()
}, { immediate: true })

// 菜单选择处理
const handleMenuSelect = ({ key }) => {
  router.push(`/${key}`)
}

// 退出登录
const handleLogout = () => {
  Modal.confirm({
    title: '提示',
    content: '确定要退出登录吗？',
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      try {
        await logout()
        message.success('退出登录成功')
        userStore.logout()
        router.push('/login')
      } catch (error) {
        console.error('退出登录失败:', error)
      }
    }
  })
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  padding: 0 20px;
}

.logo {
  color: white;
  font-size: 18px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
}

.welcome {
  margin-right: 10px;
}

.content {
  padding: 20px;
  min-height: 280px;
  background: #f0f2f5;
}
</style> 