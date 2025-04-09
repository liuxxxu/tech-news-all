<template>
  <div class="user-portrait">
    <a-card title="粉丝画像" :bordered="false">
      <a-row :gutter="16">
        <a-col :span="12">
          <div class="chart-container" ref="genderChartRef"></div>
        </a-col>
        <a-col :span="12">
          <div class="chart-container" ref="ageChartRef"></div>
        </a-col>
      </a-row>
      <a-row :gutter="16" style="margin-top: 20px">
        <a-col :span="12">
          <div class="chart-container" ref="locationChartRef"></div>
        </a-col>
        <a-col :span="12">
          <div class="chart-container" ref="deviceChartRef"></div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getPortraitStatistics } from '@/api/statistics'

// 图表实例
let genderChart = null
let ageChart = null
let locationChart = null
let deviceChart = null

// DOM引用
const genderChartRef = ref(null)
const ageChartRef = ref(null)
const locationChartRef = ref(null)
const deviceChartRef = ref(null)

// 初始化性别分布图表
const initGenderChart = () => {
  if (!genderChartRef.value) return
  
  genderChart = echarts.init(genderChartRef.value)
  
  const option = {
    title: {
      text: '性别分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: ['男性', '女性', '未知']
    },
    color: ['#1890ff', '#F759AB', '#BFBFBF'],
    series: [
      {
        name: '性别分布',
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
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: 60, name: '男性' },
          { value: 35, name: '女性' },
          { value: 5, name: '未知' }
        ]
      }
    ]
  }
  
  genderChart.setOption(option)
}

// 初始化年龄分布图表
const initAgeChart = () => {
  if (!ageChartRef.value) return
  
  ageChart = echarts.init(ageChartRef.value)
  
  const option = {
    title: {
      text: '年龄分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    color: ['#1890ff'],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: ['18岁以下', '18-24岁', '25-30岁', '31-40岁', '41-50岁', '50岁以上'],
        axisTick: {
          alignWithLabel: true
        }
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    series: [
      {
        name: '年龄段',
        type: 'bar',
        barWidth: '60%',
        data: [5, 30, 25, 20, 15, 5]
      }
    ]
  }
  
  ageChart.setOption(option)
}

// 初始化地区分布图表
const initLocationChart = () => {
  if (!locationChartRef.value) return
  
  locationChart = echarts.init(locationChartRef.value)
  
  const option = {
    title: {
      text: '地区分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: ['北京', '上海', '广州', '深圳', '杭州', '其他']
    },
    series: [
      {
        name: '地区分布',
        type: 'pie',
        radius: '65%',
        center: ['50%', '50%'],
        data: [
          { value: 20, name: '北京' },
          { value: 18, name: '上海' },
          { value: 15, name: '广州' },
          { value: 12, name: '深圳' },
          { value: 10, name: '杭州' },
          { value: 25, name: '其他' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  
  locationChart.setOption(option)
}

// 初始化设备分布图表
const initDeviceChart = () => {
  if (!deviceChartRef.value) return
  
  deviceChart = echarts.init(deviceChartRef.value)
  
  const option = {
    title: {
      text: '设备分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'normal'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      boundaryGap: [0, 0.01]
    },
    yAxis: {
      type: 'category',
      data: ['iPhone', 'Android', 'iPad', '桌面浏览器', '其他']
    },
    series: [
      {
        name: '设备数量',
        type: 'bar',
        data: [45, 40, 5, 8, 2],
        itemStyle: {
          color: function(params) {
            const colors = ['#1890ff', '#52c41a', '#fa8c16', '#722ed1', '#bfbfbf']
            return colors[params.dataIndex]
          }
        }
      }
    ]
  }
  
  deviceChart.setOption(option)
}

// 获取用户画像数据
const fetchPortraitData = async () => {
  try {
    // 实际项目中应该调用API
    // const res = await getPortraitStatistics()
    
    // 初始化各个图表
    initGenderChart()
    initAgeChart()
    initLocationChart()
    initDeviceChart()
    
    // 窗口大小变化时重新调整图表大小
    window.addEventListener('resize', () => {
      genderChart && genderChart.resize()
      ageChart && ageChart.resize()
      locationChart && locationChart.resize()
      deviceChart && deviceChart.resize()
    })
  } catch (error) {
    console.error('获取用户画像数据失败', error)
  }
}

// 初始化
onMounted(() => {
  // 延迟执行以确保DOM已渲染
  setTimeout(() => {
    fetchPortraitData()
  }, 100)
})
</script>

<style lang="scss" scoped>
.user-portrait {
  margin-bottom: 24px;
  
  :deep(.ant-card-head) {
    border-bottom: 1px solid #f0f0f0;
    
    .ant-card-head-title {
      font-size: 18px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
    }
  }
  
  .chart-container {
    height: 300px;
    width: 100%;
  }
}
</style> 