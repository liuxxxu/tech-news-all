<template>
<<<<<<< HEAD
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
                    <van-image round width="40" height="40" :src="newsDetail.sourceIcon" alt="来源图标" />
                    <div class="author-detail">
                        <div class="author-name">{{ newsDetail.source }}</div>
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
                    {{ newsDetail.aiSummary }}
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

            <div class="action-buttons">
                <div class="action-btn">
                    <van-icon name="good-job-o" size="24" />
                    <span>{{ newsDetail.likes }}</span>
                </div>
                <div class="action-btn">
                    <van-icon name="star-o" size="24" />
                    <span>{{ newsDetail.collects }}</span>
                </div>
                <div class="action-btn">
                    <van-icon name="share-o" size="24" />
                    <span>{{ newsDetail.shares }}</span>
                </div>
            </div>
        </div>
    </div>
=======
  <div class="detail-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-text="返回"
      left-arrow
      @click-left="onBackClick"
      fixed
      class="detail-nav"
    >
      <template #right>
        <van-icon name="share-o" size="20" color="#3478F6" @click="handleShare" class="share-icon" />
      </template>
    </van-nav-bar>

    <!-- 新闻内容 -->
    <div class="news-content">
      <!-- 新闻来源和关注 -->
      <div class="news-author">
        <div class="author-info">
          <van-image
            round
            width="40"
            height="40"
            :src="newsDetail.sourceIcon"
            alt="来源图标"
          />
          <div class="author-detail">
            <div class="author-name">{{ newsDetail.source }}</div>
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
          {{ newsDetail.aiSummary }}
        </div>
        <div class="ai-footer">
          由AI智能生成，仅供参考
        </div>
        <!-- 点赞按钮 -->
        <div class="like-btn">
          <van-icon name="good-job" size="20" color="#3478F6" />
          <span>有帮助</span>
        </div>
      </div>
      
      <div class="news-cover" v-if="newsDetail.cover">
        <van-image
          width="100%"
          fit="cover"
          :src="newsDetail.cover"
          alt="新闻封面"
        />
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
            <van-image
              round
              width="36"
              height="36"
              :src="comment.avatar"
              alt="用户头像"
            />
            <div class="user-info">
              <div class="user-name">{{ comment.username }}</div>
              <div class="comment-time">{{ comment.time }}</div>
            </div>
          </div>
          
          <div class="comment-content">
            {{ comment.content }}
          </div>
          
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
        <van-field
          v-model="commentText"
          placeholder="写评论..."
          class="input-field"
        />
      </div>
      
      <div class="action-buttons">
        <div class="action-btn">
          <van-icon name="good-job-o" size="24" />
          <span>{{ newsDetail.likes }}</span>
        </div>
        <div class="action-btn">
          <van-icon name="star-o" size="24" />
          <span>{{ newsDetail.collects }}</span>
        </div>
        <div class="action-btn">
          <van-icon name="share-o" size="24" />
          <span>{{ newsDetail.shares }}</span>
        </div>
      </div>
    </div>
  </div>
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
<<<<<<< HEAD
import { showSuccessToast } from '../../utils/vant-ui'
=======
import { showToast, showSuccessToast } from '../../utils/vant-ui'
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9

const router = useRouter()
const route = useRoute()

<<<<<<< HEAD
const commentText = ref('')
const aiLikeText = ref('有帮助')

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

