<template>
  <div class="app-header">
    <div class="header-content">
      <div class="logo-wrapper">
        <div class="logo">
          <h1>tech-news</h1>
        </div>
      </div>
      
      <div class="right-content">
        <a-space :size="16">
          <a-tooltip title="消息">
            <a-badge :count="5" :dot="false" :offset="[6, 0]">
              <BellOutlined class="action-icon" />
            </a-badge>
          </a-tooltip>
          
          <a-dropdown trigger="click">
            <div class="user-info">
              <a-avatar :size="32" :src="userAvatar" />
              <span class="username">{{ username }}</span>
              <DownOutlined />
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <UserOutlined />
                  <span>个人中心</span>
                </a-menu-item>
                <a-menu-item key="settings">
                  <SettingOutlined />
                  <span>账户设置</span>
                </a-menu-item>
                <a-menu-divider />
                <a-menu-item key="logout" @click="handleLogout">
                  <LogoutOutlined />
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-space>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { 
  BellOutlined, 
  DownOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '@/stores'

// 侧边栏折叠状态
const props = defineProps({
  collapsed: Boolean
})

// 事件
const emit = defineEmits(['toggle'])

// 路由
const router = useRouter()

// 用户信息
const userStore = useUserStore()
const username = computed(() => userStore.userInfo?.name || '未设置昵称')
const userAvatar = computed(() => userStore.userInfo?.image)

// 退出登录
const handleLogout = async () => {
  try {
    await userStore.handleLogout()
    message.success('退出登录成功')
  } catch (error) {
    message.error('退出登录失败')
  }
}
</script>

<style lang="scss" scoped>
.app-header {
  height: 64px;
  padding: 0;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
  z-index: 10;
  
  .header-content {
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 24px;
    
    .logo-wrapper {
      display: flex;
      align-items: center;
      
      .logo {
        display: flex;
        align-items: center;
        
        h1 {
          font-size: 18px;
          font-weight: 600;
          color: rgba(0, 0, 0, 0.85);
          margin: 0;
        }
      }
    }
    
    .right-content {
      display: flex;
      align-items: center;
      
      .action-icon {
        padding: 4px;
        font-size: 16px;
        cursor: pointer;
        
        &:hover {
          color: #1890ff;
        }
      }
      
      .user-info {
        display: flex;
        align-items: center;
        padding: 0 4px;
        cursor: pointer;
        
        .username {
          margin: 0 8px;
          color: rgba(0, 0, 0, 0.65);
        }
      }
    }
  }
}
</style> 