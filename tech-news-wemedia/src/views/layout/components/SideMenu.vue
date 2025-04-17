<template>
  <div class="side-menu">
    <a-menu
      v-model:selectedKeys="selectedKeys"
      v-model:openKeys="openKeys"
      mode="inline"
      theme="light"
      :inline-collapsed="collapsed"
    >
      <!-- 遍历路由生成菜单项 -->
      <template v-for="route in menuRoutes" :key="route.path">
        <!-- 没有子路由的菜单项 -->
        <template v-if="!hasChildren(route)">
          <a-menu-item :key="route.name" @click="() => handleMenuClick(route.path)">
            <template #icon>
              <component :is="getIcon(route.name)" v-if="getIcon(route.name)" />
            </template>
            <span>{{ route.meta?.title || route.name }}</span>
          </a-menu-item>
        </template>
        
        <!-- 有子路由的菜单项 -->
        <template v-else>
          <a-sub-menu :key="route.name">
            <template #icon>
              <component :is="getIcon(route.name)" v-if="getIcon(route.name)" />
            </template>
            <template #title>{{ route.meta?.title || route.name }}</template>
            
            <!-- 子菜单项 -->
            <a-menu-item 
              v-for="child in route.children" 
              :key="child.name"
              @click="() => handleMenuClick(`${route.path}/${child.path}`)"
            >
              <span>{{ child.meta?.title || child.name }}</span>
            </a-menu-item>
          </a-sub-menu>
        </template>
      </template>
    </a-menu>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  HomeOutlined, 
  ReadOutlined, 
  BarChartOutlined, 
  TeamOutlined, 
  MessageOutlined,
  FileImageOutlined
} from '@ant-design/icons-vue'

// 侧边栏折叠状态
const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

// 当前路由
const route = useRoute()
const router = useRouter()

// 选中的菜单项和展开的子菜单
const selectedKeys = ref([])
const openKeys = ref([])

// 获取可用于菜单的路由（排除login和404）
const menuRoutes = computed(() => {
  // 获取根路由的子路由（即 '/' 路径下的子路由）
  const mainRoute = router.options.routes.find(route => route.path === '/')
  if (!mainRoute || !mainRoute.children) return []
  
  // 过滤掉 hidden 的路由
  return mainRoute.children.map(route => {
    if (route.children) {
      return {
        ...route,
        children: route.children.filter(child => !child.meta?.hidden)
      }
    }
    return route
  })
})

// 判断是否有子路由
const hasChildren = (route) => {
  return route.children && route.children.length > 0
}

// 获取菜单图标
const getIcon = (routeName) => {
  const iconMap = {
    'home': HomeOutlined,
    'content': ReadOutlined,
    'material': FileImageOutlined,
    'statistics': BarChartOutlined,
    'fans': TeamOutlined,
    'comment': MessageOutlined
  }
  return iconMap[routeName]
}

// 处理菜单点击
const handleMenuClick = (path) => {
  // 确保路径以 / 开头
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  // 处理可能的多余斜杠
  const cleanPath = normalizedPath.replace(/\/+/g, '/')
  router.push(cleanPath)
}

// 监听路由变化
watch(() => route.path, () => {
  // 只更新选中的菜单项
  const matched = route.matched
  if (matched.length > 0) {
    const currentRoute = matched[matched.length - 1]
    selectedKeys.value = [currentRoute.name]
    
    // 如果有父路由且当前未展开，则添加到展开列表中
    if (matched.length > 1 && !props.collapsed) {
      const parentName = matched[matched.length - 2].name
      if (!openKeys.value.includes(parentName)) {
        openKeys.value = [...openKeys.value, parentName]
      }
    }
  }
}, { immediate: true })

// 当折叠状态改变时更新openKeys
watch(() => props.collapsed, (collapsed) => {
  if (collapsed) {
    openKeys.value = []
  } else {
    // 恢复展开状态，如果有匹配的父路由则确保它被展开
    const matched = route.matched
    if (matched.length > 1) {
      const parentName = matched[matched.length - 2].name
      if (!openKeys.value.includes(parentName)) {
        openKeys.value = [...openKeys.value, parentName]
      }
    }
  }
})
</script>

<style lang="scss" scoped>
.side-menu {
  height: calc(100% - 64px - 48px);
  overflow-y: auto;
  background-color: #fff;
  
  :deep(.ant-menu) {
    height: 100%;
    border-right: 0;
  }
  
  :deep(.ant-menu-item), :deep(.ant-menu-submenu-title) {
    display: flex;
    align-items: center;
  }
}
</style> 