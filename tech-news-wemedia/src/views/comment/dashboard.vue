<template>
  <div class="comment-dashboard">
    <a-card :bordered="false">
      <!-- 统计卡片区域 -->
      <a-row :gutter="24" class="stat-row">
        <a-col :span="6">
          <div class="stat-card blue">
            <div class="icon-wrapper">
              <CommentOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">5,687</div>
              <div class="stat-title">总评论数</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card yellow">
            <div class="icon-wrapper">
              <ExclamationCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">87</div>
              <div class="stat-title">待审核</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card green">
            <div class="icon-wrapper">
              <RiseOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">324</div>
              <div class="stat-title">本周新增</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card purple">
            <div class="icon-wrapper">
              <LikeOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">68.3</div>
              <div class="stat-title">互动率</div>
            </div>
          </div>
        </a-col>
      </a-row>
      
      <!-- 趋势图表 -->
      <div class="chart-container">
        <div class="chart-header">
          <h3>评论趋势</h3>
          <a-radio-group v-model:value="timeRange" button-style="solid" @change="updateTrendChart">
            <a-radio-button value="7">近7天</a-radio-button>
            <a-radio-button value="30">近30天</a-radio-button>
          </a-radio-group>
        </div>
        <div id="trendChart" class="chart" style="height: 300px;"></div>
      </div>
      
      <!-- 情感分析 -->
      <a-row :gutter="24" class="chart-row">
        <a-col :span="12">
          <a-card :bordered="false">
            <div id="sentimentChart" class="chart" style="height: 240px;"></div>
            <div class="chart-footer">
              <div class="stat-item">
                <span class="label">正面评论：</span>
                <span class="value">65%</span>
              </div>
              <div class="stat-item">
                <span class="label">中性评论：</span>
                <span class="value">25%</span>
              </div>
              <div class="stat-item">
                <span class="label">负面评论：</span>
                <span class="value">10%</span>
              </div>
            </div>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card :bordered="false">
            <a-list :data-source="hotArticles" :pagination="false">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta :title="item.title">
                    <template #description>
                      <span>评论数：{{ item.comments }}</span>
                      <a-divider type="vertical" />
                      <span>点赞率：{{ item.likeRate }}%</span>
                    </template>
                  </a-list-item-meta>
                  <div>
                    <a-tag :color="getCommentLevelColor(item.commentLevel)">
                      {{ getCommentLevelText(item.commentLevel) }}
                    </a-tag>
                  </div>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
      </a-row>
      
      <!-- 最新评论 -->
      <a-card :bordered="false" class="recent-comment-card">
        <a-list
          :data-source="recentComments"
          :pagination="{ pageSize: 5 }"
          item-layout="horizontal"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #avatar>
                  <a-avatar :src="item.avatar" />
                </template>
                <template #title>
                  <span>{{ item.username }}</span>
                  <a-tag :color="getCommentStatusColor(item.status)" class="status-tag">
                    {{ item.status }}
                  </a-tag>
                </template>
                <template #description>
                  <span>{{ item.content }}</span>
                  <div class="comment-meta">
                    <span>{{ item.time }}</span>
                    <a-divider type="vertical" />
                    <span>评论文章：{{ item.article }}</span>
                  </div>
                </template>
              </a-list-item-meta>
              <div class="comment-actions">
                <a-space>
                  <a-button size="small" @click="() => approveComment(item)">
                    <template #icon><CheckOutlined /></template>
                    通过
                  </a-button>
                  <a-button size="small" @click="() => replyComment(item)">
                    <template #icon><MessageOutlined /></template>
                    回复
                  </a-button>
                  <a-button size="small" danger @click="() => deleteComment(item)">
                    <template #icon><DeleteOutlined /></template>
                    删除
                  </a-button>
                </a-space>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </a-card>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { message } from 'ant-design-vue'
