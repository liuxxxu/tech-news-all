<template>
  <div class="article-detail">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <van-nav-bar
        left-arrow
        @click-left="onBack"
      />
    </div>

    <!-- 文章内容 -->
    <div class="article-content" v-if="article">
      <!-- 文章头部 -->
      <div class="article-header">
        <h1 class="title">{{ article.title }}</h1>
        <div class="meta">
          <div class="author">
            <van-image
              round
              width="32"
              height="32"
              :src="article.author.avatar"
            />
            <span class="name">{{ article.author.name }}</span>
          </div>
          <div class="info">
            <span class="time">{{ article.createTime }}</span>
            <span class="views">{{ article.views }}阅读</span>
            <span class="comments">{{ article.comments }}评论</span>
          </div>
        </div>
      </div>

      <!-- 文章正文 -->
      <div class="content" v-html="article.content"></div>

      <!-- 文章标签 -->
      <div class="tags" v-if="article.tags && article.tags.length">
        <van-tag 
          v-for="tag in article.tags" 
          :key="tag"
          plain
          type="primary"
          class="tag"
        >
          {{ tag }}
        </van-tag>
      </div>

      <!-- 互动区域 -->
      <div class="interaction">
        <div class="action-item" @click="handleLike">
          <van-icon :name="article.isLiked ? 'like' : 'like-o'" :class="{ active: article.isLiked }" />
          <span :class="{ active: article.isLiked }">{{ article.likes }}</span>
        </div>
        <div class="action-item" @click="handleFavorite">
          <van-icon :name="article.isFavorited ? 'star' : 'star-o'" :class="{ active: article.isFavorited }" />
          <span :class="{ active: article.isFavorited }">{{ article.favorites }}</span>
        </div>
        <div class="action-item" @click="handleShare">
          <van-icon name="share-o" />
          <span>{{ article.shares }}</span>
        </div>
      </div>
    </div>

    <!-- 评论区域 -->
    <div class="comments-section">
      <div class="section-title">
        <span>评论 ({{ article?.comments || 0 }})</span>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list" v-if="comments.length">
        <div class="comment-item" v-for="comment in comments" :key="comment.id">
          <div class="comment-header">
            <van-image
              round
              width="36"
              height="36"
              :src="comment.user.avatar"
            />
            <div class="comment-info">
              <div class="username">{{ comment.user.name }}</div>
              <div class="time">{{ comment.createTime }}</div>
            </div>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-actions">
            <span class="like" @click="handleCommentLike(comment)">
              <van-icon :name="comment.isLiked ? 'like' : 'like-o'" :class="{ active: comment.isLiked }" />
              {{ comment.likes }}
            </span>
            <span class="reply" @click="handleReply(comment)">回复</span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <van-empty
        v-else
        description="暂无评论"
        image="comment-o"
      />

      <!-- 加载更多 -->
      <van-loading v-if="loading" />
      <van-divider v-if="finished">没有更多评论了</van-divider>
    </div>

    <!-- 评论输入框 -->
    <div class="comment-input">
      <van-field
        v-model="commentContent"
        placeholder="说点什么..."
        :border="false"
        @keypress.enter="submitComment"
      >
        <template #button>
          <van-button 
            size="small" 
            type="primary" 
            :disabled="!commentContent.trim()"
            @click="submitComment"
          >
            发送
          </van-button>
        </template>
      </van-field>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { showToast, showLoadingToast, closeToast } from '@/utils/vant-ui';
import {
  getArticleDetail,
  getArticleComments,
  addComment,
  likeArticle,
  unlikeArticle,
  favoriteArticle,
  unfavoriteArticle,
  shareArticle
} from '@/api/article';

const router = useRouter();
const route = useRoute();

// 文章数据
const article = ref(null);

// 评论相关
const comments = ref([]);
const commentContent = ref('');
const loading = ref(false);
const finished = ref(false);
const page = ref(1);
const pageSize = ref(10);

// 获取文章详情
const fetchArticleDetail = async () => {
  showLoadingToast({
    message: '加载中...',
    forbidClick: true,
    loadingType: 'spinner',
    duration: 0
  });

  try {
    const res = await getArticleDetail(route.params.id);
    if (res.status === 200) {
      article.value = res.data;
    }
  } catch (error) {
    console.log('获取文章详情失败', error);
  } finally {
    closeToast();
  }
};

