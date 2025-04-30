<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar fixed class="custom-nav">
      <template #left>
        <div class="avatar-search">
          <div class="avatar-wrapper" @click="goToProfile">
            <van-image
              round
              width="32"
              height="32"
              :src="userAvatar"
              alt="用户头像"
              class="avatar"
            />
          </div>
          <van-search
            v-model="searchValue"
            placeholder="搜索新闻"
            shape="round"
            class="search-input"
            background="transparent"
            readonly
            @click="goToSearch"
          />
        </div>
      </template>
      <template #right>
        <div class="notification">
          <van-badge :content="3" max="99" color="#ee0a24">
            <van-icon name="bell" size="22" color="#3478F6" />
          </van-badge>
        </div>
      </template>
    </van-nav-bar>

    <!-- 分类栏 -->
    <div class="category-bar">
      <div class="tabs-wrapper">
        <van-tabs v-model:active="activeCategory" swipeable background="#fff" sticky @change="onCategoryChange">
          <van-tab 
            v-for="category in categories" 
            :key="category.id" 
            :title="category.name"
          ></van-tab>
        </van-tabs>
      </div>
      <div class="more-btn" @click="goToCategoryManage">
        <van-icon name="bars" size="18" />
      </div>
    </div>

    <!-- 新闻列表 -->
    <div class="news-container">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          :immediate-check="false"
          @load="onLoad"
        >
          <!-- 置顶新闻 -->
          <div class="top-news" @click="goToDetail(0)">
            <div class="top-news-image">
              <van-image
                width="100%"
                height="180"
                fit="cover"
                src="https://img.pcauto.com.cn/images/upload/upc/tx/auto5/2410/31/c6/460149191_1730358944955.jpg"
                alt="新闻图片"
              />
              <span class="top-tag">置顶</span>
            </div>
            <div class="news-content">
              <h3 class="news-title">重大突破：科学家发现新型能源技术</h3>
              <div class="news-info">
                <span>科技日报</span>
                <span class="dot">·</span>
                <span>2小时前</span>
              </div>
              <div class="news-stats">
                <span><van-icon name="eye-o" /> 12.5万</span>
                <span><van-icon name="like-o" /> 2.3万</span>
              </div>
            </div>
          </div>

          <!-- 普通新闻列表 -->
          <div class="news-list">
            <div class="news-item" v-for="(item, index) in newsList" :key="index" @click="goToDetail(item.id)">
              <div class="news-item-content" :class="{ 'no-image': !item.image }">
                <div class="news-item-text">
                  <h3 class="news-title">{{ item.title }}</h3>
                  <p class="news-desc">{{ item.description }}</p>
                  <div class="news-info">
                    <span>{{ item.source }}</span>
                    <span class="dot">·</span>
                    <span>{{ item.time }}</span>
                  </div>
                </div>
                <van-image
                  v-if="item.image"
                  width="110"
                  height="80"
                  fit="cover"
                  radius="4"
                  :src="item.image"
                  alt="新闻图片"
                />
              </div>
              <div class="news-stats">
                <span><van-icon name="eye-o" /> {{ item.views }}</span>
                <span><van-icon name="like-o" /> {{ item.likes }}</span>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from '@/utils/vant-ui'
import { useUserStore } from '@/stores'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

// 用户头像
const userAvatar = computed(() => {
  return userStore.userInfo.image || '/src/assets/portrait.png'
})

// 搜索值
const searchValue = ref('')

// 分类数据
const categories = ref([])
const activeCategory = ref(0)

// 下拉刷新
const refreshing = ref(false)

// 分页相关
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = ref(10)