import { 
  CommentOutlined,
  ExclamationCircleOutlined,
  RiseOutlined,
  LikeOutlined,
  CheckOutlined,
  MessageOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue'
import * as echarts from 'echarts'

// 时间范围选择
const timeRange = ref('7')

// 热门评论文章
const hotArticles = [
  { 
    title: '5G技术将如何改变我们的生活？', 
    comments: 156, 
    likeRate: 92, 
    commentLevel: 'high' 
  },
  { 
    title: '人工智能在医疗领域的应用前景', 
    comments: 124, 
    likeRate: 87, 
    commentLevel: 'high' 
  },
  { 
    title: '区块链技术简介：原理与应用', 
    comments: 98, 
    likeRate: 78, 
    commentLevel: 'medium' 
  },
  { 
    title: '云计算服务对比：AWS vs Azure vs Google Cloud', 
    comments: 76, 
    likeRate: 81, 
    commentLevel: 'medium' 
  },
  { 
    title: '网络安全入门：保护个人信息的基本策略', 
    comments: 65, 
    likeRate: 85, 
    commentLevel: 'medium' 
  }
]

// 最新评论
const recentComments = [
  {
    id: 1,
    username: '张三',
    avatar: 'https://joeschmoe.io/api/v1/random',
    content: '这篇文章写得非常好，内容深入浅出，很容易理解。',
    time: '2023-03-15 14:30',
    article: '5G技术将如何改变我们的生活？',
    status: '已审核'
  },
  {
    id: 2,
    username: '李四',
    avatar: 'https://joeschmoe.io/api/v1/jane',
    content: '感谢作者的分享，学到了很多新知识。',
    time: '2023-03-15 13:25',
    article: '人工智能在医疗领域的应用前景',
    status: '待审核'
  },
  {
    id: 3,
    username: '王五',
    avatar: 'https://joeschmoe.io/api/v1/john',
    content: '文章有些观点我不太认同，希望作者能进一步解释。',
    time: '2023-03-15 11:40',
    article: '区块链技术简介：原理与应用',
    status: '已审核'
  },
  {
    id: 4,
    username: '赵六',
    avatar: 'https://joeschmoe.io/api/v1/jack',
    content: '非常精彩的分析，对我帮助很大。',
    time: '2023-03-15 10:18',
    article: '云计算服务对比：AWS vs Azure vs Google Cloud',
    status: '已审核'
  },
  {
    id: 5,
    username: '钱七',
    avatar: 'https://joeschmoe.io/api/v1/jill',
    content: '这篇文章内容太浅显了，希望能有更深入的分析。',
    time: '2023-03-15 09:05',
    article: '网络安全入门：保护个人信息的基本策略',
    status: '待审核'
  }
]

// 获取评论级别对应的颜色
const getCommentLevelColor = (level) => {
  const colorMap = {
    high: '#f5222d',
    medium: '#faad14',
    low: '#52c41a'
  }
  return colorMap[level] || '#1890ff'
}

// 获取评论级别对应的文本
const getCommentLevelText = (level) => {
  const textMap = {
    high: '热门',
    medium: '一般',
    low: '冷门'
  }
  return textMap[level] || '未知'
}

// 获取评论状态对应的颜色
const getCommentStatusColor = (status) => {
  return status === '已审核' ? 'green' : 'orange'
}

// 批准评论
const approveComment = (comment) => {
  message.success(`评论"${comment.content.substring(0, 10)}..."已通过审核`)
}

// 回复评论
const replyComment = (comment) => {
  message.success(`正在回复"${comment.username}"的评论`)
}

// 删除评论
const deleteComment = (comment) => {
  message.success(`评论"${comment.content.substring(0, 10)}..."已删除`)
}

// 图表实例
const trendChart = ref(null)
const sentimentChart = ref(null)

// 初始化趋势图表
const initTrendChart = () => {
  trendChart.value = echarts.init(document.getElementById('trendChart'))
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '评论数',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        areaStyle: {
          opacity: 0.1
        },
        itemStyle: {
          color: '#1890ff'
        }
      }
    ]
  }
  trendChart.value.setOption(option)
}

