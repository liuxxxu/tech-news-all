<template>
  <div class="user-profile">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      title="账号资料"
      left-arrow
      @click-left="onClickLeft"
      fixed
    />

    <!-- 用户资料表单 -->
    <div class="profile-content">
      <!-- 头像 -->
      <van-cell center title="头像" class="avatar-cell">
        <template #value>
          <van-image
            round
            width="50"
            height="50"
            :src="userInfo.image || '/src/assets/portrait.png'"
          />
        </template>
      </van-cell>

      <!-- 基本信息 -->
      <van-cell-group inset>
        <van-cell title="用户名" :value="userInfo.name || '未设置'" />
        <van-cell title="手机号" :value="userInfo.phone || '未绑定'" />
        <van-cell title="性别" :value="formatGender(userInfo.sex)" />
        <van-cell title="注册时间" :value="formatDate(userInfo.createdTime)" />
        <van-cell title="认证状态" :value="userInfo.identityAuthentication ? '已认证' : '未认证'" />
      </van-cell-group>

      <!-- 数据统计 -->
      <van-cell-group inset class="stat-group">
        <van-cell title="关注数" :value="userInfo.followCount + ''" />
        <van-cell title="粉丝数" :value="userInfo.fansCount + ''" />
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'

const router = useRouter()
const userStore = useUserStore()

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 返回上一页
const onClickLeft = () => {
  router.back()
}

// 格式化性别
const formatGender = (sex) => {
  if (sex === 0) return '男'
  if (sex === 1) return '女'
  return '未设置'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.user-profile {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
}

.profile-content {
  padding: 16px 0;
}

.avatar-cell {
  margin-bottom: 12px;
}

.stat-group {
  margin-top: 12px;
}

:deep(.van-cell__title) {
  flex: none;
  width: 80px;
  color: #666;
}

:deep(.van-cell__value) {
  color: #333;
  text-align: left;
}
</style> 