<template>
  <div class="search-result-container">
    <!-- 顶部搜索栏 -->
    <div class="search-header">
      <van-search
        v-model="keyword"
        placeholder="请输入搜索关键词"
        background="#ffffff"
        shape="round"
        show-action
        @search="onSearch"
        @cancel="onCancel"
        clearable
      >
        <template #left-icon>
          <van-icon name="arrow-left" class="back-icon" @click="onBack" />
        </template>
      </van-search>
    </div>

    <!-- 内容选项栏 -->
    <div class="content-tabs">
      <van-tabs v-model:active="activeTab" animated swipeable>
        <van-tab title="资讯">
          <!-- 筛选条件栏 -->
          <div class="filter-bar">
            <div class="filter-item" @click="showSortOptions">
              <span>{{ currentSort }}</span>
              <van-icon name="arrow-down" :class="{ 'arrow-up': showSortPopup }" />
            </div>
            <div class="filter-item" @click="showCategoryOptions">
              <span>{{ currentCategory }}</span>
              <van-icon name="arrow-down" :class="{ 'arrow-up': showCategoryPopup }" />
            </div>
          </div>

          <!-- 资讯列表 -->
          <div class="news-list" v-if="newsList.length > 0">
            <div class="news-item" v-for="(item, index) in newsList" :key="index" @click="viewNewsDetail(item)">
              <div class="news-content">
                <h3 class="news-title">{{ item.title }}</h3>
                <div class="news-info">
                  <span class="source">{{ item.source }}</span>
                  <span class="time">{{ item.time }}</span>
                  <span class="comment-count">{{ item.comments }}评论</span>
                </div>
              </div>
              <div class="news-image" v-if="item.cover">
                <van-image
                  :src="item.cover"
                  fit="cover"
                  radius="4"
                  width="110"
                  height="70"
                />
              </div>
            </div>
            
            <!-- 加载更多 -->
            <van-loading v-if="loading" />
            <van-divider v-if="finished">没有更多了</van-divider>
          </div>

          <!-- 空状态 -->
          <div class="empty-state" v-else>
            <van-empty
              image="search"
              description="没有找到与搜索词相关的内容"
            />
          </div>
        </van-tab>

        <van-tab title="用户">
          <!-- 用户筛选条件栏 -->
          <div class="filter-bar">
            <div class="filter-item" @click="showUserSortOptions">
              <span>{{ currentUserSort }}</span>
              <van-icon name="arrow-down" :class="{ 'arrow-up': showUserSortPopup }" />
            </div>
            <div class="filter-item" @click="showUserTypeOptions">
              <span>{{ currentUserType }}</span>
              <van-icon name="arrow-down" :class="{ 'arrow-up': showUserTypePopup }" />
            </div>
          </div>

          <!-- 用户列表 -->
          <div class="user-list" v-if="filteredUserList.length > 0">
            <div class="user-item" v-for="(item, index) in filteredUserList" :key="index">
              <van-image
                round
                width="50"
                height="50"
                :src="item.avatar"
                alt="用户头像"
              />
              <div class="user-info">
                <div class="user-name">{{ item.name }}</div>
                <div class="user-desc">{{ item.description }}</div>
                <div class="user-fans">{{ item.followers }}粉丝</div>
              </div>
              <van-button 
                size="small" 
                color="#3478F6" 
                round 
                plain
                class="follow-btn"
              >
                关注
              </van-button>
            </div>
          </div>

          <!-- 空状态 -->
          <div class="empty-state" v-else>
            <van-empty
              image="search"
              description="没有找到与搜索词相关的用户"
            />
          </div>
        </van-tab>
      </van-tabs>
    </div>

    <!-- 遮罩层 - 仅当弹出层显示时才显示 -->
    <div class="global-mask" v-show="showSortPopup || showCategoryPopup || showUserSortPopup || showUserTypePopup" @click="closeAllPopups"></div>

    <!-- 筛选弹出层 - 排序 -->
    <div class="filter-popup" v-show="showSortPopup">
      <div class="popup-content">
        <div 
          class="popup-item" 
          v-for="(item, index) in sortOptions" 
          :key="index"
          :class="{ active: currentSort === item }"
          @click="selectSort(item)"
        >
          {{ item }}
        </div>
      </div>
    </div>

    <!-- 筛选弹出层 - 分类 -->
    <div class="filter-popup" v-show="showCategoryPopup">
      <div class="popup-content">
        <div 
          class="popup-item" 
          v-for="(item, index) in categoryOptions" 
          :key="index"
          :class="{ active: currentCategory === item }"
          @click="selectCategory(item)"
        >
          {{ item }}
        </div>
      </div>
    </div>

    <!-- 筛选弹出层 - 用户排序 -->
    <div class="filter-popup" v-show="showUserSortPopup">
      <div class="popup-content">
        <div 
          class="popup-item" 
          v-for="(item, index) in userSortOptions" 
          :key="index"
          :class="{ active: currentUserSort === item }"
          @click="selectUserSort(item)"
        >
          {{ item }}
        </div>
      </div>
    </div>

    <!-- 筛选弹出层 - 用户类型 -->
    <div class="filter-popup" v-show="showUserTypePopup">
      <div class="popup-content">
        <div 
          class="popup-item" 
          v-for="(item, index) in userTypeOptions" 
          :key="index"
          :class="{ active: currentUserType === item }"
          @click="selectUserType(item)"
        >
          {{ item }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showLoadingToast, closeToast } from '../../utils/vant-ui';
