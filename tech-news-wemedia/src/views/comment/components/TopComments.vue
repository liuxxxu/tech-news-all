<template>
  <div class="top-comments">
    <div class="top-title">
      <div class="text">热门评论</div>
      <a-button type="link" @click="loadMore">
        查看更多
        <RightOutlined />
      </a-button>
    </div>
    
    <div class="comments-list">
      <a-list
        :loading="loading"
        :data-source="list"
        item-layout="horizontal"
      >
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #avatar>
                <a-avatar :src="item.avatar" />
              </template>
              <template #title>
                <div class="comment-item-title">
                  <span class="author">{{ item.author }}</span>
                  <span class="time">{{ item.createdTime }}</span>
                </div>
              </template>
              <template #description>
                <div class="comment-item-content">
                  <div class="article">文章：{{ item.articleTitle }}</div>
                  <div class="content">{{ item.content }}</div>
                  <div class="metrics">
                    <span class="metric-item">
                      <LikeOutlined />
                      <span class="count">{{ item.likeCount }}</span>
                    </span>
                    <span class="metric-item">
                      <MessageOutlined />
                      <span class="count">{{ item.replyCount }}</span>
                    </span>
                  </div>
                </div>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { LikeOutlined, MessageOutlined, RightOutlined } from '@ant-design/icons-vue'
import { getTopCommentsList } from '@/api/comment'

// 列表数据
const list = ref([])
const loading = ref(false)

// 获取热门评论列表
const fetchTopComments = async () => {
  loading.value = true
  try {
    // 实际项目中应该调用API
    // const res = await getTopCommentsList({ limit: 5 })
    
    // 模拟数据
    const mockData = []
    
    // 生成模拟数据
    for (let i = 0; i < 5; i++) {
      mockData.push({
        id: i + 1,
        author: `热门用户${i + 1}`,
        avatar: `https://joeschmoe.io/api/v1/random${i + 1}`,
        articleTitle: `热门文章标题${i + 1}`,
        content: `这是热门评论内容${i + 1}，点赞数和回复数都很高，是一条优质评论...`,
        createdTime: '2023-03-30 15:30:45',
        likeCount: Math.floor(Math.random() * 100) + 50,
        replyCount: Math.floor(Math.random() * 20) + 10
      })
    }
    
    list.value = mockData
  } catch (error) {
    message.error('获取热门评论失败')
  } finally {
    loading.value = false
  }
}

// 查看更多
const loadMore = () => {
  message.info('查看更多热门评论')
}

// 初始化
onMounted(() => {
  fetchTopComments()
})
</script>

<style lang="scss" scoped>
.top-comments {
  background-color: #fff;
  padding: 24px;
  border-radius: 4px;
  margin-bottom: 24px;
  
  .top-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .text {
      font-size: 18px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
      position: relative;
      padding-left: 12px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background-color: #1890ff;
        border-radius: 2px;
      }
    }
  }
  
  .comments-list {
    .comment-item-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .author {
        font-weight: 600;
        color: rgba(0, 0, 0, 0.85);
      }
      
      .time {
        font-size: 12px;
        color: rgba(0, 0, 0, 0.45);
      }
    }
    
    .comment-item-content {
      .article {
        font-size: 13px;
        color: rgba(0, 0, 0, 0.65);
        margin-bottom: 8px;
      }
      
      .content {
        margin-bottom: 8px;
      }
      
      .metrics {
        display: flex;
        align-items: center;
        
        .metric-item {
          display: flex;
          align-items: center;
          margin-right: 16px;
          color: rgba(0, 0, 0, 0.45);
          
          .anticon {
            margin-right: 4px;
            font-size: 14px;
          }
          
          .count {
            font-size: 12px;
          }
        }
      }
    }
  }
}
</style> 