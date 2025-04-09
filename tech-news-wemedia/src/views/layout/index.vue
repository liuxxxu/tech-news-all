<template>
  <a-layout class="layout">
    <!-- 侧边栏 -->
    <a-layout-sider 
      v-model:collapsed="collapsed" 
      :trigger="null" 
      collapsible 
      width="200"
      class="layout-sider"
    >
      <logo :collapsed="collapsed" />
      <side-menu :collapsed="collapsed" />
      <div class="collapse-trigger" @click="toggleCollapsed">
        <MenuFoldOutlined v-if="!collapsed" />
        <MenuUnfoldOutlined v-else />
      </div>
    </a-layout-sider>
    
    <a-layout>
      <!-- 头部 -->
      <a-layout-header class="layout-header">
        <app-header :collapsed="collapsed" @toggle="toggleCollapsed" />
      </a-layout-header>
      
      <!-- 内容区 -->
      <a-layout-content class="layout-content">
        <!-- <div class="content-container"> -->
          <router-view />
        <!-- </div> -->
      </a-layout-content>
      
      <!-- 底部 -->
      <a-layout-footer class="layout-footer">
        tech-news 2025 Created by LiuXu
      </a-layout-footer>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import AppHeader from './components/AppHeader.vue'
import SideMenu from './components/SideMenu.vue'
import Logo from './components/Logo.vue'
import { MenuFoldOutlined, MenuUnfoldOutlined } from '@ant-design/icons-vue'

// 侧边栏折叠状态
const collapsed = ref(false)

// 切换侧边栏折叠状态
const toggleCollapsed = () => {
  collapsed.value = !collapsed.value
}
</script>

<style lang="scss" scoped>
.layout {
  height: 100vh;
  width: 100%;
  display: flex;
  overflow: hidden;
  
  .layout-sider {
    overflow: hidden;
    height: 100%;
    position: relative;
    background-color: #fff;
    border-right: 1px solid #f0f0f0;
    
    .collapse-trigger {
      position: absolute;
      bottom: 0;
      width: 100%;
      height: 48px;
      display: flex;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      color: rgba(0, 0, 0, 0.85);
      background-color: #fff;
      font-size: 16px;
      border-top: 1px solid #f0f0f0;
      
      &:hover {
        background-color: #f5f5f5;
      }
    }
  }
  
  .layout-header {
    padding: 0;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  }
  
  .layout-content {
    flex: 1;
    margin: 16px;
    overflow: auto;
    
    // .content-container {
    //   padding: 24px;
    //   background: #fff;
    //   min-height: 280px;
    //   height: 100%;
    // }
  }
  
  .layout-footer {
    text-align: center;
    padding: 16px;
    color: rgba(0, 0, 0, 0.45);
    background: #f0f2f5;
  }
}
</style> 