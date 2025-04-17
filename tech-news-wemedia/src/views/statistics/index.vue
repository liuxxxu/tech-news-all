<template>
  <div class="statistics-container">
    <a-card :bordered="false">
        <!-- 时间筛选 -->
    <time-filter @filter="handleFilterChange" />
    
    <!-- 数据面板 -->
    <data-panel />
    
    <!-- 数据趋势 -->
    <data-trend />
    
    <a-row :gutter="16">
      <a-col :span="16">
        <!-- 文章排行 -->
        <article-rank />
      </a-col>
      <a-col :span="8">
        <!-- 其他统计信息，比如总体概况或平台活动 -->
        <a-card :bordered="false">
          <a-statistic 
            title="累计文章" 
            :value="platformStats.articles"
            :precision="0"
            style="margin-bottom: 20px"
          >
            <template #suffix>
              <span class="suffix-label">篇</span>
            </template>
          </a-statistic>
          
          <a-statistic 
            title="累计阅读" 
            :value="platformStats.reads"
            :precision="0"
            style="margin-bottom: 20px"
          >
            <template #suffix>
              <span class="suffix-label">次</span>
            </template>
          </a-statistic>
          
          <a-statistic 
            title="累计粉丝" 
            :value="platformStats.fans"
            :precision="0"
            style="margin-bottom: 20px"
          >
            <template #suffix>
              <span class="suffix-label">人</span>
            </template>
          </a-statistic>
          
          <a-statistic 
            title="累计收益" 
            :value="platformStats.income"
            :precision="2"
            style="margin-bottom: 20px"
          >
            <template #prefix>
              <span class="prefix-label">¥</span>
            </template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 用户画像 -->
    <user-portrait />
    </a-card>

  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import TimeFilter from './components/TimeFilter.vue'
import DataPanel from './components/DataPanel.vue'
import DataTrend from './components/DataTrend.vue'
import ArticleRank from './components/ArticleRank.vue'
import UserPortrait from './components/UserPortrait.vue'

// 筛选条件
const filterParams = reactive({
  timeRange: 'week',
  startDate: '',
  endDate: ''
})

// 平台统计数据
const platformStats = reactive({
  articles: 1253,
  reads: 135789,
  fans: 5624,
  income: 8563.45
})

// 处理筛选变化
const handleFilterChange = (params) => {
  Object.assign(filterParams, params)
  
  // 这里可以刷新统计数据
  message.success('数据筛选已应用')
}
</script>

<style lang="scss" scoped>
.statistics-container {
  padding: 0;
  min-height: 100vh;
  width: 100%;
  
  :deep(.ant-card) {
    margin: 0;
    width: 100%;
    
    .ant-card-body {
      padding: 20px;
      width: 100%;
      box-sizing: border-box;
    }
  }
  
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: rgba(0, 0, 0, 0.85);
    margin-bottom: 24px;
  }
  
  .suffix-label {
    margin-left: 4px;
    color: rgba(0, 0, 0, 0.45);
  }
  
  .prefix-label {
    margin-right: 4px;
    color: rgba(0, 0, 0, 0.45);
  }
}
</style> 