// 假数据模拟
const newsDetail = ref({
    id: 1,
    title: '重大突破：科学家发现新型能源技术，有望解决全球能源危机',
    source: '科技日报',
    sourceIcon: 'https://img01.yzcdn.cn/vant/cat.jpeg',
    time: '2小时前',
    cover: 'https://ugc-img.ifengimg.com/img/2021/11/26/fd1a98d4-2585-426b-9b91-4e6fb9819598_w1920_h1200.jpeg',
    aiSummary: '该新闻报道了一项可能颠覆现有能源体系的科技成果。科学家们研发出一种新型能源技术，具备高达95%以上的能量转换效率，并承诺实现零碳排放。这一技术不仅具备低成本、高寿命的优势，还具备良好的环保性。预计5年内实现商业化，有望推动清洁能源变革，降低全球能源成本。',
    content: `
    <p>近日，一项重大科学突破引起了全球科技界的广泛关注。在一个跨国研究团队的努力下，科学家们成功开发出一种全新的能源技术，这一技术有望从根本上解决全球能源危机问题。</p>
    <p>据报道，这项新技术能够以前所未有的效率将太阳能转化为电能，转化率高达70%，远超目前市场上最先进太阳能电池的效率。更令人惊喜的是，该技术使用的材料成本较低，且环境友好，不产生有害废弃物。</p>
    <p>负责该项目的首席科学家张教授表示："这项技术的突破点在于我们发现了一种新型催化剂，它能够大幅提高能量转换效率。如果能够成功商业化，将彻底改变全球能源格局。"</p>
    <p>多位业内专家认为，这一技术若能大规模应用，将极大推动清洁能源产业发展，对缓解全球气候变化具有重要意义。</p>
    <p>目前，研究团队已申请多项专利，并计划在未来两年内与能源企业合作，推动技术产业化。预计首批基于该技术的商业产品将于2025年问世。</p>
  `,
    tags: ['科技', '新能源', '环保', '创新'],
    views: '12.5万',
    likes: 2300,
    collects: 568,
    shares: 421,
    comments: [
        {
            id: 1,
            username: '科技爱好者',
            avatar: 'https://img01.yzcdn.cn/vant/cat.jpeg',
            content: '这项技术如果真的能实现商业化，将是解决能源问题的重大突破！期待后续发展。',
            time: '1小时前',
            likes: 356
        },
        {
            id: 2,
            username: '环保卫士',
            avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
            content: '清洁能源是未来的必然趋势，支持这样的科研创新，希望能尽快投入实际应用。',
            time: '1.5小时前',
            likes: 289
        },
        {
            id: 3,
            username: '理性思考者',
            avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-3.jpeg',
            content: '虽然技术看起来很有前景，但从实验室到市场还有很长的路要走，建议大家保持理性乐观的态度。',
            time: '2小时前',
            likes: 176
        }
    ]
})
=======
// 评论文本
const commentText = ref('')

// 返回上一页
const onBackClick = () => {
  router.back()
}

// 分享文章
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

// 默认新闻详情数据
const newsDetail = ref({
  id: 1,
  title: '重大突破：科学家发现新型能源技术，有望解决全球能源危机',
  source: '科技日报',
  sourceIcon: 'https://img01.yzcdn.cn/vant/cat.jpeg',
  time: '2小时前',
  cover: 'https://images.unsplash.com/photo-1495020689067-958852a7765e',
  aiSummary: '国际科研团队发布了新型能源技术，具有零碳排放、高能量转换效率(95%以上)、成本低廉、使用寿命长等优势。预计5年内商业化，将降低能源成本、减少环境污染、创造就业机会并推动相关产业发展。多家企业已表示投资意愿。',
  content: `
    <p>近日，一项重大科学突破引起了全球科技界的广泛关注。在一个跨国研究团队的努力下，科学家们成功开发出一种全新的能源技术，这一技术有望从根本上解决全球能源危机问题。</p>
    
    <p>据报道，这项新技术能够以前所未有的效率将太阳能转化为电能，转化率高达70%，远超目前市场上最先进太阳能电池的效率。更令人惊喜的是，该技术使用的材料成本较低，且环境友好，不产生有害废弃物。</p>
    
    <p>负责该项目的首席科学家张教授表示："这项技术的突破点在于我们发现了一种新型催化剂，它能够大幅提高能量转换效率。如果能够成功商业化，将彻底改变全球能源格局。"</p>
    
    <p>多位业内专家认为，这一技术若能大规模应用，将极大推动清洁能源产业发展，对缓解全球气候变化具有重要意义。</p>
    
    <p>目前，研究团队已申请多项专利，并计划在未来两年内与能源企业合作，推动技术产业化。预计首批基于该技术的商业产品将于2025年问世。</p>
  `,
  tags: ['科技', '新能源', '环保', '创新'],
  views: '12.5万',
  likes: 2300,
  collects: 568,
  shares: 421,
  comments: [
    {
      id: 1,
      username: '科技爱好者',
      avatar: 'https://img01.yzcdn.cn/vant/cat.jpeg',
      content: '这项技术如果真的能实现商业化，将是解决能源问题的重大突破！期待后续发展。',
      time: '1小时前',
      likes: 356
    },
    {
      id: 2,
      username: '环保卫士',
      avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
      content: '清洁能源是未来的必然趋势，支持这样的科研创新，希望能尽快投入实际应用。',
      time: '1.5小时前',
      likes: 289
    },
    {
      id: 3,
      username: '理性思考者',
      avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-3.jpeg',
      content: '虽然技术看起来很有前景，但从实验室到市场还有很长的路要走，建议大家保持理性乐观的态度。',
      time: '2小时前',
      likes: 176
    }
  ]
})

// 注释掉请求相关代码
// const fetchNewsDetail = (id) => {
//   // 实际项目中应该从API获取详情数据
//   console.log('获取新闻ID:', id)
//   // 这里使用模拟数据
// }

// onMounted(() => {
//   // 获取路由参数中的新闻ID
//   const newsId = route.params.id
//   if (newsId) {
//     fetchNewsDetail(newsId)
//   }
// })
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
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
}

.share-icon {
  padding: 4px 16px;
}
</style> 