<template>
  <div class="space-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-arrow
      @click-left="onBack"
      fixed
      left-arrow-color="#3478F6"
    >
      <template #right>
        <van-icon name="plus" size="22" color="#3478F6" @click="handleNewPost" />
      </template>
    </van-nav-bar>

    <!-- 动态列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div class="dynamic-list">
          <!-- 动态项 -->
          <div class="dynamic-item" v-for="(item, index) in dynamicList" :key="index">
            <!-- 用户信息 -->
            <div class="user-info">
              <van-image
                width="42"
                height="42"
                round
                :src="item.avatar"
                alt="用户头像"
              />
              <div class="user-detail">
                <div class="username">{{ item.username }}</div>
                <div class="timestamp">{{ item.time }}</div>
              </div>
            </div>

            <!-- 动态内容 -->
            <div class="dynamic-content">
              <div class="dynamic-text">{{ item.content }}</div>
              
              <!-- 新闻卡片（可点击进入详情） -->
              <div class="news-card" v-if="item.news" @click="goToDetail(item.news.id)">
                <div class="news-card-content">
                  <div class="news-title">{{ item.news.title }}</div>
                  <div class="news-source">{{ item.news.source }}</div>
                </div>
                <van-image
                  width="80"
                  height="80"
                  fit="cover"
                  radius="4"
                  :src="item.news.image"
                  alt="新闻图片"
                  v-if="item.news.image"
                />
              </div>
              
              <!-- 图片（如果有） -->
              <div class="image-container" v-if="item.images && item.images.length > 0">
                <van-image
                  v-for="(img, imgIndex) in item.images"
                  :key="imgIndex"
                  width="33%"
                  fit="cover"
                  :src="img"
                  alt="动态图片"
                />
              </div>
            </div>

            <!-- 互动栏 -->
            <div class="interaction-bar">
              <div class="interaction-item" @click="handleLike(item)">
                <van-icon :name="item.isLiked ? 'like' : 'like-o'" size="18" :class="{ 'liked': item.isLiked }" /> 
                {{ item.likes }}
              </div>
              <div class="interaction-item" @click="handleComment(item)">
                <van-icon name="chat-o" size="18" /> {{ item.comments }}
              </div>
              <div class="interaction-item" @click="handleShare(item)">
                <van-icon name="share-o" size="18" /> {{ item.shares }}
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from '@/utils/vant-ui'

const router = useRouter()

// 分页相关
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)
const pageSize = ref(10)

// 动态列表数据
const dynamicList = ref([
  {
    id: 1,
    username: '张三',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e',
    time: '2小时前',
    content: '分享一篇关于新能源技术的报道，这项突破可能改变未来能源格局！',
    news: {
      id: 1,
      title: '重大突破：科学家发现新型能源技术',
      source: '科技日报',
      image: 'https://images.unsplash.com/photo-1495020689067-958852a7765e'
    },
    images: [],
    likes: 256,
    comments: 32,
    shares: 18,
    isLiked: false
  },
  {
    id: 2,
    username: '张三',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e',
    time: '昨天',
    content: '周末去了城郊的新开发区，拍到了这些美丽的风景，分享给大家！',
    news: null,
    images: [
      'https://images.unsplash.com/photo-1516483638261-f4dbaf036963',
      'https://images.unsplash.com/photo-1523906834658-6e24ef2386f9',
      'https://images.unsplash.com/photo-1528127269322-539801943592'
    ],
    likes: 312,
    comments: 45,
    shares: 23,
    isLiked: false
  }
])

// 返回上一页
const onBack = () => {
  router.back()
}

// 跳转到发布页面
const handleNewPost = () => {
  router.push('/post')
}

// 跳转到文章详情
const goToDetail = (id) => {
  router.push(`/article/${id}`)
}

// 点赞
const handleLike = (item) => {
  item.isLiked = !item.isLiked
  item.likes += item.isLiked ? 1 : -1
}

// 评论
const handleComment = (item) => {
  showToast('评论功能开发中')
}

// 分享
const handleShare = (item) => {
  showToast('分享功能开发中')
}

// 获取动态列表
const fetchDynamicList = async (isLoadMore = false) => {
  if (loading.value || finished.value) return
  
  loading.value = true
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 模拟新的动态数据
    const mockMoreData = [
      {
        id: dynamicList.value.length + 1,
        username: '张三',
        avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e',
        time: '3天前',
        content: '最近在研究新的技术方向，感觉人工智能领域发展真的太快了！',
        news: null,
        images: [],
        likes: 156,
        comments: 28,
        shares: 15,
        isLiked: false
      }
    ]

    if (isLoadMore) {
      dynamicList.value.push(...mockMoreData)
    }

    // 判断是否加载完所有数据
    if (dynamicList.value.length >= 10) {
      finished.value = true
    }
    
    page.value += 1
  } catch (error) {
    console.error('获取动态列表失败', error)
    showToast('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 刷新
const onRefresh = async () => {
  try {
    // 重置分页
    page.value = 1
    finished.value = false
    await fetchDynamicList()
    showToast('刷新成功')
  } catch (error) {
    console.error('刷新失败', error)
    showToast('刷新失败，请重试')
  } finally {
    refreshing.value = false
  }
}

// 加载更多
const onLoad = () => {
  fetchDynamicList(true)
}

// 页面加载时获取数据
onMounted(async () => {
  await fetchDynamicList()
})
</script>

<style scoped>
.space-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
  padding-bottom: 50px;
}

.dynamic-list {
  padding: 12px;
}

.dynamic-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-detail {
  flex: 1;
  margin-left: 12px;
}

.username {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.timestamp {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.dynamic-content {
  margin-bottom: 16px;
}

.dynamic-text {
  font-size: 15px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 12px;
}

.news-card {
  display: flex;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  align-items: center;
  cursor: pointer;
}

.news-card-content {
  flex: 1;
  margin-right: 12px;
}

.news-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 6px;
  font-weight: 500;
  line-height: 1.4;
}

.news-source {
  font-size: 12px;
  color: #999;
}

.image-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 12px;
}

.interaction-bar {
  display: flex;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.interaction-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.interaction-item .van-icon {
  transition: all 0.3s;
}

.interaction-item .van-icon.liked {
  color: #ee0a24;
}
</style> 