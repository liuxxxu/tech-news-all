<template>
  <div class="article-container">
    <a-card :bordered="false">
      <!-- 筛选区域 -->
      <div class="filter-container">
        <a-form layout="inline">
          <a-form-item label="状态">
            <a-select v-model:value="queryParams.status" style="width: 120px" @change="handleStatusChange">
              <a-select-option value="all">全部</a-select-option>
              <a-select-option value="published">已发布</a-select-option>
              <a-select-option value="draft">草稿</a-select-option>
            </a-select>
          </a-form-item>
          
          <a-form-item label="频道">
            <a-select v-model:value="queryParams.channel" style="width: 150px" @change="handleChannelChange">
              <a-select-option value="all">全部频道</a-select-option>
              <a-select-option v-for="item in channelOptions" :key="item.id" :value="item.id">
                {{ item.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          
          <a-form-item label="日期">
            <a-range-picker v-model:value="queryParams.dateRange" @change="handleDateChange" />
          </a-form-item>
          
          <a-form-item>
            <a-input-search
              v-model:value="queryParams.keyword"
              placeholder="请输入关键字搜索"
              style="width: 250px"
              @search="handleSearch"
            />
          </a-form-item>
        </a-form>
      </div>
      
      <!-- 文章列表 -->
      <a-table
        :columns="columns"
        :data-source="articleList"
        :rowKey="record => record.id"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <!-- 标题列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="article-title">
              <a-tag v-if="record.isTop" color="red">置顶</a-tag>
              <span class="title-text">{{ record.title }}</span>
            </div>
          </template>
          
          <!-- 封面列 -->
          <template v-else-if="column.key === 'cover'">
            <template v-if="record.cover">
              <a-image
                :width="80"
                :src="record.cover"
                :preview="false"
              />
            </template>
            <template v-else>
              <a-tag>无封面</a-tag>
            </template>
          </template>
          
          <!-- 状态列 -->
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.status === 'published' ? 'green' : 'orange'">
              {{ record.status === 'published' ? '已发布' : '草稿' }}
            </a-tag>
          </template>
          
          <!-- 操作列 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="() => handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link" @click.prevent>
                  更多 <down-outlined />
                </a>
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="preview" @click="() => handlePreview(record)">
                      <eye-outlined />预览
                    </a-menu-item>
                    <a-menu-item key="publish" v-if="record.status === 'draft'" @click="() => handlePublish(record)">
                      <check-circle-outlined />发布
                    </a-menu-item>
                    <a-menu-item key="offline" v-if="record.status === 'published'" @click="() => handleOffline(record)">
                      <stop-outlined />下线
                    </a-menu-item>
                    <a-menu-item key="top" v-if="!record.isTop" @click="() => handleTop(record)">
                      <vertical-align-top-outlined />置顶
                    </a-menu-item>
                    <a-menu-item key="cancelTop" v-if="record.isTop" @click="() => handleCancelTop(record)">
                      <vertical-align-middle-outlined />取消置顶
                    </a-menu-item>
                    <a-menu-item key="delete" @click="() => handleDelete(record)">
                      <delete-outlined />删除
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 预览弹窗 -->
    <a-modal
      v-model:visible="previewVisible"
      title="文章预览"
      width="800px"
      :footer="null"
    >
      <template v-if="currentArticle">
        <h2 style="text-align: center; margin-bottom: 20px;">{{ currentArticle.title }}</h2>
        <div class="article-info" style="text-align: center; color: #999; margin-bottom: 20px;">
          <span>{{ currentArticle.author }}</span>
          <a-divider type="vertical" />
          <span>{{ currentArticle.publishTime }}</span>
          <a-divider type="vertical" />
          <span>阅读 {{ currentArticle.readCount }}</span>
        </div>
        <div class="article-content" v-html="currentArticle.content"></div>
      </template>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  EyeOutlined, 
  CheckCircleOutlined, 
  StopOutlined, 
  VerticalAlignTopOutlined, 
  VerticalAlignMiddleOutlined, 
  DeleteOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

// 查询参数
const queryParams = reactive({
  status: 'all',
  channel: 'all',
  dateRange: null,
  keyword: ''
})

// 状态变量
const loading = ref(false)
const previewVisible = ref(false)
const currentArticle = ref(null)

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total) => `共 ${total} 条记录`
})

// 表格列定义
const columns = [
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title',
    ellipsis: true,
    width: '30%'
  },
  {
    title: '封面',
    key: 'cover',
    width: '10%'
  },
  {
    title: '频道',
    dataIndex: 'channelName',
    key: 'channel',
    width: '10%'
  },
  {
    title: '状态',
    key: 'status',
    width: '10%'
  },
  {
    title: '发布时间',
    dataIndex: 'publishTime',
    key: 'publishTime',
    sorter: true,
    width: '15%'
  },
  {
    title: '阅读量',
    dataIndex: 'readCount',
    key: 'readCount',
    sorter: true,
    width: '10%'
  },
  {
    title: '操作',
    key: 'action',
    width: '15%'
  }
]

