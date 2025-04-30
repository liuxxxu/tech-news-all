<template>
  <div class="message-container">
    <!-- 私信列表 -->
    <a-card :bordered="false">
      <div class="message-header">
        <a-button type="primary" @click="handleSendMessage">
          <template #icon><message-outlined /></template>
          发送新私信
        </a-button>
      </div>

      <a-table
        :columns="columns"
        :data-source="messageList"
        :rowKey="record => record.id"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <!-- 发送人列 -->
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'sender'">
            <div class="sender-info">
              <a-avatar :src="record.avatar" size="small" />
              <span class="sender-name">{{ record.sender }}</span>
            </div>
          </template>
          
          <!-- 消息内容列 -->
          <template v-else-if="column.key === 'content'">
            <div class="message-content" :title="record.content">
              {{ record.content }}
            </div>
          </template>
          
          <!-- 状态列 -->
          <template v-else-if="column.key === 'status'">
            <a-tag :color="record.isRead ? 'green' : 'blue'">
              {{ record.isRead ? '已读' : '未读' }}
            </a-tag>
          </template>
          
          <!-- 操作列 -->
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="() => handleReadMessage(record)">查看</a>
              <a-divider type="vertical" />
              <a @click="() => handleReplyMessage(record)">回复</a>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定要删除这条私信吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="() => handleDeleteMessage(record)"
              >
                <a>删除</a>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 查看/回复私信弹窗 -->
    <a-modal
      v-model:visible="messageVisible"
      :title="messageAction === 'read' ? '查看私信' : '回复私信'"
      width="600px"
      @ok="handleModalOk"
    >
      <template v-if="currentMessage">
        <div class="message-detail">
          <div class="message-info">
            <a-avatar :src="currentMessage.avatar" size="large" />
            <div class="user-info">
              <div class="user-name">{{ currentMessage.sender }}</div>
              <div class="message-time">{{ currentMessage.time }}</div>
            </div>
          </div>
          
          <div class="message-body">
            {{ currentMessage.content }}
          </div>

          <div class="message-history" v-if="currentMessage.history && currentMessage.history.length > 0">
            <div class="history-title">历史消息</div>
            <div class="history-item" v-for="(item, index) in currentMessage.history" :key="index">
              <div class="history-info">
                <span class="history-sender">{{ item.isMe ? '我' : currentMessage.sender }}</span>
                <span class="history-time">{{ item.time }}</span>
              </div>
              <div class="history-content" :class="{ 'my-message': item.isMe }">
                {{ item.content }}
              </div>
            </div>
          </div>
          
          <a-textarea
            v-if="messageAction === 'reply'"
            v-model:value="replyContent"
            placeholder="请输入回复内容"
            :auto-size="{ minRows: 3, maxRows: 6 }"
            :maxLength="200"
            show-count
          />
        </div>
      </template>
    </a-modal>

    <!-- 发送新私信弹窗 -->
    <a-modal
      v-model:visible="newMessageVisible"
      title="发送新私信"
      width="600px"
      @ok="handleSendNewMessage"
    >
      <div class="new-message-form">
        <a-form :model="newMessageForm" layout="vertical">
          <a-form-item label="收件人" name="receiver">
            <a-select
              v-model:value="newMessageForm.receiver"
              placeholder="请选择收件人"
              mode="multiple"
              :options="receiverOptions"
            ></a-select>
          </a-form-item>
          
          <a-form-item label="私信内容" name="content">
            <a-textarea
              v-model:value="newMessageForm.content"
              placeholder="请输入私信内容"
              :auto-size="{ minRows: 3, maxRows: 6 }"
              :maxLength="200"
              show-count
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { MessageOutlined } from '@ant-design/icons-vue'

// 状态变量
const loading = ref(false)
const messageVisible = ref(false)
const newMessageVisible = ref(false)
const messageAction = ref('read') // 'read' or 'reply'
const currentMessage = ref(null)
const replyContent = ref('')

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
    title: '发送人',
    key: 'sender',
    width: '20%'
  },
  {
    title: '内容',
    key: 'content',
    ellipsis: true,
    width: '40%'
  },
  {
    title: '时间',
    dataIndex: 'time',
    key: 'time',
    sorter: true,
    width: '15%'
  },
  {
    title: '状态',
    key: 'status',
    width: '10%'
  },
  {
    title: '操作',
    key: 'action',
    width: '15%'
  }
]