// 初始化情感分析图表
const initSentimentChart = () => {
  sentimentChart.value = echarts.init(document.getElementById('sentimentChart'))
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0
    },
    series: [
      {
        name: '情感分析',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '20',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 65, name: '正面评论', itemStyle: { color: '#52c41a' } },
          { value: 25, name: '中性评论', itemStyle: { color: '#1890ff' } },
          { value: 10, name: '负面评论', itemStyle: { color: '#ff4d4f' } }
        ]
      }
    ]
  }
  sentimentChart.value.setOption(option)
}

// 更新趋势图表数据
const updateTrendChart = () => {
  // 这里可以根据 timeRange 的值请求不同时间范围的数据
  // 示例数据
  const data = {
    '7': {
<<<<<<< HEAD
      dates: ['4-1', '4-2', '4-3', '4-4', '4-5', '4-6', '4-7'],
=======
      dates: ['3-1', '3-2', '3-3', '3-4', '3-5', '3-6', '3-7'],
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
      values: [120, 132, 101, 134, 90, 230, 210]
    },
    '30': {
      dates: Array.from({length: 30}, (_, i) => `${i + 1}日`),
      values: Array.from({length: 30}, () => Math.floor(Math.random() * 200 + 100))
    }
  }

  const currentData = data[timeRange.value]
  trendChart.value.setOption({
    xAxis: {
      data: currentData.dates
    },
    series: [{
      data: currentData.values
    }]
  })
}

// 监听窗口大小变化
const handleResize = () => {
  trendChart.value?.resize()
  sentimentChart.value?.resize()
}

onMounted(() => {
  initTrendChart()
  initSentimentChart()
  window.addEventListener('resize', handleResize)
  
  // 初始更新趋势图
  updateTrendChart()
})

// 组件卸载时移除事件监听
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.comment-dashboard {
  .stat-row {
    margin-bottom: 24px;
  }

  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    .icon-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 50%;
      margin-right: 16px;
      font-size: 24px;
    }

    .stat-content {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        line-height: 1.2;
        margin-bottom: 4px;
      }

      .stat-title {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
      }
    }

    &.blue {
      .icon-wrapper {
        background-color: rgba(24, 144, 255, 0.1);
        color: #1890ff;
      }
      .stat-value {
        color: #1890ff;
      }
    }

    &.green {
      .icon-wrapper {
        background-color: rgba(82, 196, 26, 0.1);
        color: #52c41a;
      }
      .stat-value {
        color: #52c41a;
      }
    }

    &.yellow {
      .icon-wrapper {
        background-color: rgba(250, 173, 20, 0.1);
        color: #faad14;
      }
      .stat-value {
        color: #faad14;
      }
    }

    &.purple {
      .icon-wrapper {
        background-color: rgba(114, 46, 209, 0.1);
        color: #722ed1;
      }
      .stat-value {
        color: #722ed1;
      }
    }
  }
  
  .chart-container {
    margin-top: 24px;
    margin-bottom: 24px;
    
    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h3 {
        margin-bottom: 0;
        color: rgba(0, 0, 0, 0.85);
        font-weight: 500;
      }
    }
    
    .chart {
      border-radius: 4px;
    }
  }
  
  .chart-row {
    margin-bottom: 24px;
  }
  
  .chart-footer {
    margin-top: 16px;
    display: flex;
    flex-wrap: wrap;
    
    .stat-item {
      flex: 1;
      min-width: 33%;
      
      .label {
        color: rgba(0, 0, 0, 0.45);
      }
      
      .value {
        font-weight: 500;
        color: rgba(0, 0, 0, 0.85);
      }
    }
  }
  
  .recent-comment-card {
    .status-tag {
      margin-left: 8px;
    }
    
    .comment-meta {
      margin-top: 4px;
      color: rgba(0, 0, 0, 0.45);
      font-size: 12px;
    }
    
    .comment-actions {
      display: flex;
      align-items: center;
    }
  }
}
</style> 