// 获取评论列表
const fetchComments = async (isLoadMore = false) => {
  if (loading.value || finished.value) return;
  
  loading.value = true;
  try {
    const params = {
      page: page.value,
      pageSize: pageSize.value
    };

    const res = await getArticleComments(route.params.id, params);
    if (res.status === 200) {
      const { list, total } = res.data;
      
      if (isLoadMore) {
        comments.value.push(...list);
      } else {
        comments.value = list;
      }

      if (comments.value.length >= total) {
        finished.value = true;
      } else {
        page.value += 1;
      }
    }
  } catch (error) {
    console.log('获取评论失败', error);
  } finally {
    loading.value = false;
  }
};

// 提交评论
const submitComment = async () => {
  if (!commentContent.value.trim()) {
    showToast('请输入评论内容');
    return;
  }

  try {
    const res = await addComment(route.params.id, {
      content: commentContent.value
    });
    
    if (res.status === 200) {
      commentContent.value = '';
      // 重新加载评论列表
      page.value = 1;
      finished.value = false;
      await fetchComments();
    }
  } catch (error) {
    console.log('发表评论失败', error);
  }
};

// 点赞文章
const handleLike = async () => {
  try {
    const api = article.value.isLiked ? unlikeArticle : likeArticle;
    const res = await api(route.params.id);
    
    if (res.status === 200) {
      article.value.isLiked = !article.value.isLiked;
      article.value.likes += article.value.isLiked ? 1 : -1;
    }
  } catch (error) {
    console.log('操作失败', error);
  }
};

// 收藏文章
const handleFavorite = async () => {
  try {
    const api = article.value.isFavorited ? unfavoriteArticle : favoriteArticle;
    const res = await api(route.params.id);
    
    if (res.status === 200) {
      article.value.isFavorited = !article.value.isFavorited;
      article.value.favorites += article.value.isFavorited ? 1 : -1;
    }
  } catch (error) {
    console.log('操作失败', error);
  }
};

// 分享文章
const handleShare = async () => {
  try {
    const res = await shareArticle(route.params.id);
    if (res.status === 200) {
      article.value.shares += 1;
      showToast('分享成功');
    }
  } catch (error) {
    console.log('分享失败', error);
  }
};

// 点赞评论
const handleCommentLike = async (comment) => {
  try {
    // TODO: 实现评论点赞API
    comment.isLiked = !comment.isLiked;
    comment.likes += comment.isLiked ? 1 : -1;
  } catch (error) {
    console.log('操作失败', error);
  }
};

// 回复评论
const handleReply = (comment) => {
  commentContent.value = `@${comment.user.name} `;
};

// 返回上一页
const onBack = () => {
  router.back();
};

// 加载更多
const onLoad = () => {
  fetchComments(true);
};

// 页面加载时获取数据
onMounted(async () => {
  await Promise.all([
    fetchArticleDetail(),
    fetchComments()
  ]);
});
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background-color: #fff;
  padding-bottom: 50px;
}

.nav-bar {
  position: sticky;
  top: 0;
  z-index: 100;
}

.article-content {
  padding: 16px;
}

.article-header {
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  margin-bottom: 12px;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author {
  display: flex;
  align-items: center;
}

.author .name {
  margin-left: 8px;
  font-size: 14px;
  color: #333;
}

.info {
  font-size: 12px;
  color: #999;
}

.info span {
  margin-left: 12px;
}

.content {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 20px;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.tag {
  font-size: 12px;
}

.interaction {
  display: flex;
  justify-content: space-around;
  padding: 16px 0;
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #666;
}

.action-item .van-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.action-item .active {
  color: #f56c6c;
}

.comments-section {
  padding: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 16px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.comment-info {
  margin-left: 8px;
}

.username {
  font-size: 14px;
  color: #333;
  margin-bottom: 2px;
}

.time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.comment-actions span {
  display: flex;
  align-items: center;
}

.comment-actions .van-icon {
  margin-right: 4px;
}

.comment-input {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #f5f5f5;
  padding: 8px 16px;
}

.van-loading {
  text-align: center;
  padding: 16px 0;
}

.van-divider {
  margin: 16px 0;
}
</style> 