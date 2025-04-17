<template>
  <div class="home">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <!-- 轮播图 -->
      <div class="banner" v-if="banners.length">
        <van-swipe class="banner-swipe" :autoplay="3000" indicator-color="white">
          <van-swipe-item v-for="banner in banners" :key="banner.id">
            <img :src="banner.image" :alt="banner.title" @click="handleBannerClick(banner)">
          </van-swipe-item>
        </van-swipe>
      </div>

      <!-- 分类标签 -->
      <div class="categories" v-if="categories.length">
        <van-grid :column-num="4" :border="false">
          <van-grid-item 
            v-for="category in categories" 
            :key="category.id"
            :icon="category.icon"
            :text="category.name"
            @click="handleCategoryClick(category)"
          />
        </van-grid>
      </div>

      <!-- 每日推荐 -->
      <div class="daily-recommend" v-if="dailyRecommend">
        <div class="section-title">
          <span>每日推荐</span>
          <van-icon name="arrow" @click="viewDailyRecommend" />
        </div>
        <div class="recommend-content" @click="viewDailyRecommend">
          <van-image
            :src="dailyRecommend.cover"
            fit="cover"
            radius="8"
            width="100%"
            height="160"
          />
          <div class="recommend-info">
            <h3 class="title">{{ dailyRecommend.title }}</h3>
            <p class="desc">{{ dailyRecommend.description }}</p>
          </div>
        </div>
      </div>

      <!-- 热门文章 -->
      <div class="hot-articles" v-if="hotArticles.length">
        <div class="section-title">
          <span>热门文章</span>
          <van-icon name="arrow" @click="viewHotArticles" />
        </div>
        <div class="article-list">
          <div 
            class="article-item" 
            v-for="article in hotArticles" 
            :key="article.id"
            @click="viewArticle(article)"
          >
            <div class="article-content">
              <h3 class="title">{{ article.title }}</h3>
              <p class="desc">{{ article.description }}</p>
              <div class="meta">
                <span class="author">{{ article.author.name }}</span>
                <span class="time">{{ article.createTime }}</span>
                <span class="views">{{ article.views }}阅读</span>
              </div>
            </div>
            <div class="article-image" v-if="article.cover">
              <van-image
                :src="article.cover"
                fit="cover"
                radius="4"
                width="110"
                height="70"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 推荐作者 -->
      <div class="recommended-authors" v-if="recommendedAuthors.length">
        <div class="section-title">
          <span>推荐作者</span>
          <van-icon name="arrow" @click="viewRecommendedAuthors" />
        </div>
        <div class="author-list">
          <div 
            class="author-item" 
            v-for="author in recommendedAuthors" 
            :key="author.id"
            @click="viewAuthor(author)"
          >
            <van-image
              round
              width="50"
              height="50"
              :src="author.avatar"
            />
            <div class="author-info">
              <div class="name">{{ author.name }}</div>
              <div class="desc">{{ author.description }}</div>
              <div class="stats">
                <span>{{ author.followers }}粉丝</span>
                <span>{{ author.articles }}文章</span>
              </div>
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
      </div>

      <!-- 文章列表 -->
      <div class="article-list">
        <div 
          class="article-item" 
          v-for="article in articles" 
          :key="article.id"
          @click="viewArticle(article)"
        >
          <div class="article-content">
            <h3 class="title">{{ article.title }}</h3>
            <p class="desc">{{ article.description }}</p>
            <div class="meta">
              <span class="author">{{ article.author.name }}</span>
              <span class="time">{{ article.createTime }}</span>
              <span class="views">{{ article.views }}阅读</span>
            </div>
          </div>
          <div class="article-image" v-if="article.cover">
            <van-image
              :src="article.cover"
              fit="cover"
              radius="4"
              width="110"
              height="70"
            />
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <van-loading v-if="loading" />
      <van-divider v-if="finished">没有更多了</van-divider>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showLoadingToast, closeToast } from '@/utils/vant-ui';
import {
  getHomeArticles,
  getHotArticles,
  getRecommendedAuthors,
  getCategories,
  getBanners,
  getDailyRecommend
} from '@/api/home';

const router = useRouter();

// 数据列表
const banners = ref([]);
const categories = ref([]);
const dailyRecommend = ref(null);
const hotArticles = ref([]);
const recommendedAuthors = ref([]);
const articles = ref([]);

// 分页相关
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const pageSize = ref(10);

// 下拉刷新
const refreshing = ref(false);

// 获取轮播图
const fetchBanners = async () => {
  try {
    const res = await getBanners();
    if (res.status === 200) {
      banners.value = res.data;
    }
  } catch (error) {
    console.log('获取轮播图失败', error);
  }
};

// 获取分类
const fetchCategories = async () => {
  try {
    const res = await getCategories();
    if (res.status === 200) {
      categories.value = res.data;
    }
  } catch (error) {
    console.log('获取分类失败', error);
  }
};

// 获取每日推荐
const fetchDailyRecommend = async () => {
  try {
    const res = await getDailyRecommend();
    if (res.status === 200) {
      dailyRecommend.value = res.data;
    }
  } catch (error) {
    console.log('获取每日推荐失败', error);
  }
};

