<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <h2>抱歉，您访问的页面不存在</h2>
      <a-button type="primary" @click="goHome">返回首页</a-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.content {
  text-align: center;
}

h1 {
  font-size: 72px;
  color: #1890ff;
  margin-bottom: 24px;
}

h2 {
  font-size: 20px;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 24px;
}
</style> 