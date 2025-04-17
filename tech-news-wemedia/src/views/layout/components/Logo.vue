<template>
  <div class="logo-container" :class="{ collapsed }">
    <FireOutlined class="logo-icon" />
    <h1 v-show="!collapsed">tech-news</h1>
  </div>
</template>

<script setup>
import { FireOutlined } from '@ant-design/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
.logo-container {
  height: 64px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  overflow: hidden;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  transition: all 0.3s;
  
  &.collapsed {
    padding: 0 8px;
    justify-content: center;
  }
  
  .logo-icon {
    font-size: 24px;
    margin-right: 8px;
    color: #1890ff;
    transition: all 0.3s;
  }
  
  h1 {
    color: rgba(0, 0, 0, 0.85);
    font-size: 18px;
    margin: 0;
    white-space: nowrap;
    transition: opacity 0.3s;
  }
}
</style> 