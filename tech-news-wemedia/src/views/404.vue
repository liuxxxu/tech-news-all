<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在</p>
      <a-button type="primary" @click="goBack">返回上一页</a-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
  
  .not-found-content {
    text-align: center;
    padding: 40px;
    
    h1 {
      font-size: 80px;
      margin-bottom: 0;
      color: #1890ff;
    }
    
    h2 {
      font-size: 32px;
      margin-top: 0;
      margin-bottom: 20px;
    }
    
    p {
      font-size: 16px;
      color: #666;
      margin-bottom: 30px;
    }
  }
}
</style>