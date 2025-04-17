<template>
  <div class="article-rank">
    <a-card title="文章排行" :bordered="false">
      <a-tabs v-model:activeKey="activeTab">
        <a-tab-pane key="read" tab="阅读榜">
          <a-spin :spinning="loading">
            <a-list
              :data-source="articleList"
              :pagination="false"
            >
              <template #renderItem="{ item, index }">
                <a-list-item class="list-item">
                  <a-list-item-meta>
                    <template #title>
                      <div class="title-wrapper">
                        <div class="rank-number" :class="index < 3 ? 'top' : ''">{{ index + 1 }}</div>
                        <div class="title">{{ item.title }}</div>
                      </div>
                    </template>
                    <template #description>
                      <div class="meta-info">
                        <span class="time">{{ item.publishTime }}</span>
                        <a-divider type="vertical" />
                        <span class="category">{{ item.category }}</span>
                      </div>
                    </template>
                  </a-list-item-meta>
                  <div class="count-info">
                    <EyeOutlined /> {{ item.readCount }}
                  </div>
                </a-list-item>
              </template>
            </a-list>
          </a-spin>
        </a-tab-pane>
        <a-tab-pane key="comment" tab="评论榜">
          <a-spin :spinning="loading">
            <a-list
              :data-source="articleList"
              :pagination="false"
            >
              <template #renderItem="{ item, index }">
                <a-list-item class="list-item">
                  <a-list-item-meta>
                    <template #title>
                      <div class="title-wrapper">
                        <div class="rank-number" :class="index < 3 ? 'top' : ''">{{ index + 1 }}</div>
                        <div class="title">{{ item.title }}</div>
                      </div>
                    </template>
                    <template #description>
                      <div class="meta-info">
                        <span class="time">{{ item.publishTime }}</span>
                        <a-divider type="vertical" />
                        <span class="category">{{ item.category }}</span>
                      </div>
                    </template>
                  </a-list-item-meta>
                  <div class="count-info">
                    <CommentOutlined /> {{ item.commentCount }}
                  </div>
                </a-list-item>
              </template>
            </a-list>
          </a-spin>
        </a-tab-pane>
        <a-tab-pane key="like" tab="点赞榜">
          <a-spin :spinning="loading">
            <a-list
              :data-source="articleList"
              :pagination="false"
            >
              <template #renderItem="{ item, index }">
                <a-list-item class="list-item">
                  <a-list-item-meta>
                    <template #title>
                      <div class="title-wrapper">
                        <div class="rank-number" :class="index < 3 ? 'top' : ''">{{ index + 1 }}</div>
                        <div class="title">{{ item.title }}</div>
                      </div>
                    </template>
                    <template #description>
                      <div class="meta-info">
                        <span class="time">{{ item.publishTime }}</span>
                        <a-divider type="vertical" />
                        <span class="category">{{ item.category }}</span>
                      </div>
                    </template>
                  </a-list-item-meta>
                  <div class="count-info">
                    <LikeOutlined /> {{ item.likeCount }}
                  </div>
                </a-list-item>
              </template>
            </a-list>
          </a-spin>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { EyeOutlined, CommentOutlined, LikeOutlined } from '@ant-design/icons-vue'
import { getArticleRank } from '@/api/statistics'

// 当前选中的标签页
const activeTab = ref('read')
// 加载状态
const loading = ref(false)
// 文章列表
const articleList = ref([])

// 获取排行数据
const fetchRankData = async (type) => {
  loading.value = true
  try {
    // 实际项目中应该调用API
    // const res = await getArticleRank({ type })
    
    // 模拟数据
    const mockData = []
    
    for (let i = 0; i < 10; i++) {
      mockData.push({
        id: i + 1,
        title: `文章标题 ${i + 1}：这是一篇关于前端技术的文章，探讨了Vue3的新特性`,
        publishTime: '2023-03-30 15:30',
        category: '前端开发',
        readCount: Math.floor(Math.random() * 1000) + 500,
        commentCount: Math.floor(Math.random() * 100) + 10,
        likeCount: Math.floor(Math.random() * 200) + 30
      })
    }
    
    // 根据当前选中的标签页排序
    if (type === 'read') {
      mockData.sort((a, b) => b.readCount - a.readCount)
    } else if (type === 'comment') {
      mockData.sort((a, b) => b.commentCount - a.commentCount)
    } else if (type === 'like') {
      mockData.sort((a, b) => b.likeCount - a.likeCount)
    }
    
    articleList.value = mockData
  } catch (error) {
    message.error('获取排行数据失败')
  } finally {
    loading.value = false
  }
}

// 监听标签页切换
watch(() => activeTab.value, (newValue) => {
  fetchRankData(newValue)
})

// 初始化
onMounted(() => {
  fetchRankData(activeTab.value)
})
</script>

<style lang="scss" scoped>
.article-rank {
  margin-bottom: 24px;
  
  :deep(.ant-card-head) {
    border-bottom: 1px solid #f0f0f0;
    
    .ant-card-head-title {
      font-size: 18px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
    }
  }
  
  .list-item {
    padding: 12px 0;
    
    .title-wrapper {
      display: flex;
      align-items: center;
      
      .rank-number {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background-color: #f0f0f0;
        color: rgba(0, 0, 0, 0.65);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 600;
        margin-right: 12px;
        
        &.top {
          background-color: #1890ff;
          color: #fff;
        }
      }
      
      .title {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.85);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        max-width: 400px;
      }
    }
    
    .meta-info {
      font-size: 12px;
      color: rgba(0, 0, 0, 0.45);
      margin-left: 36px;
      
      .time, .category {
        margin-right: 8px;
      }
    }
    
    .count-info {
      font-size: 14px;
      color: rgba(0, 0, 0, 0.65);
      
      .anticon {
        margin-right: 4px;
        color: #1890ff;
      }
    }
  }
}
</style> 