// 频道选项
const channelOptions = [
  { id: 1, name: '前端开发' },
  { id: 2, name: '后端开发' },
  { id: 3, name: '移动开发' },
  { id: 4, name: '人工智能' },
  { id: 5, name: '云计算' },
  { id: 6, name: '大数据' },
  { id: 7, name: '区块链' },
  { id: 8, name: '产品设计' },
  { id: 9, name: '运营' }
]

// 模拟文章数据
const articleList = ref([
  {
    id: 1,
    title: '5G技术将如何改变我们的生活？',
    cover: 'https://via.placeholder.com/300x200',
    channelId: 4,
    channelName: '人工智能',
    status: 'published',
    publishTime: '2023-03-15 14:30',
    readCount: 2456,
    commentCount: 156,
    isTop: true,
    author: '张三',
    content: '<p>这是文章内容，介绍5G技术如何改变我们的生活...</p>'
  },
  {
    id: 2,
    title: '人工智能在医疗领域的应用前景',
    cover: 'https://via.placeholder.com/300x200',
    channelId: 4,
    channelName: '人工智能',
    status: 'published',
    publishTime: '2023-03-14 09:45',
    readCount: 1853,
    commentCount: 124,
    isTop: false,
    author: '李四',
    content: '<p>这是文章内容，介绍人工智能在医疗领域的应用前景...</p>'
  },
  {
    id: 3,
    title: '区块链技术简介：原理与应用',
    cover: 'https://via.placeholder.com/300x200',
    channelId: 7,
    channelName: '区块链',
    status: 'published',
    publishTime: '2023-03-13 16:20',
    readCount: 1356,
    commentCount: 98,
    isTop: false,
    author: '王五',
    content: '<p>这是文章内容，介绍区块链技术原理与应用...</p>'
  },
  {
    id: 4,
    title: '云计算服务对比：AWS vs Azure vs Google Cloud',
    cover: null,
    channelId: 5,
    channelName: '云计算',
    status: 'draft',
    publishTime: null,
    readCount: 0,
    commentCount: 0,
    isTop: false,
    author: '赵六',
    content: '<p>这是文章内容，对比不同云计算服务的优缺点...</p>'
  },
  {
    id: 5,
    title: '网络安全入门：保护个人信息的基本策略',
    cover: 'https://via.placeholder.com/300x200',
    channelId: 2,
    channelName: '后端开发',
    status: 'draft',
    publishTime: null,
    readCount: 0,
    commentCount: 0,
    isTop: false,
    author: '钱七',
    content: '<p>这是文章内容，介绍网络安全入门知识...</p>'
  }
])

// 组件挂载时执行
onMounted(() => {
  loadData()
})

// 加载数据
const loadData = () => {
  loading.value = true
  
  // 这里应该是实际的API调用
  setTimeout(() => {
    // 模拟数据加载和过滤
    let filteredList = [...articleList.value]
    
    // 应用过滤条件
    if (queryParams.status !== 'all') {
      filteredList = filteredList.filter(item => item.status === queryParams.status)
    }
    
    if (queryParams.channel !== 'all') {
      filteredList = filteredList.filter(item => item.channelId === queryParams.channel)
    }
    
    if (queryParams.keyword) {
      filteredList = filteredList.filter(item => 
        item.title.includes(queryParams.keyword) || 
        item.content.includes(queryParams.keyword)
      )
    }
    
    // 模拟分页
    pagination.total = filteredList.length
    loading.value = false
  }, 500)
}

// 状态变化处理
const handleStatusChange = () => {
  pagination.current = 1
  loadData()
}

// 频道变化处理
const handleChannelChange = () => {
  pagination.current = 1
  loadData()
}

// 日期变化处理
const handleDateChange = () => {
  pagination.current = 1
  loadData()
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  loadData()
}

// 表格变化处理
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 编辑文章
const handleEdit = (record) => {
  message.info(`编辑文章：${record.title}`)
  // 这里应该跳转到编辑页面
}

// 预览文章
const handlePreview = (record) => {
  currentArticle.value = record
  previewVisible.value = true
}

// 发布文章
const handlePublish = (record) => {
  message.success(`文章《${record.title}》已发布`)
  // 这里应该调用API更新状态
  record.status = 'published'
  record.publishTime = new Date().toLocaleString()
}

// 下线文章
const handleOffline = (record) => {
  message.success(`文章《${record.title}》已下线`)
  // 这里应该调用API更新状态
  record.status = 'draft'
}

// 置顶文章
const handleTop = (record) => {
  message.success(`文章《${record.title}》已置顶`)
  // 这里应该调用API更新状态
  record.isTop = true
}

// 取消置顶
const handleCancelTop = (record) => {
  message.success(`已取消文章《${record.title}》的置顶`)
  // 这里应该调用API更新状态
  record.isTop = false
}

// 删除文章
const handleDelete = (record) => {
  message.success(`文章《${record.title}》已删除`)
  // 这里应该调用API删除文章
  articleList.value = articleList.value.filter(item => item.id !== record.id)
  loadData()
}
</script>

<style lang="scss" scoped>
.article-container {
  .filter-container {
    margin-bottom: 16px;
  }
  
  .article-title {
    display: flex;
    align-items: center;
    
    .title-text {
      margin-left: 8px;
    }
  }
  
  .article-content {
    margin-top: 20px;
    line-height: 1.8;
  }
}
</style> 