// 跳转到分类管理
const goToCategoryManage = () => {
  router.push('/category')
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/article/${id}`)
}

// 跳转到搜索页
const goToSearch = () => {
  router.push('/search')
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 新闻列表数据
const newsList = ref([])

// 获取分类数据
const fetchCategories = async () => {
  try {
    const response = await request.get('/article/api/channels/all', {})
    const { data } = response
    categories.value = [
      { id: 'all', name: '全部' },
      ...data.map(item => ({
        id: item.id,
        name: item.name
      }))
    ]
  } catch (error) {
    console.error('获取分类数据失败', error)
    showToast('获取分类失败')
  }
}

// 获取新闻列表数据
const fetchNewsList = async () => {
  if (loading.value) return
  
  loading.value = true
  finished.value = false
  try {
    const currentCategory = categories.value[activeCategory.value]
    const response = await request.post('/article/api/article/load', {
      tag: currentCategory?.id || ''
    })
    const { data } = response

    const formattedData = data.map(item => ({
      id: item.id,
      title: item.title,
      description: item.channelName,
      source: item.authorName,
      time: formatTime(item.publishTime),
      image: item.cover,
      views: item.views,
      likes: item.likes,
      publishTime: item.publishTime
    }))

    newsList.value = formattedData
    if (data.length < 10) {
      finished.value = true
    }
  } catch (error) {
    console.error('获取新闻列表失败', error)
    showToast('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 加载更多数据
const loadMoreData = async () => {
  if (finished.value) return
  
  try {
    const minTime = newsList.value[newsList.value.length - 1]?.publishTime
    const currentCategory = categories.value[activeCategory.value]
    const response = await request.post('/article/api/article/loadmore', {
      minTime: minTime || '',
      tag: currentCategory?.id || ''
    })
    const { data } = response

    if (data.length === 0) {
      finished.value = true
      return
    }

    const formattedData = data.map(item => ({
      id: item.id,
      title: item.title,
      description: item.channelName,
      source: item.authorName,
      time: formatTime(item.publishTime),
      image: item.cover,
      views: item.views,
      likes: item.likes,
      publishTime: item.publishTime
    }))

    newsList.value.push(...formattedData)
    
    if (data.length < 10) {
      finished.value = true
    }
  } catch (error) {
    console.error('获取更多数据失败', error)
    showToast('加载失败，请重试')
  }
}

// 加载最新数据
const loadNewData = async () => {
  try {
    const maxTime = newsList.value[0]?.publishTime
    const currentCategory = categories.value[activeCategory.value]
    const response = await request.post('/article/api/article/loadnew', {
      maxTime: maxTime || '',
      tag: currentCategory?.id || ''
    })
    const { data } = response

    const formattedData = data.map(item => ({
      id: item.id,
      title: item.title,
      description: item.channelName,
      source: item.authorName,
      time: formatTime(item.publishTime),
      image: item.cover,
      views: item.views,
      likes: item.likes,
      publishTime: item.publishTime
    }))

    if (formattedData.length > 0) {
      newsList.value = [...formattedData, ...newsList.value]
    }
    return data.length > 0
  } catch (error) {
    console.error('获取最新数据失败', error)
    showToast('刷新失败，请重试')
    return false
  }
}

// 分类切换处理
const onCategoryChange = () => {
  newsList.value = [] // 清空当前列表
  finished.value = false // 重置加载状态
  fetchNewsList() // 重新加载数据
}

// 刷新数据（下拉刷新）
const onRefresh = async () => {
  try {
    // 如果列表为空，直接获取初始数据
    if (!newsList.value.length) {
      await fetchNewsList()
      showToast('刷新成功')
      return
    }
    
    const hasNewData = await loadNewData()
    if (hasNewData) {
      showToast('刷新成功')
    } else {
      showToast('暂无新数据')
    }
  } finally {
    refreshing.value = false
  }
}

// 加载更多（上滑加载）
const onLoad = async () => {
  loading.value = true
  await loadMoreData()
  loading.value = false
}

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  if (minutes > 0) return `${minutes}分钟前`
  return '刚刚'
}

// 页面加载时获取数据
onMounted(async () => {
  loading.value = true
  await Promise.all([
    fetchCategories(),
    fetchNewsList()
  ])
  loading.value = false
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #ffffff;
  padding-bottom: 50px;
  padding-top: 102px;
}

.custom-nav {
  border-bottom: 1px solid #f0f0f0;
}

.custom-nav :deep(.van-nav-bar__content) {
  height: 56px;
  padding: 0 4px;
}

.custom-nav :deep(.van-nav-bar__left) {
  flex: 1;
  padding-left: 8px;
  padding-right: 0;
  width: calc(100% - 46px);
}

.custom-nav :deep(.van-nav-bar__right) {
  padding-right: 8px;
  min-width: 40px;
}

.avatar-wrapper {
  cursor: pointer;
  flex-shrink: 0;
}

.avatar-search {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 8px;
}

.search-input {
  flex: 1;
  min-width: 160px;
  width: 100%;
}

.search-input :deep(.van-search) {
  padding: 0;
  width: 100%;
}

.search-input :deep(.van-search__content) {
  background-color: #f5f5f5;
}

.search-input :deep(.van-field__left-icon) {
  margin-right: 4px;
}

.notification {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.category-bar {
  position: fixed;
  top: 56px;
  left: 0;
  right: 0;
  z-index: 10;
  background-color: #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
}

.tabs-wrapper {
  flex: 1;
  overflow-x: auto;
}

.tabs-wrapper :deep(.van-tabs__nav) {
  display: flex;
  flex-direction: row;
}

.more-btn {
  padding: 0 12px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  z-index: 11;
}

.news-container {
  padding: 16px;
}

.top-news {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.top-news-image {
  position: relative;
}

.top-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background-color: #ee0a24;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.news-content {
  padding: 12px;
}

.news-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  line-height: 1.4;
  color: #333;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.news-info {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.dot {
  margin: 0 4px;
}

.news-stats {
  display: flex;
  align-items: center;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.news-stats span {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.news-stats .van-icon {
  margin-right: 2px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.news-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.news-item-content {
  display: flex;
  justify-content: space-between;
}

.news-item-content.no-image .news-item-text {
  flex: 1;
  margin-right: 0;
}

.news-item-text {
  flex: 1;
  margin-right: 10px;
}

.news-desc {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
</style>
