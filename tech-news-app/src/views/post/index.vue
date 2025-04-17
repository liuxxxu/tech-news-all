<template>
  <div class="post-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      :title="isShare ? '分享文章' : '发布动态'"
      left-arrow
      @click-left="onBack"
      fixed
      left-arrow-color="#3478F6"
    >
      <template #right>
        <van-button 
          type="primary" 
          size="small" 
          :disabled="!content && !selectedImages.length && !isShare"
          @click="handlePublish"
        >
          发布
        </van-button>
      </template>
    </van-nav-bar>

    <!-- 内容区域 -->
    <div class="post-content">
      <!-- 文字输入区 -->
      <van-field
        v-model="content"
        type="textarea"
        :placeholder="isShare ? '说说你的想法...' : '分享新鲜事...'"
        rows="5"
        autosize
        class="content-input"
      />

      <!-- 分享的文章卡片 -->
      <div class="news-card" v-if="isShare" @click="goToDetail">
        <div class="news-card-content">
          <div class="news-title">{{ sharedNews.title }}</div>
          <div class="news-source">{{ sharedNews.source }}</div>
        </div>
        <van-image
          v-if="sharedNews.cover"
          width="80"
          height="80"
          fit="cover"
          radius="4"
          :src="sharedNews.cover"
          alt="新闻图片"
        />
      </div>

      <!-- 图片上传区 -->
      <van-uploader
        v-if="!isShare"
        v-model="selectedImages"
        :max-count="9"
        multiple
        :after-read="afterRead"
        @delete="onDelete"
      >
        <template #preview-cover="{ file }">
          <div class="preview-cover">
            <van-icon 
              name="clear" 
              class="preview-delete-icon"
              @click.stop="onDelete(file)"
            />
          </div>
        </template>
      </van-uploader>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast } from '@/utils/vant-ui'

const router = useRouter()
const route = useRoute()
const content = ref('')
const selectedImages = ref([])

// 判断是否是分享文章
const isShare = computed(() => route.query.type === 'share')

// 分享的文章信息
const sharedNews = computed(() => ({
  id: route.query.newsId,
  title: route.query.newsTitle,
  source: route.query.newsSource,
  cover: route.query.newsCover
}))

// 返回上一页
const onBack = () => {
  router.back()
}

// 跳转到文章详情
const goToDetail = () => {
  router.push(`/article/${sharedNews.value.id}`)
}

// 处理图片上传后的回调
const afterRead = (file) => {
  // 这里可以处理图片上传到服务器的逻辑
  console.log('上传的图片:', file)
}

// 删除图片
const onDelete = (file) => {
  const index = selectedImages.value.indexOf(file)
  if (index !== -1) {
    selectedImages.value.splice(index, 1)
  }
}

// 发布动态
const handlePublish = async () => {
  try {
    // 这里添加发布动态的API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccessToast('发布成功')
    router.back()
  } catch (error) {
    console.error('发布失败:', error)
    showToast('发布失败，请重试')
  }
}
</script>

<style scoped>
.post-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
}

.post-content {
  background-color: #fff;
  padding: 16px;
}

.content-input {
  margin-bottom: 16px;
}

.content-input :deep(.van-field__control) {
  font-size: 16px;
  line-height: 1.6;
}

.news-card {
  display: flex;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 16px;
  align-items: center;
  cursor: pointer;
}

.news-card-content {
  flex: 1;
  margin-right: 12px;
}

.news-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 6px;
  font-weight: 500;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.news-source {
  font-size: 12px;
  color: #999;
}

:deep(.van-uploader) {
  padding: 8px 0;
}

:deep(.van-uploader__preview) {
  margin: 0 8px 8px 0;
}

.preview-cover {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  display: flex;
  justify-content: flex-end;
  padding: 4px;
}

.preview-delete-icon {
  color: #fff;
  font-size: 18px;
  padding: 4px;
  background-color: rgba(0, 0, 0, 0.3);
  border-radius: 50%;
}

:deep(.van-nav-bar__right) .van-button {
  height: 28px;
  padding: 0 12px;
}

:deep(.van-nav-bar__right) .van-button--disabled {
  opacity: 0.5;
}
</style> 