// 模拟私信数据
const messageList = ref([
  {
    id: 1,
    sender: '张三',
    avatar: 'https://joeschmoe.io/api/v1/male/1',
    content: '您好，我想了解一下如何加入您的社区？',
    time: '2024-04-15 14:30',
    isRead: false,
    history: [
      {
        time: '2024-04-14 10:25',
        content: '您好，我是张三，想了解一下您的平台。',
        isMe: false
      },
      {
        time: '2024-04-14 11:30',
        content: '您好，欢迎了解我们的平台，有什么可以帮助您的吗？',
        isMe: true
      }
    ]
  },
  {
    id: 2,
    sender: '李四',
    avatar: 'https://joeschmoe.io/api/v1/male/2',
    content: '我想反馈一个网站的问题，页面加载很慢',
    time: '2024-04-14 09:45',
    isRead: true,
    history: []
  },
  {
    id: 3,
    sender: '王五',
    avatar: 'https://joeschmoe.io/api/v1/male/3',
    content: '您发布的《区块链技术简介》文章很有帮助，感谢分享！',
    time: '2024-04-13 16:20',
    isRead: true,
    history: [
      {
        time: '2024-04-12 14:25',
        content: '您好，您的文章写得很好，想请教几个问题。',
        isMe: false
      },
      {
        time: '2024-04-12 15:30',
        content: '谢谢您的支持，请问您有什么问题呢？',
        isMe: true
      },
      {
        time: '2024-04-12 15:45',
        content: '想了解一下区块链在金融领域的应用前景。',
        isMe: false
      }
    ]
  },
  {
    id: 4,
    sender: '赵六',
    avatar: 'https://joeschmoe.io/api/v1/male/4',
    content: '想邀请您参加我们的技术沙龙，时间是下周三晚上7点',
    time: '2024-04-12 11:20',
    isRead: false,
    history: []
  },
  {
    id: 5,
    sender: '钱七',
    avatar: 'https://joeschmoe.io/api/v1/female/1',
    content: '您好，我是行业媒体记者，想约您做个采访',
    time: '2024-04-11 09:15',
    isRead: true,
    history: []
  }
])

// 新消息表单
const newMessageForm = reactive({
  receiver: [],
  content: ''
})

// 收件人选项
const receiverOptions = [
  { value: '1', label: '张三' },
  { value: '2', label: '李四' },
  { value: '3', label: '王五' },
  { value: '4', label: '赵六' },
  { value: '5', label: '钱七' }
]

// 组件挂载时执行
onMounted(() => {
  loadMessageData()
})

// 加载私信数据
const loadMessageData = () => {
  loading.value = true
  
  // 这里应该是实际的API调用
  setTimeout(() => {
    // 模拟数据加载
    pagination.total = messageList.value.length
    loading.value = false
  }, 500)
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
  
  loadMessageData()
}

// 查看私信
const handleReadMessage = (record) => {
  messageAction.value = 'read'
  currentMessage.value = record
  messageVisible.value = true
  
  // 标记为已读
  if (!record.isRead) {
    record.isRead = true
  }
}

// 回复私信
const handleReplyMessage = (record) => {
  messageAction.value = 'reply'
  currentMessage.value = record
  replyContent.value = ''
  messageVisible.value = true
  
  // 标记为已读
  if (!record.isRead) {
    record.isRead = true
  }
}

// 删除私信
const handleDeleteMessage = (record) => {
  // 这里应该是实际的API调用
  messageList.value = messageList.value.filter(item => item.id !== record.id)
  message.success('删除私信成功')
}

// 弹窗确认处理
const handleModalOk = () => {
  if (messageAction.value === 'reply') {
    if (!replyContent.value.trim()) {
      message.warning('请输入回复内容')
      return
    }
    
    // 这里应该是实际的API调用
    // 模拟添加回复到历史记录
    if (!currentMessage.value.history) {
      currentMessage.value.history = []
    }
    
    currentMessage.value.history.push({
      time: new Date().toLocaleString(),
      content: replyContent.value,
      isMe: true
    })
    
    message.success('回复发送成功')
  }
  
  messageVisible.value = false
}

// 打开发送新私信弹窗
const handleSendMessage = () => {
  newMessageForm.receiver = []
  newMessageForm.content = ''
  newMessageVisible.value = true
}

// 发送新私信
const handleSendNewMessage = () => {
  if (newMessageForm.receiver.length === 0) {
    message.warning('请选择收件人')
    return
  }
  
  if (!newMessageForm.content.trim()) {
    message.warning('请输入私信内容')
    return
  }
  
  // 这里应该是实际的API调用
  message.success('私信发送成功')
  newMessageVisible.value = false
}
</script>

<style lang="scss" scoped>
.message-container {
  padding: 0 16px;
  
  .message-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .sender-info {
    display: flex;
    align-items: center;
    
    .sender-name {
      margin-left: 8px;
    }
  }
  
  .message-content {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .message-detail {
    .message-info {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
      
      .user-info {
        margin-left: 12px;
        
        .user-name {
          font-weight: 600;
          font-size: 16px;
        }
        
        .message-time {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }
    
    .message-body {
      background-color: #f5f5f5;
      padding: 16px;
      border-radius: 4px;
      margin-bottom: 20px;
    }
    
    .message-history {
      margin-top: 24px;
      margin-bottom: 24px;
      
      .history-title {
        font-weight: 600;
        margin-bottom: 12px;
        color: #8c8c8c;
      }
      
      .history-item {
        margin-bottom: 12px;
        
        .history-info {
          display: flex;
          justify-content: space-between;
          margin-bottom: 4px;
          
          .history-sender {
            font-weight: 500;
          }
          
          .history-time {
            font-size: 12px;
            color: #8c8c8c;
          }
        }
        
        .history-content {
          background-color: #f5f5f5;
          padding: 8px 12px;
          border-radius: 4px;
          display: inline-block;
          max-width: 80%;
          
          &.my-message {
            background-color: #e6f7ff;
            float: right;
          }
        }
      }
      
      .history-item::after {
        content: '';
        display: table;
        clear: both;
      }
    }
  }
}
</style> 