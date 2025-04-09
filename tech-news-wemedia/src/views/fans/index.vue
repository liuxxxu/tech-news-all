<template>
  <div class="fans-container">
    <a-card :bordered="false">
        <!-- 统计卡片 -->
    <div class="statistics-cards">
      <a-row :gutter="16">
        <a-col :span="6">
          <div class="stat-card blue">
            <div class="icon-wrapper">
              <TeamOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">12,836</div>
              <div class="stat-title">粉丝总数</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card green">
            <div class="icon-wrapper">
              <UserAddOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">365</div>
              <div class="stat-title">新增粉丝</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card purple">
            <div class="icon-wrapper">
              <ThunderboltOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">8,954</div>
              <div class="stat-title">活跃粉丝</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card yellow">
            <div class="icon-wrapper">
              <InteractionOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">2,318</div>
              <div class="stat-title">互动总数</div>
            </div>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 粉丝数据列表 -->
    <a-card :bordered="false" class="data-table-card">
      <a-table
        :columns="columns"
        :data-source="fansData"
        :rowKey="record => record.id"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <!-- 日期列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'date'">
            <span>{{ record.date }}</span>
          </template>
          
          <!-- 粉丝增量列 -->
          <template v-else-if="column.key === 'increment'">
            <span :class="{ 'up-text': record.increment > 0, 'down-text': record.increment < 0 }">
              {{ record.increment > 0 ? '+' : '' }}{{ record.increment }}
            </span>
          </template>
          
          <!-- 操作列 -->
          <template v-else-if="column.key === 'action'">
            <a @click="() => showDetail(record)">查看详情</a>
          </template>
        </template>
      </a-table>
    </a-card>
    </a-card>
    

    <!-- 详情弹窗 -->
    <a-modal
      v-model:visible="detailVisible"
      title="粉丝详细数据"
      width="700px"
      :footer="null"
    >
      <template v-if="currentDetail">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="日期" :span="2">{{ currentDetail.date }}</a-descriptions-item>
          <a-descriptions-item label="新增粉丝">{{ currentDetail.increment }}</a-descriptions-item>
          <a-descriptions-item label="流失粉丝">{{ currentDetail.loss }}</a-descriptions-item>
          <a-descriptions-item label="活跃粉丝">{{ currentDetail.active }}</a-descriptions-item>
          <a-descriptions-item label="互动量">{{ currentDetail.interaction }}</a-descriptions-item>
          <a-descriptions-item label="主要来源" :span="2">{{ currentDetail.source }}</a-descriptions-item>
          <a-descriptions-item label="备注" :span="2">{{ currentDetail.notes }}</a-descriptions-item>
        </a-descriptions>
        
        <div class="detail-chart" id="fansTrendChart"></div>
      </template>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import { getFansStatistic } from '@/api/fans'