// 获取热门文章
const fetchHotArticles = async () => {
  try {
    const res = await getHotArticles();
    if (res.status === 200) {
      hotArticles.value = res.data;
    }
  } catch (error) {
    console.log('获取热门文章失败', error);
  }
};

// 获取推荐作者
const fetchRecommendedAuthors = async () => {
  try {
    const res = await getRecommendedAuthors();
    if (res.status === 200) {
      recommendedAuthors.value = res.data;
    }
  } catch (error) {
    console.log('获取推荐作者失败', error);
  }
};

// 获取文章列表
const fetchArticles = async (isLoadMore = false) => {
  if (loading.value || finished.value) return;
  
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    };

    const res = await getHomeArticles(params);
    if (res.status === 200) {
      const { list, total } = res.data;
      
      if (isLoadMore) {
        articles.value.push(...list);
      } else {
        articles.value = list;
      }

      if (articles.value.length >= total) {
        finished.value = true;
      } else {
        page.value += 1;
      }
    }
  } catch (error) {
    console.log('获取文章列表失败', error);
    showToast('加载失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 处理轮播图点击
const handleBannerClick = (banner) => {
  if (banner.link) {
    router.push(banner.link);
  }
};

// 处理分类点击
const handleCategoryClick = (category) => {
  router.push({
    path: '/search/result',
    query: { category: category.id }
  });
};

// 查看每日推荐
const viewDailyRecommend = () => {
  if (dailyRecommend.value) {
    router.push(`/article/${dailyRecommend.value.id}`);
  }
};

// 查看热门文章
const viewHotArticles = () => {
  router.push({
    path: '/search/result',
    query: { type: 'hot' }
  });
};

// 查看推荐作者
const viewRecommendedAuthors = () => {
  router.push({
    path: '/search/result',
    query: { type: 'authors' }
  });
};

// 查看文章详情
<<<<<<< HEAD
// const viewArticle = (article) => {
//   router.push(`/article/${article.id}`);
// };
const viewArticle = (article) => {
  router.push(`/article`);
=======
const viewArticle = (article) => {
  router.push(`/article/${article.id}`);
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
};

// 查看作者主页
const viewAuthor = (author) => {
  router.push(`/user/${author.id}`);
};

// 刷新所有数据
const refreshAllData = async () => {
  try {
    await Promise.all([
      fetchBanners(),
      fetchCategories(),
      fetchDailyRecommend(),
      fetchHotArticles(),
      fetchRecommendedAuthors()
    ]);
    
    // 重置文章列表
    page.value = 1;
    finished.value = false;
    await fetchArticles();
  } catch (error) {
    console.log('刷新数据失败', error);
    showToast('刷新失败，请重试');
  }
};

// 下拉刷新
const onRefresh = async () => {
  try {
    await refreshAllData();
  } finally {
    refreshing.value = false;
  }
};

// 页面加载时获取数据
onMounted(async () => {
  showLoadingToast({
    message: '加载中...',
    forbidClick: true,
    loadingType: 'spinner',
    duration: 0
  });

  try {
    await Promise.all([
      fetchBanners(),
      fetchCategories(),
      fetchDailyRecommend(),
      fetchHotArticles(),
      fetchRecommendedAuthors(),
      fetchArticles()
    ]);
  } catch (error) {
    console.log('加载数据失败', error);
    showToast('加载失败，请重试');
  } finally {
    closeToast();
  }
});
</script>

<style scoped>
.home {
  padding-bottom: 50px;
}

.banner {
  margin-bottom: 12px;
}

.banner-swipe {
  height: 160px;
}

.banner-swipe img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.categories {
  background-color: #fff;
  padding: 12px 0;
  margin-bottom: 12px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: #fff;
  margin-bottom: 12px;
}

.section-title span {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.section-title .van-icon {
  font-size: 16px;
  color: #999;
}

.daily-recommend {
  background-color: #fff;
  margin-bottom: 12px;
}

.recommend-content {
  padding: 0 16px 16px;
}

.recommend-info {
  margin-top: 8px;
}

.recommend-info .title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.recommend-info .desc {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.hot-articles {
  background-color: #fff;
  margin-bottom: 12px;
}

.article-list {
  padding: 0 16px;
}

.article-item {
  display: flex;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.article-content {
  flex: 1;
  margin-right: 12px;
}

.article-content .title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  margin-bottom: 8px;
}

.article-content .desc {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.article-content .meta {
  font-size: 12px;
  color: #999;
}

.article-content .meta span {
  margin-right: 12px;
}

.article-image {
  flex-shrink: 0;
}

.recommended-authors {
  background-color: #fff;
  margin-bottom: 12px;
}

.author-list {
  padding: 0 16px;
}

.author-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.author-info {
  flex: 1;
  margin-left: 12px;
}

.author-info .name {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.author-info .desc {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1;
  overflow: hidden;
}

.author-info .stats {
  font-size: 12px;
  color: #999;
}

.author-info .stats span {
  margin-right: 12px;
}

.follow-btn {
  margin-left: 12px;
}

.van-loading {
  text-align: center;
  padding: 16px 0;
}

.van-divider {
  margin: 16px 0;
}
</style> 