<template>
  <div class="data-trend">
    <a-card :bordered="false">
      <template #title>
        <div class="card-title">
          <span>数据趋势</span>
          <a-radio-group v-model:value="chartType" button-style="solid">
            <a-radio-button value="week">最近7天</a-radio-button>
            <a-radio-button value="month">最近30天</a-radio-button>
          </a-radio-group>
        </div>
      </template>
      <div class="chart-container" ref="chartRef"></div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getNewsStatistics } from '@/api/statistics'

// 图表类型
const chartType = ref('week')
// 图表实例
let chartInstance = null
const chartRef = ref(null)

// 初始化图表
const initChart = (type) => {
  if (!chartRef.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  // 生成日期和模拟数据
  const dates = []
  const readData = []
  const commentData = []
  const likeData = []
  
  // 根据选择的时间范围生成数据
  const days = type === 'week' ? 7 : 30
  
  // 生成日期和随机数据
  const now = new Date()
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(now.getTime() - i * 24 * 60 * 60 * 1000)
    const month = date.getMonth() + 1
    const day = date.getDate()
    dates.push(`${month}/${day}`)
    
    // 随机生成模拟数据
    readData.push(Math.floor(Math.random() * 1000) + 500)
    commentData.push(Math.floor(Math.random() * 100) + 20)
    likeData.push(Math.floor(Math.random() * 200) + 50)
  }
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['阅读量', '评论量', '点赞量'],
      right: 0,
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#E3E3E3'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      },
      splitLine: {
        lineStyle: {
          color: '#E3E3E3',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '阅读量',
        type: 'line',
        data: readData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#1890FF'
        },
        lineStyle: {
          width: 2
        }
      },
      {
        name: '评论量',
        type: 'line',
        data: commentData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#52C41A'
        },
        lineStyle: {
          width: 2
        }
      },
      {
        name: '点赞量',
        type: 'line',
        data: likeData,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#FA8C16'
        },
        lineStyle: {
          width: 2
        }
      }
    ]
  }
  
  chartInstance.setOption(option)
  
  // 自适应窗口大小变化
  window.addEventListener('resize', () => {
    chartInstance && chartInstance.resize()
  })
}

// 监听图表类型变化
watch(() => chartType.value, (newValue) => {
  initChart(newValue)
})

// 初始化
onMounted(() => {
  initChart(chartType.value)
})
</script>

<style lang="scss" scoped>
.data-trend {
  margin-bottom: 24px;
  
  :deep(.ant-card-head) {
    border-bottom: 1px solid #f0f0f0;
  }
  
  .card-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    span {
      font-size: 18px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
    }
  }
  
  .chart-container {
    height: 400px;
    width: 100%;
  }
}
</style> 