import * as echarts from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { GridComponent, TooltipComponent, TitleComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { TeamOutlined, UserAddOutlined, ThunderboltOutlined, InteractionOutlined } from '@ant-design/icons-vue'

// 注册 ECharts 组件
echarts.use([GridComponent, TooltipComponent, TitleComponent, LegendComponent, LineChart, CanvasRenderer])

// 状态变量
const loading = ref(false)
const detailVisible = ref(false)
const currentDetail = ref(null)
let trendChart = null

// 统计数据
const statisticsData = reactive([
  { title: '粉丝总数', value: '12,836', change: 3.2 },
  { title: '新增粉丝', value: '365', change: 12.5 },
  { title: '活跃粉丝', value: '8,954', change: -2.1 },
  { title: '互动总数', value: '2,318', change: 8.7 }
])

// 表格列定义
const columns = [
  {
    title: '日期',
    dataIndex: 'date',
    key: 'date',
    sorter: true
  },
  {
    title: '粉丝总数',
    dataIndex: 'total',
    key: 'total',
    sorter: true
  },
  {
    title: '粉丝增量',
    key: 'increment',
    dataIndex: 'increment',
    sorter: true
  },
  {
    title: '活跃粉丝',
    dataIndex: 'active',
    key: 'active',
    sorter: true
  },
  {
    title: '互动量',
    dataIndex: 'interaction',
    key: 'interaction',
    sorter: true
  },
  {
    title: '操作',
    key: 'action'
  }
]

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`
})

// 模拟粉丝数据
const fansData = ref([
  {
    id: 1,
    date: '2023-03-15',
    total: 12836,
    increment: 125,
    loss: 32,
    active: 8954,
    interaction: 2318,
    source: '站内推荐',
    notes: '当日发布内容获得较高点赞量'
  },
  {
    id: 2,
    date: '2023-03-14',
    total: 12711,
    increment: 98,
    loss: 41,
    active: 8876,
    interaction: 2134,
    source: '搜索引擎',
    notes: '技术类文章带来较多新增粉丝'
  },
  {
    id: 3,
    date: '2023-03-13',
    total: 12613,
    increment: 156,
    loss: 28,
    active: 9012,
    interaction: 2453,
    source: '社交分享',
    notes: '热门话题讨论引发粉丝增长'
  },
  {
    id: 4,
    date: '2023-03-12',
    total: 12457,
    increment: 76,
    loss: 53,
    active: 8721,
    interaction: 1987,
    source: '站内推荐',
    notes: '周末活跃度下降'
  },
  {
    id: 5,
    date: '2023-03-11',
    total: 12381,
    increment: -23,
    loss: 87,
    active: 8654,
    interaction: 1832,
    source: '直接访问',
    notes: '粉丝净流失，需关注原因'
  }
])

// 组件挂载时执行
onMounted(() => {
  loadFansData()
})

// 加载粉丝数据
const loadFansData = async () => {
  loading.value = true
  
  try {
    // 这里应该是实际的API调用
    // const res = await getFansStatistic({ page: pagination.current, limit: pagination.pageSize })
    // fansData.value = res.data.items
    // pagination.total = res.data.total

    // 假数据处理
    setTimeout(() => {
      pagination.total = fansData.value.length
      loading.value = false
    }, 500)
  } catch (error) {
    console.error('获取粉丝数据失败:', error)
    message.error('获取粉丝数据失败，请稍后重试')
    loading.value = false
  }
}

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  
  // 排序处理
  if (sorter && sorter.field) {
    const order = sorter.order === 'ascend' ? 'asc' : 'desc'
    // 传递排序参数到API
  }
  
  loadFansData()
}

// 查看详情
const showDetail = (record) => {
  currentDetail.value = record
  detailVisible.value = true
  
  // 在下一个渲染周期初始化图表
  setTimeout(() => {
    initTrendChart()
  }, 0)
}

// 初始化趋势图表
const initTrendChart = () => {
  if (!currentDetail.value) return
  
  // 如果图表已存在，先销毁
  if (trendChart) {
    trendChart.dispose()
  }
  
  // 获取图表DOM
  const chartDom = document.getElementById('fansTrendChart')
  if (!chartDom) return
  
  trendChart = echarts.init(chartDom)
  
  // 模拟7天的历史数据
  const dates = []
  const totalData = []
  const incrementData = []
  const activeData = []
  
  // 获取当前记录日期
  const currentDate = new Date(currentDetail.value.date)
  
  // 生成前7天的日期和模拟数据
  for (let i = 6; i >= 0; i--) {
    const date = new Date(currentDate)
    date.setDate(date.getDate() - i)
    const dateStr = `${date.getMonth() + 1}/${date.getDate()}`
    dates.push(dateStr)
    
    // 模拟数据
    const baseTotal = currentDetail.value.total - Math.floor(Math.random() * 500) - (i * 100)
    totalData.push(baseTotal)
    incrementData.push(Math.floor(Math.random() * 200) - 50)
    activeData.push(Math.floor(baseTotal * 0.7) + Math.floor(Math.random() * 500))
  }
  
  // 设置图表选项
  const option = {
    title: {
      text: '近7天粉丝趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['粉丝总数', '粉丝增量', '活跃粉丝'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: [
      {
        type: 'value',
        name: '总数',
        position: 'left'
      },
      {
        type: 'value',
        name: '增量',
        position: 'right'
      }
    ],
    series: [
      {
        name: '粉丝总数',
        type: 'line',
        data: totalData,
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#1890ff'
        }
      },
      {
        name: '粉丝增量',
        type: 'line',
        yAxisIndex: 1,
        data: incrementData,
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#52c41a'
        }
      },
      {
        name: '活跃粉丝',
        type: 'line',
        data: activeData,
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#faad14'
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 监听弹窗关闭，销毁图表
watch(detailVisible, (newVal) => {
  if (!newVal && trendChart) {
    trendChart.dispose()
    trendChart = null
  }
})
</script>

<style lang="scss" scoped>
.fans-container {
  padding: 0 16px;
  
  .statistics-cards {
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
  
  .data-table-card {
    margin-bottom: 24px;
    
    .up-text {
      color: #52c41a;
    }
    
    .down-text {
      color: #ff4d4f;
    }
  }
  
  .detail-chart {
    margin-top: 24px;
    height: 300px;
  }
}
</style> 