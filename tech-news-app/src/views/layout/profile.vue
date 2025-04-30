<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar fixed class="custom-nav">
      <template #left>
        <van-icon name="arrow-left" size="20" color="#3478F6" @click="onBack" class="back-icon" />
      </template>
    </van-nav-bar>

    <!-- 用户信息区域 -->
    <div class="user-info">
      <div class="avatar-info">
        <van-image
          round
          width="72"
          height="72"
          :src="userInfo.avatar"
          alt="用户头像"
        />
        <div class="user-details">
          <h2 class="user-name">{{ userInfo.username }}</h2>
          <p class="user-tag">{{ userInfo.userTag }}</p>
        </div>
        <div class="space-btn" @click="goToSpace">
          <van-button round type="primary" size="small">我的空间</van-button>
        </div>
      </div>
      
      <!-- 数据统计 -->
      <div class="user-stats">
        <div class="stat-item" @click="goToSpace">
          <!-- <span class="count">{{ userInfo.dynamicCount }}</span> -->
          <span class="count"> 2 </span>
          <span class="label">动态</span>
        </div>
        <div class="stat-item" @click="goToFollows(0)">
          <span class="count">{{ userInfo.followCount }}</span>
          <span class="label">关注</span>
        </div>
        <div class="stat-item" @click="goToFollows(1)">
          <span class="count">{{ userInfo.fansCount }}</span>
          <span class="label">粉丝</span>
        </div>
      </div>
    </div>

    <!-- 成为自媒体 -->
    <div class="media-card-wrap">
      <div class="media-card">
        <div class="media-info">
          <h3 class="media-title">成为自媒体</h3>
          <p class="media-desc">发布原创内容，获得更多关注</p>
        </div>
        <van-button round type="default" size="small" class="media-btn" @click="goToMediaCert">
          立即认证
        </van-button>
      </div>
    </div>

    <!-- 功能列表 -->
    <div class="feature-block">
      <!-- 功能图标区域 -->
      <van-grid :column-num="4" :border="false" icon-size="26">
        <van-grid-item icon="clock" text="历史记录" />
        <van-grid-item icon="like" text="我的点赞" />
        <van-grid-item icon="comment" text="我的评论" />
        <van-grid-item icon="star" text="我的收藏" />
      </van-grid>
    </div>
    
    <!-- 设置列表 -->
    <div class="settings-block">
      <van-cell-group>
        <van-cell title="账号资料" icon="contact" is-link @click="goToUserProfile" />
        <van-cell title="修改密码" icon="lock" is-link @click="goToResetPassword" />
        <van-cell title="帮助与反馈" icon="question" is-link @click="showHelp" />
        <van-cell title="关于我们" icon="info" is-link @click="showAbout" />
      </van-cell-group>
    </div>

    <!-- 退出登录按钮 -->
    <div class="logout-button">
      <van-button block type="danger" native-type="button" @click="handleLogout">
        退出登录
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showToast, showSuccessToast, showLoadingToast, closeToast } from '../../utils/vant-ui'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'
import { logout } from '@/api/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()
const fileInput = ref(null)

// 用户统计数据
const counts = ref({
  followCount: 0,
  fansCount: 0,
  dynamicCount: 0
})

// 用户信息数据
const userInfo = computed(() => {
  const user = userStore.userInfo
  return {
    dynamicCount: counts.value.dynamicCount || 0,
    followCount: counts.value.followCount || 0,
    fansCount: counts.value.fansCount || 0,
    avatar: user.image || '/src/assets/portrait.png',
    username: user.name || '未设置昵称',
    userTag: user.identityAuthentication ? '已成为自媒体' : '未认证自媒体'
  }
})

// 获取用户统计数据
const fetchUserCounts = async () => {
  try {
    const res = await request.get('user/api/user/count')
    if (res.code === 0) {
      counts.value = {
        ...counts.value,
        ...res.data
      }
    }
  } catch (error) {
    console.error('获取用户统计数据失败', error)
  }
}

// 返回上一页
const onBack = () => {
  router.back()
}

// 跳转到自媒体认证页面
const goToMediaCert = () => {
  router.push('/media-cert')
}

// 跳转到账号资料页面
const goToUserProfile = () => {
  router.push('/user-profile')
}

// 跳转到修改密码页面
const goToResetPassword = () => {
  router.push('/reset-password')
}

// 显示帮助与反馈
const showHelp = () => {
  showToast('您可以通过邮箱feedback@technews.com联系我们，我们会在1-3个工作日内回复您的问题')
}

// 显示关于我们
const showAbout = () => {
  showToast('科技新闻平台 - 您身边的科技资讯平台，为您提供最新最热的科技动态')
}

// 处理退出登录逻辑
const handleLogout = async () => {
  try {
    await logout()
    showSuccessToast('退出登录成功')
    userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}

// 跳转到个人空间
const goToSpace = () => {
  router.push('/space')
}

// 跳转到关注/粉丝页面
const goToFollows = (tabIndex) => {
  router.push({
    name: 'follows',
    query: { tab: tabIndex }
  })
}

// 页面加载时获取用户统计数据
onMounted(() => {
  if (userStore.userInfo.id) {
    fetchUserCounts()
  }
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  background-color: #ffffff;
  padding-bottom: 20px;
}

.custom-nav {
  border-bottom: 1px solid #f0f0f0;
}

.back-icon {
  padding: 4px;
}

.user-info {
  margin-top: 46px;
  background-color: #fff;
  padding: 16px;
}

.avatar-info {
  display: flex;
  align-items: center;
  padding-top: 4px;
  position: relative;
}

.user-details {
  margin-left: 16px;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.user-tag {
  font-size: 14px;
  color: #999;
}

.user-space {
  position: absolute;
  right: 0;
  top: 10px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #3478F6;
}

.user-space span {
  margin-right: 4px;
}

.user-stats {
  display: flex;
  padding: 16px 0;
  margin-top: 16px;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  flex: 1;
  text-align: center;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.count {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.label {
  font-size: 12px;
  color: #999;
}

.media-card-wrap {
  padding: 0 16px;
  margin-top: 16px;
}

.media-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(to right, #3478F6, #5B9DF8);
  border-radius: 8px;
  padding: 16px;
}

.media-title {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.media-desc {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  margin-top: 4px;
}

.media-btn {
  background-color: #fff;
  color: #3478F6;
  font-weight: 500;
  border: none;
}

.feature-block, .settings-block {
  margin-top: 16px;
  background-color: #fff;
}

.logout-button {
  padding: 16px;
  margin-top: 16px;
}

:deep(.van-cell__left-icon) {
  font-size: 18px;
  margin-right: 8px;
  color: #666;
}

.space-btn {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.space-btn :deep(.van-button) {
  height: 32px;
  font-size: 14px;
  padding: 0 16px;
  background-color: #3478F6;
  border: none;
}
</style>
