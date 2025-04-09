<template>
  <div class="page-container">
    <div class="detail-header">
      <a-page-header
        :title="'返回'"
        @back="goBack"
        style="padding: 0"
      >
        <template #subTitle>文章详情</template>
      </a-page-header>
      
      <div class="status-info">
        <span class="status-label">状态：</span>
        <a-tag :color="newsDetail.enable ? 'success' : 'error'">
          {{ newsDetail.enable ? '已上架' : '已下架' }}
        </a-tag>
      </div>
    </div>
    
    <a-divider />
    
    <div class="detail-content">
      <a-row :gutter="24">
        <a-col :span="8">
          <div class="content-label">文章标题</div>
          <div class="content-value">{{ newsDetail.title }}</div>
        </a-col>
        <a-col :span="8">
          <div class="content-label">作者</div>
          <div class="content-value">{{ newsDetail.authorName }}</div>
        </a-col>
        <a-col :span="8">
          <div class="content-label">更新时间</div>
          <div class="content-value">{{ formatDateTime(newsDetail.submitedTime) }}</div>
        </a-col>
      </a-row>
      
      <a-divider />
      
      <div class="content-label" style="margin-bottom: 20px;">正文信息</div>
      
      <div class="article-content">
        <div v-for="(item, index) in contentList" :key="index" class="content-item">
          <div v-if="item.type === 'text'" class="text-content">{{ item.value }}</div>
          <img v-else :src="item.value" class="image-content" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useNewsStore } from '@/store/news'
import { getNewsDetail } from '@/api/news'
import { message } from 'ant-design-vue'

const router = useRouter()
const route = useRoute()
const newsStore = useNewsStore()

const id = ref('')
const newsDetail = ref({})
const loading = ref(false)
const contentList = computed(() => {
  try {
    return JSON.parse(newsDetail.value.content || '[]')
  } catch (e) {
    console.error('解析内容失败:', e)
    return []
  }
})

// 获取文章详情
const getDetail = async () => {
  const id = route.params.id
  if (!id) return
  
  loading.value = true
  try {
    const res = await getNewsDetail(id)
    if (res.code === 0) {
      newsDetail.value = res.data
    } else {
      message.error(res.errorMessage || '获取文章详情失败')
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    message.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getDetail()
})

// 日期格式化
const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString()
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.status-info {
  text-align: right;
}

.status-label {
  margin-right: 8px;
  color: rgba(0, 0, 0, 0.45);
}

.detail-content {
  margin-top: 20px;
}

.content-label {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
  margin-bottom: 8px;
}

.content-value {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 24px;
}

.article-content {
  margin-top: 16px;
}

.content-item {
  margin-bottom: 16px;
}

.text-content {
  font-size: 16px;
  line-height: 1.8;
  color: #17233d;
}

.image-content {
  max-width: 100%;
  height: auto;
  margin: 16px 0;
}
</style> 