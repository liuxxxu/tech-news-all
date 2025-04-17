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
        <van-tabs v-model:active="activeCategory" swipeable background="#fff" sticky>
          <van-tab v-for="category in categories" :key="category" :title="category"></van-tab>
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

const router = useRouter()
const userStore = useUserStore()

// 用户头像
const userAvatar = computed(() => {
  return userStore.userInfo.image || '/src/assets/portrait.png'
})

// 搜索值
const searchValue = ref('')

// 当前激活的分类
const activeCategory = ref(0)

// 下拉刷新
const refreshing = ref(false)

// 分页相关
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = ref(10)

// 分类数据
const categories = ['推荐', '热点', '科技', '财经', '体育', '娱乐', '军事', '国际']

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
const newsList = ref([
  {
    id: 1,
    title: '全球股市迎来新一轮上涨',
    description: '受多重利好因素影响，全球主要股指普遍上涨...',
    source: '财经周刊',
    time: '3小时前',
    image: 'https://s1.xiaomiev.com/activity-outer-assets/0328/images/su7/su7_1.jpg',
    views: '8.2万',
    likes: '1.5万'
  },
  {
    id: 2,
    title: '人工智能技术再获重大突破，新一代语言模型准确率提升50%',
    description: '来自全球顶尖研究机构的最新研究显示，新一代语言模型在多项测试中展现出惊人的性能，准确率相比上一代提升超过50%。专家表示，这一突破将为AI应用带来革命性变化...',
    source: '科技前沿',
    time: '1小时前',
    views: '12.8万',
    likes: '2.1万'
  },
  {
    id: 3,
    title: '2024年世界杯预选赛最新战报',
    description: '多支强队展开激烈角逐，精彩比赛不断...',
    source: '体育新闻',
    time: '4小时前',
    image: 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2',
    views: '15.3万',
    likes: '3.2万'
  },
  {
    id: 4,
    title: '央行发布重要政策信号：下半年货币政策将更加灵活精准',
    description: '央行最新货币政策例会指出，将继续实施稳健的货币政策，加大对实体经济的支持力度。专家分析认为，这意味着下半年货币政策将更具灵活性，有望为经济发展提供更有力的支持...',
    source: '经济日报',
    time: '2小时前',
    views: '9.6万',
    likes: '1.8万'
  }
])

// 获取新闻列表数据
const fetchNewsList = async (isLoadMore = false) => {
  if (loading.value || finished.value) return
  
  loading.value = true
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 模拟新的数据
    const mockMoreNews = [
      {
        id: newsList.value.length + 1,
        title: '新能源汽车产业链全面解析',
        description: '随着新能源汽车市场的快速发展，产业链上下游企业迎来新的发展机遇...',
        source: '产业观察',
        time: '5小时前',
        image: 'https://s1.xiaomiev.com/activity-outer-assets/0328/images/su7/su7_1.jpg',
        views: '7.8万',
        likes: '1.3万'
      },
      {
        id: newsList.value.length + 2,
        title: '数字化转型：传统企业的机遇与挑战',
        description: '在后疫情时代，越来越多的传统企业开始积极拥抱数字化转型...',
        source: '商业评论1',
        time: '6小时前',
        views: '6.5万',
        likes: '1.1万'
      }
    ]

    if (isLoadMore) {
      newsList.value.push(...mockMoreNews)
    } else {
      newsList.value = mockMoreNews
    }

    // 模拟数据加载完成的情况
    if (newsList.value.length >= 10) {
      finished.value = true
    }
    
    page.value += 1
  } catch (error) {
    console.error('获取新闻列表失败', error)
    showToast('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 刷新数据
const onRefresh = async () => {
  try {
    // 重置分页
    page.value = 1
    finished.value = false
    await fetchNewsList()
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
  fetchNewsList(true)
}

// 页面加载时获取数据
onMounted(async () => {
  await fetchNewsList()
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
