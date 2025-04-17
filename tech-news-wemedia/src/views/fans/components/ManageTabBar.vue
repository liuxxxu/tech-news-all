<template>
  <div class="tab-bar">
    <a-tabs v-model:activeKey="activeKey" @change="handleTabChange">
      <a-tab-pane key="overview" tab="粉丝概况"></a-tab-pane>
      <a-tab-pane key="portrait" tab="粉丝画像"></a-tab-pane>
      <a-tab-pane key="list" tab="粉丝列表"></a-tab-pane>
      <a-tab-pane key="message" tab="私信管理"></a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeKey = ref('overview')

// 路由映射配置
const routeMapping = {
  'overview': '/fans',
  'portrait': '/fans/portrait',
  'list': '/fans/list',
  'message': '/fans/message'
}

// 初始化时根据当前路由设置激活的标签
onMounted(() => {
  const path = route.path
  for (const [key, routePath] of Object.entries(routeMapping)) {
    if (path === routePath) {
      activeKey.value = key
      break
    }
  }
})

// 标签切换处理
const handleTabChange = (key) => {
  // 导航到对应的路由
  router.push(routeMapping[key])
}
</script>

<style lang="scss" scoped>
.tab-bar {
  margin-bottom: 24px;
  
  :deep(.ant-tabs-nav) {
    margin-bottom: 20px;
  }
  
  :deep(.ant-tabs-tab) {
    font-size: 16px;
    padding: 12px 0;
  }
  
  :deep(.ant-tabs-tab-active) {
    .ant-tabs-tab-btn {
      font-weight: 600;
    }
  }
}
</style> 