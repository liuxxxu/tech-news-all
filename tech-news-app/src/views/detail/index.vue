<template>
    <div class="detail-container">
        <!-- 顶部导航栏 -->
        <van-nav-bar left-text="返回" left-arrow @click-left="onBackClick" fixed class="detail-nav">
            <template #right>
                <van-icon name="share-o" size="20" color="#3478F6" @click="handleShare" class="share-icon" />
            </template>
        </van-nav-bar>

        <!-- 新闻内容 -->
        <div class="news-content">
            <!-- 新闻来源和关注 -->
            <div class="news-author">
                <div class="author-info">
                    <van-image round width="40" height="40" :src="newsDetail.authorAvatar" alt="来源图标" />
                    <div class="author-detail">
                        <div class="author-name">{{ newsDetail.authorName }}</div>
                        <div class="publish-time">{{ newsDetail.time }}</div>
                    </div>
                </div>
                <van-button round type="primary" size="small" class="follow-btn">关注</van-button>
            </div>

            <h1 class="news-title">{{ newsDetail.title }}</h1>

            <!-- AI文摘部分 -->
            <div class="ai-summary">
                <div class="ai-header">
                    <van-icon name="cluster-o" size="20" color="#3478F6" />
                    <span class="ai-title">AI文摘</span>
                </div>
                <div class="ai-content">
                    {{ displayedSummary }}
                </div>
                <div class="ai-footer">由AI智能生成，仅供参考</div>
                <div class="like-btn" @click="handleAiLike">
                    <van-icon name="good-job" size="20" color="#3478F6" />
                    <span>{{ aiLikeText }}</span>
                </div>
            </div>

            <div class="news-cover" v-if="newsDetail.cover">
                <van-image width="100%" fit="cover" :src="newsDetail.cover" alt="新闻封面" />
            </div>

            <div class="news-body" v-html="newsDetail.content"></div>

            <div class="news-tags">
                <van-tag plain type="primary" v-for="(tag, index) in newsDetail.tags" :key="index">
                    {{ tag }}
                </van-tag>
            </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
            <div class="section-header">
                <h3>评论 {{ newsDetail.comments.length }}</h3>
                <span class="sort-text">按热度排序</span>
            </div>

            <div class="comments-list">
                <div class="comment-item" v-for="(comment, index) in newsDetail.comments" :key="index">
                    <div class="comment-user">
                        <van-image round width="36" height="36" :src="comment.avatar" alt="用户头像" />
                        <div class="user-info">
                            <div class="user-name">{{ comment.username }}</div>
                            <div class="comment-time">{{ comment.time }}</div>
                        </div>
                    </div>

                    <div class="comment-content">{{ comment.content }}</div>

                    <div class="comment-actions">
                        <div class="action-item">
                            <van-icon name="good-job-o" size="16" /> {{ comment.likes }}
                        </div>
                        <div class="action-item">
                            <van-icon name="chat-o" size="16" /> 回复
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="bottom-bar">
            <div class="comment-input">
                <van-field v-model="commentText" placeholder="写评论..." class="input-field" />
            </div>
            <van-button round type="primary" size="small" class="comment-btn">评论</van-button>

            <div class="action-buttons">
                <div class="action-btn" @click="handleLike">
                    <van-icon :name="isLiked ? 'good-job' : 'good-job-o'" :class="{ 'active': isLiked }" size="24" />
                    <span :class="{ 'active': isLiked }">{{ newsDetail.likes }}</span>
                </div>
                <div class="action-btn" @click="handleCollect">
                    <van-icon :name="isCollected ? 'star' : 'star-o'" :class="{ 'active': isCollected }" size="24" />
                    <span :class="{ 'active': isCollected }">{{ newsDetail.collects }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showSuccessToast, showToast } from '../../utils/vant-ui'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()

const commentText = ref('')
const aiLikeText = ref('有帮助')
const displayedSummary = ref('')
const isLiked = ref(false)
const isCollected = ref(false)
const newsDetail = ref({
  sourceIcon: '',
  source: '',
  time: '',
  title: '',
  summary: '',
  cover: '',
  content: '',
  tags: [],
  comments: [],
  likes: 0,
  collects: 0,
  shares: 0,
  authorName: '',
  authorAvatar: '/src/assets/portrait.png'
})

// 流式输出AI文摘
const streamSummary = async (text) => {
  displayedSummary.value = ''
  const chars = text.split('')
  for (let char of chars) {
    displayedSummary.value += char
    await new Promise(resolve => setTimeout(resolve, 50)) // 每个字50ms的延迟
  }
}

