<template>
  <div class="content-container">
    <a-card :bordered="false">
            <div class="filter-container">
      <a-form layout="inline" :model="listQuery">
        <a-form-item label="状态">
          <a-select
            v-model:value="listQuery.status"
            style="width: 120px"
            @change="handleStatusChange"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="0">草稿</a-select-option>
            <a-select-option value="1">待审核</a-select-option>
            <a-select-option value="2">审核未通过</a-select-option>
            <a-select-option value="3">待人工审核</a-select-option>
            <a-select-option value="8">审核通过</a-select-option>
            <a-select-option value="9">已发布</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="频道">
          <a-select
            v-model:value="listQuery.channelId"
            style="width: 120px"
            @change="handleChannelChange"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option 
              v-for="item in channelList" 
              :key="item.id" 
              :value="item.id"
            >
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="日期">
          <a-range-picker 
            v-model:value="dateRange"
            format="YYYY-MM-DD" 
            @change="onDateChange"
            :placeholder="['开始时间', '结束时间']"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="getList">查询</a-button>
        </a-form-item>
      </a-form>
      <div class="action-buttons">
        <a-button type="primary" @click="handlePublish">
          <template #icon><plus-outlined /></template>
          发布文章
        </a-button>
      </div>
    </div>

    <a-table
      v-if="list.length > 0"
      :dataSource="list"
      :columns="columns"
      :pagination="false"
      :rowKey="record => record.id"
      class="article-table"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'title'">
          <div class="article-title">{{ record.title }}</div>
        </template>
        
        <template v-if="column.key === 'status'">
          <div>
            <a-tooltip v-if="record.status === 2 && record.reason" :title="record.reason">
              <a-tag :color="(statusMap[record.status] || { color: 'default' }).color">
                {{ (statusMap[record.status] || { text: '未知' }).text }}
              </a-tag>
            </a-tooltip>
            <a-tag v-else :color="(statusMap[record.status] || { color: 'default' }).color">
              {{ (statusMap[record.status] || { text: '未知' }).text }}
            </a-tag>
            <a-tag v-if="record.status === 9" :color="record.enable === 0 ? 'default' : 'green'">
              {{ record.enable === 1 ? '已上架' : '已下架' }}
            </a-tag>
          </div>
        </template>
        
        <template v-if="column.key === 'actions'">
          <div class="action-buttons">
            <a-button type="link" size="small" @click="editArticle(record.id)">
              <template #icon><edit-outlined /></template>
              编辑
            </a-button>
            <a-button v-if="record.status === 9" type="link" size="small" @click="toggleArticleStatus(record)">
              <template #icon>
                <down-outlined v-if="record.enable === 1" />
                <up-outlined v-else />
              </template>
              {{ record.enable === 1 ? '下架' : '上架' }}
            </a-button>
            <a-button type="link" size="small" danger @click="deleteArticle(record.id)">
              <template #icon><delete-outlined /></template>
              删除
            </a-button>
          </div>
        </template>
      </template>
    </a-table>
    <a-empty v-else description="暂无数据" />

    <a-pagination
      v-show="total > 0"
      :current="listQuery.page"
      :pageSize="listQuery.size"
      :total="total"
      show-size-changer
      @change="handlePageChange"
      @showSizeChange="handleSizeChange"
      class="pagination"
    />
    </a-card>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { searchArticle, deleteArticles, upDownArticle, getChannels } from '@/api/content'
import dayjs from 'dayjs'

const router = useRouter()

// 表格列定义
const columns = [
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title',
    width: '40%'
  },
  {
    title: '状态',
    key: 'status',
    width: '20%'
  },
  {
    title: '发布时间',
    dataIndex: 'publishTime',
    key: 'publishTime',
    width: '20%',
    customRender: ({ text }) => text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'
  },
  {
    title: '操作',
    key: 'actions',
    width: '20%'
  }
]

