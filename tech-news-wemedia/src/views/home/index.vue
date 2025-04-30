<template>
  <div class="home-container">
    <a-card :bordered="false">
        <div class="welcome-section">
      <h1>欢迎使用 tech-news 自媒体平台</h1>
      <p>这里是您管理内容和查看数据的中心</p>
    </div>
    
    <div class="stats-overview">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card hoverable>
            <template #cover>
              <FileTextOutlined style="font-size: 48px; color: #1890ff; margin: 24px 0;" />
            </template>
            <a-card-meta title="内容管理">
              <template #description>管理您的文章和素材</template>
            </a-card-meta>
            <a-button type="primary" style="margin-top: 16px; width: 100%;" @click="$router.push('/content/index')">
              查看文章
            </a-button>
          </a-card>
        </a-col>
        
        <a-col :span="6">
          <a-card hoverable>
            <template #cover>
              <BarChartOutlined style="font-size: 48px; color: #52c41a; margin: 24px 0;" />
            </template>
            <a-card-meta title="数据统计">
              <template #description>查看内容表现数据</template>
            </a-card-meta>
            <a-button type="primary" style="margin-top: 16px; width: 100%;" @click="$router.push('/statistics')">
              查看统计
            </a-button>
          </a-card>
        </a-col>
        
        <a-col :span="6">
          <a-card hoverable>
            <template #cover>
              <TeamOutlined style="font-size: 48px; color: #fa8c16; margin: 24px 0;" />
            </template>
            <a-card-meta title="粉丝管理">
              <template #description>了解您的粉丝画像</template>
            </a-card-meta>
            <a-button type="primary" style="margin-top: 16px; width: 100%;" @click="$router.push('/fans/index')">
              查看粉丝
            </a-button>
          </a-card>
        </a-col>
        
        <a-col :span="6">
          <a-card hoverable>
            <template #cover>
              <CommentOutlined style="font-size: 48px; color: #722ed1; margin: 24px 0;" />
            </template>
            <a-card-meta title="评论管理">
              <template #description>管理文章评论互动</template>
            </a-card-meta>
            <a-button type="primary" style="margin-top: 16px; width: 100%;" @click="$router.push('/comment/dashboard')">
              查看评论
            </a-button>
          </a-card>
        </a-col>
      </a-row>
    </div>

    </a-card>
    
  </div>
</template>

<script setup>
import { 
  FileTextOutlined, 
  BarChartOutlined, 
  TeamOutlined, 
  CommentOutlined,
  PlusOutlined,
  PictureOutlined,
  LineChartOutlined
} from '@ant-design/icons-vue'
</script>

<style lang="scss" scoped>
.home-container {
  padding: 0 24px;
  
  .welcome-section {
    margin-bottom: 48px;
    text-align: center;
    
    h1 {
      font-size: 28px;
      font-weight: 600;
      margin-bottom: 16px;
    }
    
    p {
      font-size: 16px;
      color: rgba(0, 0, 0, 0.45);
    }
  }
  
  .stats-overview {
    margin-bottom: 48px;
    
    .ant-card {
      text-align: center;
      
      :deep(.ant-card-meta-title) {
        font-size: 18px;
      }
    }
  }
  
  .quick-actions {
    margin: 24px 0;
  }
}
</style> 