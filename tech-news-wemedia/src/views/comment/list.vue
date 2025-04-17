<template>
  <div class="comment-list">
    <a-card :bordered="false">
      <!-- 统计卡片区域 -->
      <a-row :gutter="24" class="stat-row">
        <a-col :span="6">
          <div class="stat-card blue">
            <div class="icon-wrapper">
              <CommentOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">256</div>
              <div class="stat-title">总评论数</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card green">
            <div class="icon-wrapper">
              <CheckCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">220</div>
              <div class="stat-title">通过评论数</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card red">
            <div class="icon-wrapper">
              <CloseCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">26</div>
              <div class="stat-title">拒绝评论数</div>
            </div>
          </div>
        </a-col>
        <a-col :span="6">
          <div class="stat-card yellow">
            <div class="icon-wrapper">
              <ExclamationCircleOutlined />
            </div>
            <div class="stat-content">
              <div class="stat-value">10</div>
              <div class="stat-title">待审核评论数</div>
            </div>
          </div>
        </a-col>
      </a-row>

      <!-- 搜索和筛选区域 -->
      <div class="search-filter">
        <a-form layout="inline">
          <a-form-item label="文章标题">
            <a-input
              v-model:value="searchKeyword"
              placeholder="请输入文章标题"
              style="width: 250px"
            />
          </a-form-item>
          
          <a-form-item label="评论时间">
            <a-range-picker 
              v-model:value="dateRange" 
              :disabled-date="disabledDate"
              @change="onDateChange"
              :placeholder="['开始时间', '结束时间']"
            />
          </a-form-item>
          
          <a-form-item>
            <a-space>
              <a-button type="primary" @click="onSearch">
                <template #icon><SearchOutlined /></template>
                查询
              </a-button>
              <a-button @click="resetFilters">
                <template #icon><ReloadOutlined /></template>
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </div>
      
      <!-- 操作工具栏 -->
      <div class="operation-bar">
        <a-space>
          <a-button type="primary" @click="batchApprove" :disabled="!hasSelected">
            <template #icon><CheckOutlined /></template>
            批量通过
          </a-button>
          
          <a-button danger @click="batchReject" :disabled="!hasSelected">
            <template #icon><CloseOutlined /></template>
            批量拒绝
          </a-button>
          
          <a-button @click="exportComments">
            <template #icon><DownloadOutlined /></template>
            导出评论
          </a-button>
        </a-space>
        
        <span v-if="selectedRowKeys.length > 0" class="selected-count">
          已选择 {{ selectedRowKeys.length }} 项
          <a @click="clearSelection" style="margin-left: 8px">清空</a>
        </span>
      </div>
      
      <!-- 评论表格 -->
      <a-table
        :columns="columns"
        :data-source="commentList"
        :rowKey="record => record.id"
        :pagination="pagination"
        :loading="loading"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <!-- 评论内容列 -->
          <template v-if="column.key === 'content'">
            <div class="comment-content">
              <div class="comment-text">{{ record.content }}</div>
              <div class="comment-article">
                <LinkOutlined style="margin-right: 4px" />
                <a>{{ record.article }}</a>
              </div>
            </div>
          </template>
          
          <!-- 评论用户列 -->
          <template v-else-if="column.key === 'user'">
            <div class="user-info">
              <a-avatar :src="record.avatar" :size="32" />
              <span class="username">{{ record.username }}</span>
            </div>
          </template>
          
          <!-- 评论状态列 -->
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <!-- 情感分析列 -->
          <template v-else-if="column.key === 'sentiment'">
            <a-tag :color="getSentimentColor(record.sentiment)">
              {{ getSentimentText(record.sentiment) }}
            </a-tag>
          </template>
          
          <!-- 操作列 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button 
                type="link" 
                size="small" 
                @click="approveComment(record)"
                v-if="record.status === 'pending'"
              >
                通过
              </a-button>
              
              <a-button 
                type="link" 
                size="small" 
                danger 
                @click="rejectComment(record)"
                v-if="record.status === 'pending'"
              >
                拒绝
              </a-button>
              
              <a-button 
                type="link" 
                size="small" 
                @click="replyComment(record)"
              >
                回复
              </a-button>
              
              <a-dropdown>
                <a class="ant-dropdown-link" @click.prevent>
                  更多 <DownOutlined />
                </a>
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="detail" @click="() => viewCommentDetail(record)">
                      <EyeOutlined /> 查看详情
                    </a-menu-item>
                    <a-menu-item key="view-article" @click="() => viewArticle(record)">
                      <FileTextOutlined /> 查看文章
                    </a-menu-item>
                    <a-menu-item key="delete" @click="() => deleteComment(record)">
                      <DeleteOutlined /> 删除评论
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <!-- 回复评论对话框 -->
    <a-modal
      v-model:visible="replyModalVisible"
      title="回复评论"
      @ok="handleReplyOk"
      okText="发送回复"
      cancelText="取消"
    >
      <div v-if="currentComment" class="reply-modal-content">
        <div class="original-comment">
          <p><strong>{{ currentComment.username }}</strong>: {{ currentComment.content }}</p>
          <p class="comment-info">{{ currentComment.time }} · {{ currentComment.article }}</p>
        </div>
        <a-form>
          <a-form-item>
            <a-textarea
              v-model:value="replyContent"
              :rows="4"
              placeholder="请输入回复内容"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  SearchOutlined, 
  ReloadOutlined, 
  CheckOutlined, 
  CloseOutlined, 
  DownloadOutlined,
  LinkOutlined,
  DownOutlined,
  EyeOutlined,
  FileTextOutlined,
  DeleteOutlined,
  CommentOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  StarOutlined
} from '@ant-design/icons-vue'