// 获取新闻详情
const fetchNewsDetail = async () => {
  try {
    const response = await request.get(`/article/api/article/${route.params.id}`)
    const { data } = response
    // 确保数据结构正确
    newsDetail.value = {
      sourceIcon: data.authorAvatar || '/src/assets/portrait.png',
      source: data.authorName || '',
      time: data.time || '',
      title: data.title || '',
      summary: data.summary || '',
      cover: data.cover || '',
      content: data.content || '',
      tags: Array.isArray(data.tags) ? data.tags : [],
      comments: Array.isArray(data.comments) ? data.comments : [],
      likes: Number(data.likes) || 0,
      collects: Number(data.collects) || 0,
      shares: Number(data.shares) || 0,
      authorName: data.authorName || '',
      authorAvatar: data.authorAvatar || '/src/assets/portrait.png'
    }
    // 开始流式输出摘要
    if (data.summary) {
      streamSummary(data.summary)
    }
  } catch (error) {
    console.error('获取新闻详情失败', error)
    showToast('获取新闻详情失败')
  }
}

onMounted(() => {
  fetchNewsDetail()
})

const onBackClick = () => {
    router.back()
}

const handleShare = () => {
    router.push({
        path: '/post',
        query: {
            type: 'share',
            newsId: newsDetail.value.id,
            newsTitle: newsDetail.value.title,
            newsSource: newsDetail.value.source,
            newsCover: newsDetail.value.cover
        }
    })
}

const handleAiLike = () => {
    aiLikeText.value = '感谢反馈！'
    showSuccessToast('感谢您的支持！')
}

// 处理点赞
const handleLike = () => {
  if (!isLiked.value) {
    newsDetail.value.likes++
    isLiked.value = true
    showToast('点赞成功')
  } else {
    newsDetail.value.likes--
    isLiked.value = false
    showToast('已取消点赞')
  }
}

// 处理收藏
const handleCollect = () => {
  if (!isCollected.value) {
    newsDetail.value.collects++
    isCollected.value = true
    showToast('收藏成功')
  } else {
    newsDetail.value.collects--
    isCollected.value = false
    showToast('已取消收藏')
  }
}
</script>

<style scoped>
.detail-container {
  padding-top: 46px;
  padding-bottom: 56px;
  background-color: #fff;
  min-height: 100vh;
}

.detail-nav {
  border-bottom: 1px solid #f0f0f0;
}

.detail-nav :deep(.van-nav-bar__title) {
  font-weight: 500;
}

.detail-nav :deep(.van-nav-bar__text) {
  color: #3478F6;
}

.news-content {
  padding: 16px;
}

.news-author {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
}

.author-detail {
  margin-left: 12px;
}

.author-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.publish-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.follow-btn {
  height: 32px;
  font-size: 14px;
  padding: 0 16px;
}

.news-title {
  font-size: 22px;
  font-weight: 600;
  line-height: 1.4;
  color: #333;
  margin-bottom: 20px;
}

.ai-summary {
  background-color: #f7f8fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
  position: relative;
}

.ai-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.ai-title {
  margin-left: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #3478F6;
}

.ai-content {
  font-size: 15px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 12px;
}

.ai-footer {
  font-size: 12px;
  color: #999;
}

.like-btn {
  position: absolute;
  right: 16px;
  bottom: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #3478F6;
  font-size: 14px;
}

.news-cover {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.news-body {
  font-size: 16px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 24px;
}

.news-body p {
  margin-bottom: 16px;
}

.news-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.comments-section {
  padding: 16px;
  background-color: #f7f8fa;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.sort-text {
  font-size: 14px;
  color: #999;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
}

.comment-user {
  display: flex;
  margin-bottom: 12px;
}

.user-info {
  margin-left: 12px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.comment-content {
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #999;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 8px 16px;
  display: flex;
  align-items: center;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.1);
}

.comment-input {
  flex: 1;
  margin-right: 12px;
}

.input-field {
  background-color: #f5f5f5;
  border-radius: 20px;
  height: 36px;
}

.input-field :deep(.van-field__control) {
  height: 36px;
  line-height: 36px;
}

.action-buttons {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}

.action-btn .active {
  color: #3478F6;
}

.comment-btn {
  height: 36px;
  padding: 0 16px;
  margin-right: 12px;
}

.share-icon {
  padding: 4px 16px;
}
</style> 