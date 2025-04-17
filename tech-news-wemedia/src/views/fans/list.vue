<template>
  <div class="fans-list">
    <a-card :bordered="false">
      <!-- 搜索和筛选区域 -->
      <div class="search-filter">
        <a-form layout="inline">
          <a-form-item>
            <a-input-search
              v-model:value="searchValue"
              placeholder="搜索粉丝昵称"
              style="width: 250px"
              @search="onSearch"
            />
          </a-form-item>
          
          <a-form-item label="关注时间">
            <a-range-picker 
              v-model:value="dateRange" 
              :disabled-date="disabledDate"
              format="YYYY-MM-DD"
              @change="onDateChange"
            />
          </a-form-item>
          
          <a-form-item label="粉丝级别">
            <a-select
              v-model:value="fanLevel"
              placeholder="请选择级别"
              style="width: 150px"
              @change="onLevelChange"
            >
              <a-select-option value="all">全部级别</a-select-option>
              <a-select-option value="normal">普通粉丝</a-select-option>
              <a-select-option value="active">活跃粉丝</a-select-option>
              <a-select-option value="vip">VIP粉丝</a-select-option>
            </a-select>
          </a-form-item>
          
          <a-form-item>
            <a-button type="primary" @click="onSearch">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
          </a-form-item>
          
          <a-form-item>
            <a-button @click="resetFilters">
              <template #icon><ReloadOutlined /></template>
              重置
            </a-button>
          </a-form-item>
        </a-form>
      </div>
      
      <!-- 操作工具栏 -->
      <div class="toolbar">
        <a-button type="primary" @click="exportData">
          <template #icon><DownloadOutlined /></template>
          导出数据
        </a-button>
        
        <a-button @click="batchTag">
          <template #icon><TagsOutlined /></template>
          批量打标签
        </a-button>
        
        <a-button @click="sendMessage">
          <template #icon><MailOutlined /></template>
          发送消息
        </a-button>
      </div>
      
      <!-- 表格区域 -->
      <a-table
        :columns="columns"
        :data-source="fansList"
        :rowKey="record => record.id"
        :pagination="pagination"
        :loading="loading"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="record.avatar" />
          </template>
          
          <template v-else-if="column.key === 'level'">
            <a-tag :color="getLevelColor(record.level)">
              {{ record.levelName }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="() => viewDetail(record)">
                查看
              </a>
              <a-divider type="vertical" />
              <a @click="() => sendMessageToUser(record)">
                发消息
              </a>
              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link" @click.prevent>
                  更多 <DownOutlined />
                </a>
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="1" @click="() => addTag(record)">
                      <TagOutlined />添加标签
                    </a-menu-item>
                    <a-menu-item key="2" @click="() => blockUser(record)">
                      <StopOutlined />拉黑用户
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  SearchOutlined, 
  ReloadOutlined, 
  DownloadOutlined, 
  TagsOutlined, 
  MailOutlined,
  TagOutlined,
  StopOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

// 搜索和筛选状态
const searchValue = ref('')
const dateRange = ref([])
const fanLevel = ref('all')
const loading = ref(false)

// 表格选择状态
const selectedRowKeys = ref([])

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
    title: '头像',
    key: 'avatar',
    width: 80
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
    sorter: true
  },
  {
    title: '性别',
    dataIndex: 'gender',
    key: 'gender'
  },
  {
    title: '关注时间',
    dataIndex: 'followTime',
    key: 'followTime',
    sorter: true
  },
  {
    title: '粉丝级别',
    key: 'level',
    dataIndex: 'level',
    sorter: true
  },
  {
    title: '互动次数',
    dataIndex: 'interactions',
    key: 'interactions',
    sorter: true
  },
  {
    title: '所在地区',
    dataIndex: 'location',
    key: 'location'
  },
  {
    title: '操作',
    key: 'action',
    width: 180
  }
]

// 模拟粉丝数据
const fansList = ref([
  {
    id: 1,
    nickname: '黑马小王',
    gender: '男',
    avatar: 'https://joeschmoe.io/api/v1/random',
    followTime: '2023-02-15',
    level: 'vip',
    levelName: 'VIP粉丝',
    interactions: 248,
    location: '北京'
  },
  {
    id: 2,
    nickname: '科技达人',
    gender: '男',
    avatar: 'https://joeschmoe.io/api/v1/josh',
    followTime: '2023-03-22',
    level: 'active',
    levelName: '活跃粉丝',
    interactions: 156,
    location: '上海'
  },
  {
    id: 3,
    nickname: '阅读控',
    gender: '女',
    avatar: 'https://joeschmoe.io/api/v1/jana',
    followTime: '2023-01-05',
    level: 'vip',
    levelName: 'VIP粉丝',
    interactions: 321,
    location: '广州'
  },
  {
    id: 4,
    nickname: '数码迷',
    gender: '男',
    avatar: 'https://joeschmoe.io/api/v1/jake',
    followTime: '2023-04-18',
    level: 'normal',
    levelName: '普通粉丝',
    interactions: 89,
    location: '深圳'
  },
  {
    id: 5,
    nickname: '旅行者',
    gender: '女',
    avatar: 'https://joeschmoe.io/api/v1/jess',
    followTime: '2023-05-10',
    level: 'active',
    levelName: '活跃粉丝',
    interactions: 178,
    location: '杭州'
  }
])

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
    pagination.total = 58 // 总条数
    loading.value = false
  }, 500)
}

// 处理表格变化
const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadData()
}

// 获取粉丝级别对应的颜色
const getLevelColor = (level) => {
  const colorMap = {
    normal: 'blue',
    active: 'green',
    vip: 'gold'
  }
  return colorMap[level] || 'blue'
}

// 处理表格行选择
const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

// 处理搜索
const onSearch = () => {
  pagination.current = 1
  loadData()
}

// 日期选择限制
const disabledDate = (current) => {
  return current && current > new Date()
}

// 日期变化处理
const onDateChange = () => {
  pagination.current = 1
  loadData()
}

// 级别变化处理
const onLevelChange = () => {
  pagination.current = 1
  loadData()
}

// 重置筛选
const resetFilters = () => {
  searchValue.value = ''
  dateRange.value = []
  fanLevel.value = 'all'
  pagination.current = 1
  loadData()
}

// 导出数据
const exportData = () => {
  message.success('数据导出成功')
}

// 批量打标签
const batchTag = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择粉丝')
    return
  }
  message.success(`已为${selectedRowKeys.value.length}个粉丝添加标签`)
}

// 发送消息
const sendMessage = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择粉丝')
    return
  }
  message.success(`已向${selectedRowKeys.value.length}个粉丝发送消息`)
}

// 查看粉丝详情
const viewDetail = (record) => {
  message.info(`查看粉丝：${record.nickname}`)
}

// 给单个用户发消息
const sendMessageToUser = (record) => {
  message.success(`已向${record.nickname}发送消息`)
}

// 添加标签
const addTag = (record) => {
  message.success(`已为${record.nickname}添加标签`)
}

// 拉黑用户
const blockUser = (record) => {
  message.success(`已将${record.nickname}加入黑名单`)
}
</script>

<style lang="scss" scoped>
.fans-list {
  .search-filter {
    margin-bottom: 16px;
  }
  
  .toolbar {
    margin-bottom: 16px;
    display: flex;
    gap: 8px;
  }
}
</style> 