// 搜索和筛选状态
const searchKeyword = ref('')
const dateRange = ref(null)
const loading = ref(false)

// 表格选择状态
const selectedRowKeys = ref([])
const hasSelected = computed(() => selectedRowKeys.value.length > 0)

// 表格分页配置
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
    title: '评论内容',
    key: 'content',
    dataIndex: 'content',
    ellipsis: true,
    width: '30%'
  },
  {
    title: '评论用户',
    key: 'user',
    dataIndex: 'username',
    width: '15%'
  },
  {
    title: '评论时间',
    dataIndex: 'time',
    key: 'time',
    sorter: true,
    width: '15%'
  },
  {
    title: '状态',
    key: 'status',
    dataIndex: 'status',
    filters: [
      { text: '已审核', value: 'approved' },
      { text: '待审核', value: 'pending' },
      { text: '已拒绝', value: 'rejected' }
    ],
    width: '10%'
  },
  {
    title: '情感分析',
    key: 'sentiment',
    dataIndex: 'sentiment',
    filters: [
      { text: '正面', value: 'positive' },
      { text: '中性', value: 'neutral' },
      { text: '负面', value: 'negative' }
    ],
    width: '10%'
  },
  {
    title: '操作',
    key: 'action',
    width: '20%'
  }
]

// 模拟评论数据
const commentList = ref([
  {
    id: 1,
    content: '这篇文章写得非常好，内容深入浅出，很容易理解。希望能看到更多类似的高质量文章！',
    username: '张三',
    avatar: 'https://joeschmoe.io/api/v1/random',
    time: '2023-03-15 14:30',
    article: '5G技术将如何改变我们的生活？',
    status: 'approved',
    sentiment: 'positive'
  },
  {
    id: 2,
    content: '感谢作者的分享，学到了很多新知识。不过有些观点我不太理解，希望能够进一步说明。',
    username: '李四',
    avatar: 'https://joeschmoe.io/api/v1/jane',
    time: '2023-03-15 13:25',
    article: '人工智能在医疗领域的应用前景',
    status: 'pending',
    sentiment: 'neutral'
  },
  {
    id: 3,
    content: '这篇文章太浅显了，完全没有技术深度，浪费了我的时间。建议作者多做研究再发表文章。',
    username: '王五',
    avatar: 'https://joeschmoe.io/api/v1/john',
    time: '2023-03-15 11:40',
    article: '区块链技术简介：原理与应用',
    status: 'approved',
    sentiment: 'negative'
  },
  {
    id: 4,
    content: '文章内容很好，但有几处错误需要指出：1. AWS不是唯一提供这项服务的平台；2. Azure的定价策略描述不准确。',
    username: '赵六',
    avatar: 'https://joeschmoe.io/api/v1/jack',
    time: '2023-03-15 10:18',
    article: '云计算服务对比：AWS vs Azure vs Google Cloud',
    status: 'approved',
    sentiment: 'neutral'
  },
  {
    id: 5,
    content: '作为一名网络安全从业者，我认为这篇文章对安全风险的认识还是很到位的，值得推荐给入门者阅读。',
    username: '钱七',
    avatar: 'https://joeschmoe.io/api/v1/jill',
    time: '2023-03-15 09:05',
    article: '网络安全入门：保护个人信息的基本策略',
    status: 'pending',
    sentiment: 'positive'
  },
  {
    id: 6,
    content: '作者对技术的理解存在偏差，文中说法与行业最新标准不符，建议更新内容。',
    username: '孙八',
    avatar: 'https://joeschmoe.io/api/v1/jake',
    time: '2023-03-14 16:45',
    article: '5G技术将如何改变我们的生活？',
    status: 'rejected',
    sentiment: 'negative'
  }
])

// 回复评论相关状态
const replyModalVisible = ref(false)
const currentComment = ref(null)
const replyContent = ref('')