// 状态映射
const statusMap = {
  0: { text: '草稿', color: 'orange' },
  1: { text: '待审核', color: 'blue' },
  2: { text: '审核未通过', color: 'red' },
  3: { text: '待人工审核', color: 'blue' },
  8: { text: '审核通过', color: 'green' },
  9: { text: '已发布', color: 'green' }
}

// 数据
const list = ref([])
const total = ref(0)
const channelList = ref([])
const dateRange = ref([])

const listQuery = reactive({
  page: 1,
  size: 10,
  status: '',
  channelId: '',
  beginPubDate: '',
  endPubDate: '',
  keyword: ''
})

// 初始化
onMounted(() => {
  getList()
  fetchChannels()
})

// 获取频道列表
const fetchChannels = async () => {
  try {
    const res = await getChannels()
    channelList.value = res.data || []
  } catch (error) {
    message.error('获取频道列表失败')
  }
}

// 获取文章列表
const getList = async () => {
  try {
    const result = await searchArticle({
      page: listQuery.page,
      size: listQuery.size,
      status: listQuery.status || null,
      channelId: listQuery.channelId || null,
      beginPubDate: listQuery.beginPubDate,
      endPubDate: listQuery.endPubDate,
      keyword: listQuery.keyword || ''
    })
    if (result.code === 0) {
      list.value = result.data
      total.value = result.total
    } else {
      message.error(result.errorMessage || '获取文章列表失败')
    }
  } catch (error) {
    message.error('获取文章列表失败')
  }
}

// 日期变更
const onDateChange = (dates, dateStrings) => {
  if (dates) {
    listQuery.beginPubDate = dateStrings[0] + ' 00:00:00'
    listQuery.endPubDate = dateStrings[1] + ' 23:59:59'
  } else {
    listQuery.beginPubDate = ''
    listQuery.endPubDate = ''
  }
}

// 分页变化
const handlePageChange = (page) => {
  listQuery.page = page
  getList()
}

// 每页条数变化
const handleSizeChange = (current, size) => {
  listQuery.size = size
  getList()
}

// 状态变化
const handleStatusChange = () => {
  listQuery.page = 1
  getList()
}

// 频道变化
const handleChannelChange = () => {
  listQuery.page = 1
  getList()
}

// 跳转到发布页
const handlePublish = () => {
  router.push('/content/publish')
}

// 编辑文章
const editArticle = (id) => {
  router.push(`/content/publish/${id}`)
}

// 删除文章
const deleteArticle = (id) => {
  Modal.confirm({
    title: '确定要删除这篇文章吗？',
    content: '删除后不可恢复',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await deleteArticles(id)
        if (res.code === 0) {
          message.success('删除成功')
          getList()
        } else {
          message.error(res.errorMessage || '删除失败')
        }
      } catch (error) {
      }
    }
  })
}

// 上下架文章
const toggleArticleStatus = async (record) => {
  const enable = record.enable === 1 ? 0 : 1
  const action = enable === 1 ? '上架' : '下架'
  
  try {
    const res = await upDownArticle({
      id: record.id,
      enable
    })
    
    if (res.code === 0) {
      message.success(`${action}成功`)
      getList()
    } else {
      message.error(res.errorMessage || `${action}失败`)
    }
  } catch (error) {
    message.error(`${action}失败`)
  }
}
</script>

<style lang="scss" scoped>
.content-container {
  padding: 0;
  min-height: 100vh;
  width: 100%;
  

  .filter-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    padding: 16px;
    // background-color: #fff;
    border-radius: 4px;
    
    .action-buttons {
      display: flex;
      gap: 8px;
    }
  }

  .article-table {
    margin-bottom: 16px;
    // background-color: #fff;
    border-radius: 4px;
    
    .article-title {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 500px;
    }
    
    .action-buttons {
      display: flex;
      gap: 8px;
    }
  }

  .pagination {
    margin-top: 16px;
    text-align: right;
  }
}
</style> 