import { searchArticles } from '@/api/search';

const router = useRouter();
const route = useRoute();
const keyword = ref('');
const activeTab = ref(0);

// 资讯筛选相关状态
const showSortPopup = ref(false);
const showCategoryPopup = ref(false);
const currentSort = ref('默认排序');
const currentCategory = ref('全部分类');

const sortOptions = ref(['默认排序', '最新发布', '最多浏览']);
const categoryOptions = ref(['全部分类', '科技', '财经', '体育', '娱乐', '健康', '教育', '汽车', '旅游']);

// 用户筛选相关状态
const showUserSortPopup = ref(false);
const showUserTypePopup = ref(false);
const currentUserSort = ref('默认排序');
const currentUserType = ref('全部用户');

const userSortOptions = ref(['默认排序', '最新发布', '粉丝由多到少', '粉丝由少到多']);
const userTypeOptions = ref(['全部用户', '自媒体', '普通用户']);

// 分页相关状态
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const pageSize = ref(10);
const newsList = ref([]);

// 关闭所有弹出层
const closeAllPopups = () => {
  showSortPopup.value = false;
  showCategoryPopup.value = false;
  showUserSortPopup.value = false;
  showUserTypePopup.value = false;
};

// 获取搜索结果
const fetchSearchResults = async (isLoadMore = false) => {
  if (loading.value || finished.value) return;
  
  loading.value = true;
  if (!isLoadMore) {
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      loadingType: 'spinner',
      duration: 0
    });
  }

  try {
    const params = {
      keyword: keyword.value,
      page: page.value,
      pageSize: pageSize.value,
      sort: currentSort.value === '默认排序' ? 'default' : 
            currentSort.value === '最新发布' ? 'newest' : 'most_viewed',
      category: currentCategory.value === '全部分类' ? '' : currentCategory.value
    };

    const res = await searchArticles(params);
    if (res.status === 200) {
      const { list, total } = res.data;
      
      if (isLoadMore) {
        newsList.value.push(...list);
      } else {
        newsList.value = list;
      }

      if (newsList.value.length >= total) {
        finished.value = true;
      } else {
        page.value += 1;
      }
    }
  } catch (error) {
    console.log('获取搜索结果失败', error);
    showToast('加载失败，请重试');
  } finally {
    loading.value = false;
    if (!isLoadMore) {
      closeToast();
    }
  }
};

// 处理搜索操作
const onSearch = () => {
  if (!keyword.value.trim()) {
    showToast('请输入搜索内容');
    return;
  }
  
  // 重置分页状态
  page.value = 1;
  finished.value = false;
  newsList.value = [];
  
  // 重新获取搜索结果
  fetchSearchResults();
};

// 取消搜索
const onCancel = () => {
  router.back();
};

// 返回上一页
const onBack = () => {
  router.back();
};

// 查看新闻详情
const viewNewsDetail = (item) => {
  router.push(`/news/${item.id}`);
};

// 显示排序选项
const showSortOptions = () => {
  showSortPopup.value = true;
};

// 显示分类选项
const showCategoryOptions = () => {
  showCategoryPopup.value = true;
};

// 显示用户排序选项
const showUserSortOptions = () => {
  showUserSortPopup.value = true;
};

// 显示用户类型选项
const showUserTypeOptions = () => {
  showUserTypePopup.value = true;
};

// 选择排序方式
const selectSort = (item) => {
  currentSort.value = item;
  showSortPopup.value = false;
  // 重置分页状态并重新获取数据
  page.value = 1;
  finished.value = false;
  newsList.value = [];
  fetchSearchResults();
};