// 组件挂载时加载数据
onMounted(() => {
  loadData()
})

// 加载表格数据
const loadData = () => {
  loading.value = true
  
  // 这里应该是实际的API调用
  setTimeout(() => {
    // 模拟服务器返回数据
    pagination.total = 123 // 总条数
    loading.value = false
  }, 500)
}

// 处理表格变化
const handleTableChange = (pag, filters, sorter) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  // 处理排序和筛选
  loadData()
}

// 处理表格行选择
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

// 清空选择
const clearSelection = () => {
  selectedRowKeys.value = []
}

// 获取评论状态对应的文本
const getStatusText = (status) => {
  const statusMap = {
    approved: '已审核',
    pending: '待审核',
    rejected: '已拒绝'
  }
  return statusMap[status] || status
}

// 获取评论状态对应的颜色
const getStatusColor = (status) => {
  const colorMap = {
    approved: 'green',
    pending: 'orange',
    rejected: 'red'
  }
  return colorMap[status] || 'blue'
}

// 获取情感分析对应的文本
const getSentimentText = (sentiment) => {
  const textMap = {
    positive: '正面',
    neutral: '中性',
    negative: '负面'
  }
  return textMap[sentiment] || sentiment
}

// 获取情感分析对应的颜色
const getSentimentColor = (sentiment) => {
  const colorMap = {
    positive: 'green',
    neutral: 'blue',
    negative: 'red'
  }
  return colorMap[sentiment] || 'default'
}

// 日期选择限制
const disabledDate = (current) => {
  return current && current > new Date()
}

// 处理搜索
const onSearch = () => {
  pagination.current = 1
  loadData()
}

// 日期变化处理
const onDateChange = () => {
  pagination.current = 1
  loadData()
}

// 重置筛选
const resetFilters = () => {
  searchKeyword.value = ''
  dateRange.value = null
  pagination.current = 1
  loadData()
}

// 批量通过评论
const batchApprove = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择评论')
    return
  }
  message.success(`已批量通过${selectedRowKeys.value.length}条评论`)
  // 这里应该有实际的API调用
  selectedRowKeys.value = []
  loadData()
}

// 批量拒绝评论
const batchReject = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择评论')
    return
  }
  message.success(`已批量拒绝${selectedRowKeys.value.length}条评论`)
  // 这里应该有实际的API调用
  selectedRowKeys.value = []
  loadData()
}

// 导出评论
const exportComments = () => {
  message.success('评论数据导出成功')
}

// 通过单条评论
const approveComment = (comment) => {
  message.success(`评论"${comment.content.substring(0, 10)}..."已通过审核`)
  // 这里应该有实际的API调用
  loadData()
}

// 拒绝单条评论
const rejectComment = (comment) => {
  message.success(`评论"${comment.content.substring(0, 10)}..."已被拒绝`)
  // 这里应该有实际的API调用
  loadData()
}

// 回复评论
const replyComment = (comment) => {
  replyModalVisible.value = true
  currentComment.value = comment
  replyContent.value = ''
}

// 处理回复提交
const handleReplyOk = () => {
  if (!replyContent.value.trim()) {
    message.warning('请输入回复内容')
    return
  }
  
  message.success(`已回复"${currentComment.value.username}"的评论`)
  replyModalVisible.value = false
  // 这里应该有实际的API调用
}

// 查看评论详情
const viewCommentDetail = (comment) => {
  message.info(`查看评论详情：${comment.id}`)
}

// 查看文章
const viewArticle = (comment) => {
  message.info(`查看文章：${comment.article}`)
}

// 删除评论
const deleteComment = (comment) => {
  message.success(`评论"${comment.content.substring(0, 10)}..."已删除`)
  // 这里应该有实际的API调用
  loadData()
}
</script>

<style lang="scss" scoped>
.comment-list {
  padding: 0;
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
  
  .search-filter {
    margin-bottom: 16px;
  }
  
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

    &.red {
      .icon-wrapper {
        background-color: rgba(245, 34, 45, 0.1);
        color: #f5222d;
      }
      .stat-value {
        color: #f5222d;
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
  }
  
  .operation-bar {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .selected-count {
      color: rgba(0, 0, 0, 0.45);
    }
  }
  
  .comment-content {
    .comment-text {
      margin-bottom: 4px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    
    .comment-article {
      font-size: 12px;
      color: rgba(0, 0, 0, 0.45);
    }
  }
  
  .user-info {
    display: flex;
    align-items: center;
    
    .username {
      margin-left: 8px;
    }
  }
  
  .reply-modal-content {
    .original-comment {
      background-color: #f0f2f5;
      border-radius: 4px;
      padding: 12px;
      margin-bottom: 16px;
      
      .comment-info {
        margin-top: 8px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 12px;
      }
    }
  }
}
</style>