<template>
  <div class="layout">
    <!-- 内容区域 -->
    <div class="content">
      <router-view></router-view>
    </div>

    <!-- 底部标签栏 -->
    <div class="tab-bar">
      <van-tabbar v-model="active" route>
        <van-tabbar-item to="/home" icon="home-o">首页</van-tabbar-item>
        <van-tabbar-item to="/dynamic" icon="fire-o">动态</van-tabbar-item>
        <van-tabbar-item to="/profile" icon="user-o">我的</van-tabbar-item>
      </van-tabbar>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const active = ref(0);
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background-color: #f7f8fa;
  display: flex;
  flex-direction: column;
}

.content {
  flex: 1;
  overflow-y: auto;
}

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background-color: #fff;
  border-top: 1px solid #f5f5f5;
}
</style>
