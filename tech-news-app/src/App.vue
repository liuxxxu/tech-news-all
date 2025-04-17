<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores'

const router = useRouter()
const userStore = useUserStore()

// 检查登录状态
onMounted(async () => {
  const token = userStore.getToken()
  if (token) {
    // 如果有token，获取用户信息
    await userStore.getUserInfo()
  }
})
</script>

<template>
  <router-view></router-view>
</template>

<style>
/* 全局样式覆盖 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  background-color: #f7f8fa;
}

/* 覆盖Vant导航栏的默认样式，确保没有额外的空间 */
.van-nav-bar {
  margin-top: 0 !important;
  padding-top: 0 !important;
}

/* 移除所有可能的默认内边距 */
#app {
  margin: 0;
  padding: 0;
}
</style>
