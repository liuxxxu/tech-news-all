<template>
  <div class="statistics-cards">
    <a-row :gutter="16">
      <a-col :span="6" v-for="(item, index) in statisticsData" :key="index">
        <a-card class="stat-card" :bordered="false">
          <div class="card-icon" :style="{ backgroundColor: item.bgColor }">
            <component :is="item.icon" :style="{ color: item.iconColor }" />
          </div>
          <div class="card-content">
            <div class="number">{{ item.number }}</div>
            <div class="text">{{ item.text }}</div>
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getCommentStatistic } from '@/api/comment'
import { 
  MessageOutlined, 
  CheckCircleOutlined, 
  CloseCircleOutlined, 
  StarOutlined 
} from '@ant-design/icons-vue'

// 统计数据
const statisticsData = ref([
  {
    icon: MessageOutlined,
    text: '总评论数',
    number: '0',
    bgColor: '#e8f4ff',
    iconColor: '#1890ff'
  },
  {
    icon: CheckCircleOutlined,
    text: '通过评论数',
    number: '0',
    bgColor: '#e8f7ee',
    iconColor: '#00a870'
  },
  {
    icon: CloseCircleOutlined,
    text: '拒绝评论数',
    number: '0',
    bgColor: '#fff2f0',
    iconColor: '#ff4d4f'
  },
  {
    icon: StarOutlined,
    text: '待审核评论数',
    number: '0',
    bgColor: '#fffbe8',
    iconColor: '#faad14'
  }
])

// 获取统计数据
const fetchStatisticsData = async () => {
  try {
    // 实际项目中应该调用API
    // const res = await getCommentStatistic()
    
    // 模拟数据
    const mockData = {
      total: 256,
      passed: 220,
      rejected: 26,
      pending: 10
    }
    
    // 更新数据
    statisticsData.value[0].number = mockData.total
    statisticsData.value[1].number = mockData.passed
    statisticsData.value[2].number = mockData.rejected
    statisticsData.value[3].number = mockData.pending
  } catch (error) {
    message.error('获取评论统计数据失败')
  }
}

// 初始化
onMounted(() => {
  fetchStatisticsData()
})
</script>

<style lang="scss" scoped>
.statistics-cards {
  margin-bottom: 20px;
  
  .stat-card {
    height: 100px;
    display: flex;
    align-items: center;
    border-radius: 8px;
    
    :deep(.ant-card-body) {
      padding: 16px;
      width: 100%;
      display: flex;
      align-items: center;
    }
    
    .card-icon {
      width: 48px;
      height: 48px;
      border-radius: 8px;
      display: flex;
      justify-content: center;
      align-items: center;
      margin-right: 16px;
      
      .anticon {
        font-size: 24px;
      }
    }
    
    .card-content {
      flex: 1;
      
      .number {
        font-size: 24px;
        font-weight: 600;
        color: rgba(0, 0, 0, 0.85);
        margin-bottom: 4px;
      }
      
      .text {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
      }
    }
  }
}
</style> 