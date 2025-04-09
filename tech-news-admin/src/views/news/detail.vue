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
      
      <div v-if="newsDetail.status === 3" class="operation-buttons">
        <a-button type="primary" @click="handlePass">
          <template #icon><CheckOutlined /></template>
          通过
        </a-button>
        <a-button type="primary" danger class="ml-16" @click="handleFail">
          <template #icon><CloseOutlined /></template>
          驳回
        </a-button>
      </div>
      
      <div v-else-if="newsDetail.status === 8" class="status-info">
        <span class="status-label">状态：</span>
        <a-tag color="success">审核通过</a-tag>
      </div>
      
      <div v-else-if="newsDetail.status === 2" class="status-info">
        <div>
          <span class="status-label">状态：</span>
          <a-tag color="error">已驳回</a-tag>
        </div>
        <div class="reject-reason">
          理由：{{ newsDetail.reason }}
        </div>
      </div>
    </div>
    
    <a-divider />
    
    <div class="detail-content">
      <a-row :gutter="24">
        <a-col :span="6">
          <div class="content-label">文章标题</div>
          <div class="content-value">{{ newsDetail.title }}</div>
        </a-col>
        <a-col :span="6">
          <div class="content-label">作者</div>
          <div class="content-value">{{ newsDetail.authorName }}</div>
        </a-col>
        <a-col :span="6">
          <div class="content-label">创建时间</div>
          <div class="content-value">{{ formatDateTime(newsDetail.createdTime) }}</div>
        </a-col>
        <a-col :span="6">
          <div class="content-label">更新时间</div>
          <div class="content-value">{{ formatDateTime(newsDetail.updatedTime) }}</div>
        </a-col>
      </a-row>
      
      <a-divider />
      
      <div class="content-label" style="margin-bottom: 20px;">正文信息</div>
      
      <div class="article-content" v-html="newsDetail.content"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Modal, Input, message } from 'ant-design-vue'
import { CheckOutlined, CloseOutlined } from '@ant-design/icons-vue'
import { useNewsStore } from '@/stores/news'
import { getNewsDetail } from '@/api/news'

const router = useRouter()
const route = useRoute()
const newsStore = useNewsStore()

const newsDetail = ref({})
const loading = ref(false)
const reasonText = ref('')

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

// 通过
const handlePass = async () => {
  const success = await newsStore.passNews({ id: newsDetail.value.id, status: 4 })
  if (success) {
    await getDetail()
  }
}

// 驳回
const handleFail = () => {
  reasonText.value = ''
  
  Modal.confirm({
    title: '驳回原因',
    content: h('div', [
      h(Input.TextArea, {
        placeholder: '请输入驳回原因',
        rows: 4,
        onChange: (e) => {
          reasonText.value = e.target.value
        }
      })
    ]),
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      if (!reasonText.value.trim()) {
        message.warning('请输入驳回原因')
        return Promise.reject()
      }
      
      const success = await newsStore.failNews({ 
        id: newsDetail.value.id, 
        status: 2, 
        msg: reasonText.value 
      })
      
      if (success) {
        await getDetail()
      }
    }
  })
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

.reject-reason {
  margin-top: 8px;
  color: #f5222d;
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
  line-height: 1.8;
}

.article-content :deep(img) {
  max-width: 100%;
  height: auto;
  margin: 16px 0;
}

.article-content :deep(p) {
  margin-bottom: 16px;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin: 24px 0 16px;
  font-weight: 600;
}

.article-content :deep(blockquote) {
  margin: 16px 0;
  padding: 10px 20px;
  border-left: 4px solid #ddd;
  background-color: #f5f5f5;
}

.ml-16 {
  margin-left: 16px;
}
</style> 