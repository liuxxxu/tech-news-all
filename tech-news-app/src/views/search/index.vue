<template>
  <div class="search-container">
    <!-- 顶部搜索栏 -->
    <div class="search-header">
      <van-search
        v-model="searchValue"
        show-action
        placeholder="请输入搜索关键词"
        background="#ffffff"
        shape="round"
        @search="onSearch"
        @cancel="onCancel"
        @focus="onFocus"
        clearable
      >
        <template #left-icon>
          <van-icon name="arrow-left" class="back-icon" @click="onBack" />
        </template>
      </van-search>
    </div>

    <!-- 搜索历史 -->
    <div class="search-history" v-if="searchHistoryVisible">
      <div class="history-header">
        <span class="header-title">搜索历史</span>
        <van-icon name="delete" class="clear-icon" @click="clearHistory" />
      </div>
      <div class="history-list">
        <div 
          class="history-item" 
          v-for="(item, index) in searchHistory" 
          :key="index"
          @click="useHistoryItem(item)"
        >
          {{ item }}
        </div>
      </div>
    </div>

    <!-- 热门搜索标签 -->
    <div class="hot-search">
      <div class="hot-header">
        <span class="header-title">热门搜索</span>
        <van-icon name="replay" class="refresh-icon" @click="refreshHotTopics" />
      </div>
      <div class="hot-list">
        <div 
          class="hot-item" 
          v-for="(item, index) in hotTopics" 
          :key="index"
          @click="useHotTopic(item)"
        >
          <span class="hot-rank" :class="{ 'top-rank': index < 3 }">{{ index + 1 }}</span>
          <span class="hot-text">{{ item }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showLoadingToast, closeToast } from '../../utils/vant-ui';
// import { getHotSearch, getSearchHistory, clearSearchHistory, addSearchHistory } from '@/api/search';

const router = useRouter();
const searchValue = ref('');
const searchHistoryVisible = ref(true);
const searchHistory = ref([]);
const hotTopics = ref([]);

// 获取搜索历史
const fetchSearchHistory = async () => {
  // 使用假数据
  searchHistory.value = [
    '人工智能',
    'ChatGPT',
    '元宇宙',
    '区块链',
    'Web3.0',
    '5G技术'
  ];
  
  // try {
  //   const res = await getSearchHistory();
  //   if (res.status === 200) {
  //     searchHistory.value = res.data;
  //   }
  // } catch (error) {
  //   console.log('获取搜索历史失败', error);
  // }
};

// 获取热门搜索
const fetchHotTopics = async () => {
  // 使用假数据
  hotTopics.value = [
    'ChatGPT最新进展',
    'AI绘画工具推荐',
    '元宇宙发展趋势',
    'Web3.0应用场景',
    '区块链技术应用',
    '5G网络建设进展',
    '数字人民币发展',
    '智能家居最新产品'
  ];
  
  // try {
  //   const res = await getHotSearch();
  //   if (res.status === 200) {
  //     hotTopics.value = res.data;
  //   }
  // } catch (error) {
  //   console.log('获取热门搜索失败', error);
  // }
};

// 处理搜索操作
const onSearch = async () => {
  if (!searchValue.value.trim()) {
    showToast('请输入搜索内容');
    return;
  }
  
  try {
    // 添加到搜索历史
    // await addSearchHistory(searchValue.value);
    // 重新获取搜索历史
    // await fetchSearchHistory();
    
    // 跳转到搜索结果页
    router.push({
      path: '/search/result',
      query: { keyword: searchValue.value }
    });
  } catch (error) {
    console.log('搜索失败', error);
    showToast('搜索失败，请重试');
  }
};

// 取消搜索
const onCancel = () => {
  router.back();
};

// 返回上一页
const onBack = () => {
  router.back();
};

// 搜索框获取焦点
const onFocus = () => {
  searchHistoryVisible.value = true;
};

// 清除搜索历史
const clearHistory = async () => {
  try {
    // const res = await clearSearchHistory();
    // if (res.status === 200) {
    searchHistory.value = [];
    showToast('已清空搜索历史');
    // }
  } catch (error) {
    console.log('清除搜索历史失败', error);
    showToast('清除失败，请重试');
  }
};

// 使用历史搜索项
const useHistoryItem = (item) => {
  searchValue.value = item;
  onSearch();
};

// 使用热门话题
const useHotTopic = (item) => {
  searchValue.value = item;
  onSearch();
};

// 刷新热门话题
const refreshHotTopics = async () => {
  showLoadingToast({
    message: '更新中...',
    forbidClick: true,
    loadingType: 'spinner',
    duration: 0
  });
  
  try {
    // 模拟刷新，随机打乱热门话题顺序
    hotTopics.value = [...hotTopics.value].sort(() => Math.random() - 0.5);
    closeToast();
    showToast('已更新热门搜索');
  } catch (error) {
    console.log('刷新热门搜索失败', error);
    closeToast();
    showToast('更新失败，请重试');
  }
};

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchSearchHistory(),
    fetchHotTopics()
  ]);
});
</script>

<style scoped>
.search-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 20px;
}

.search-header {
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-icon {
  font-size: 18px;
  color: #333;
  margin-right: 4px;
}

.search-history, .hot-search {
  margin: 16px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
}

.history-header, .hot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.clear-icon, .refresh-icon {
  font-size: 16px;
  color: #999;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.history-item {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border-radius: 16px;
  font-size: 14px;
  color: #666;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hot-item {
  display: flex;
  align-items: center;
}

.hot-rank {
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #999;
}

.top-rank {
  color: #f56c6c;
  font-weight: bold;
}

.hot-text {
  font-size: 14px;
  color: #333;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style> 