// 选择分类
const selectCategory = (item) => {
  currentCategory.value = item;
  showCategoryPopup.value = false;
  // 重置分页状态并重新获取数据
  page.value = 1;
  finished.value = false;
  newsList.value = [];
  fetchSearchResults();
};

// 选择用户排序方式
const selectUserSort = (item) => {
  currentUserSort.value = item;
  showUserSortPopup.value = false;
};

// 选择用户类型
const selectUserType = (item) => {
  currentUserType.value = item;
  showUserTypePopup.value = false;
};

// 加载更多
const onLoad = () => {
  fetchSearchResults(true);
};

// 页面加载时获取初始数据
onMounted(() => {
  // 从路由参数中获取搜索关键词
  const searchKeyword = route.query.keyword || '';
  keyword.value = searchKeyword;
  
  if (searchKeyword) {
    fetchSearchResults();
  }
});

// 筛选后的用户列表
const filteredUserList = computed(() => {
  // 根据用户类型筛选
  let filteredList = currentUserType.value === '全部用户'
    ? allUserList.value
    : allUserList.value.filter(item => item.type === currentUserType.value);

  // 根据排序选项排序
  if (currentUserSort.value === '最新发布') {
    // 按创建时间排序
    return [...filteredList].sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
  } else if (currentUserSort.value === '粉丝由多到少') {
    // 按粉丝数从多到少排序
    return [...filteredList].sort((a, b) => b.followers - a.followers);
  } else if (currentUserSort.value === '粉丝由少到多') {
    // 按粉丝数从少到多排序
    return [...filteredList].sort((a, b) => a.followers - b.followers);
  }
  
  // 默认排序
  return filteredList;
});
</script>

<style scoped>
.search-result-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  position: relative;
}

.search-header {
  position: sticky;
  top: 0;
  z-index: 2000;
}

.back-icon {
  font-size: 18px;
  color: #333;
  margin-right: 4px;
}

.content-tabs {
  background-color: #fff;
  position: relative;
  z-index: 10;
}

.content-tabs :deep(.van-tabs__nav) {
  background-color: #fff;
}

.content-tabs :deep(.van-tab) {
  color: #666;
  font-size: 15px;
}

.content-tabs :deep(.van-tab--active) {
  color: #3478F6;
  font-size: 15px;
  font-weight: 500;
}

.content-tabs :deep(.van-tabs__line) {
  background-color: #3478F6;
}

.filter-bar {
  display: flex;
  justify-content: flex-start;
  padding: 12px 16px;
  background-color: #fff;
  border-bottom: 1px solid #f0f0f0;
  position: relative;
  z-index: 20;
}

.filter-item {
  display: flex;
  align-items: center;
  margin-right: 24px;
  font-size: 14px;
  color: #666;
}

.filter-item .van-icon {
  margin-left: 4px;
  transition: transform 0.3s;
}

.arrow-up {
  transform: rotate(180deg);
}

/* 全局遮罩层 */
.global-mask {
  position: fixed;
  top: 138px;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  z-index: 900;
}

/* 筛选弹出层 */
.filter-popup {
  position: absolute;
  top: 138px;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #fff;
}

.popup-content {
  width: 100%;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(100, 101, 102, 0.12);
}

.popup-item {
  padding: 12px 0;
  font-size: 14px;
  color: #666;
  border-bottom: 1px solid #f5f5f5;
}

.popup-item.active {
  color: #3478F6;
}

.popup-item:last-child {
  border-bottom: none;
}

/* 列表样式 */
.news-list, .user-list {
  background-color: #fff;
  position: relative;
  z-index: 5;
  padding: 0 16px;
}

.news-item {
  display: flex;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.news-content {
  flex: 1;
  margin-right: 12px;
}

.news-title {
  font-size: 16px;
  font-weight: 400;
  line-height: 1.4;
  color: #333;
  margin: 0 0 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.news-info {
  font-size: 12px;
  color: #999;
}

.news-info .source {
  margin-right: 8px;
}

.news-info .time, .news-info .comment-count {
  margin-right: 8px;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  flex: 1;
  margin-left: 12px;
}

.user-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.user-desc {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.user-fans {
  font-size: 12px;
  color: #999;
}

.follow-btn {
  height: 32px;
  padding: 0 12px;
}

.empty-state {
  padding: 40px 0;
}

/* 加载更多样式 */
.van-loading {
  text-align: center;
  padding: 16px 0;
}

.van-divider {
  margin: 16px 0;
}
</style> 