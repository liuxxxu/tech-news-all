<template>
  <div class="page-container">
    <div class="page-title">内容管理</div>
    
    <!-- 搜索表单 -->
    <div class="form-container">
      <a-form layout="inline">
        <a-form-item label="搜索内容">
          <a-input
            v-model:value="newsStore.publishedQuery.title"
            placeholder="请输入内容标题"
            allowClear
            @change="handleTitleChange"
          />
        </a-form-item>
        <a-form-item label="发布状态">
          <a-select
            v-model:value="newsStore.publishedQuery.enable"
            placeholder="请选择状态"
            style="width: 200px"
            allowClear
            @change="handleStatusChange"
          >
            <a-select-option v-for="(item, index) in enableList" :key="index" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="发布日期">
          <a-range-picker
            v-model:value="dateRange"
            format="YYYY-MM-DD"
            :placeholder="['开始日期', '结束日期']"
            @change="handleDateChange"
          />
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="newsStore.publishedList"
        :loading="newsStore.publishedLoading"
        :pagination="false"
        rowKey="id"
      >
        <template #bodyCell="{ column, record, index }">
          <!-- 序号列 -->
          <template v-if="column.dataIndex === 'index'">
            {{ (newsStore.publishedQuery.page - 1) * newsStore.publishedQuery.size + index + 1 }}
          </template>
          
          <!-- 类型列 -->
          <template v-if="column.dataIndex === 'type'">
            {{ getTypeLabel(record.type) }}
          </template>
          
          <!-- 发布时间列 -->
          <template v-if="column.dataIndex === 'publishTime'">
            {{ formatDateTime(record.publishTime) }}
          </template>
          
          <!-- 状态列 -->
          <template v-if="column.dataIndex === 'enable'">
            <a-tag :color="record.enable === 1 ? 'green' : 'default'">
              {{ record.enable === 1 ? '已上架' : '已下架' }}
            </a-tag>
          </template>
          
          <!-- 操作列 -->
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="handleViewDetail(record)">查看</a-button>
            <a-button 
              type="link" 
              :disabled="record.enable === 1"
              @click="handleChangeStatus(record.id, 1)"
            >上架</a-button>
            <a-button 
              type="link" 
              danger
              :disabled="record.enable === 0"
              @click="handleChangeStatus(record.id, 0)"
            >下架</a-button>
          </template>
        </template>
      </a-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container" v-if="newsStore.publishedTotal > 0">
        <a-pagination
          v-model:current="newsStore.publishedQuery.page"
          :total="newsStore.publishedTotal"
          :pageSize="newsStore.publishedQuery.size"
          showSizeChanger
          showQuickJumper
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNewsStore } from '@/stores/news'
import { Modal, DatePicker } from 'ant-design-vue'

const router = useRouter()
const newsStore = useNewsStore()
const dateRange = ref(null)

// 上架状态列表
const enableList = [
  { label: '全部', value: undefined },
  { label: '已上架', value: 1 },
  { label: '已下架', value: 0 }
]

// 类型列表
const typeList = [
  { value: 0, label: '无图文章' },
  { value: 1, label: '单图文章' }
]

// 表格列定义
const columns = [
  { title: '序号', dataIndex: 'index', width: 80, align: 'center' },
  { title: '标题', dataIndex: 'title', align: 'center' },
  { title: '作者', dataIndex: 'authorName', align: 'center' },
  { title: '类型', dataIndex: 'type', align: 'center' },
  { title: '标签', dataIndex: 'labels', align: 'center' },
  { title: '发布时间', dataIndex: 'publishTime', align: 'center' },
  { title: '状态', dataIndex: 'enable', align: 'center' },
  { title: '操作', dataIndex: 'action', width: 240, align: 'center' }
]

// 生命周期钩子
onMounted(() => {
  newsStore.getPublishedList()
})

// 辅助方法
const getTypeLabel = (type) => {
  const item = typeList.find(item => item.value === type)
  return item ? item.label : '未知类型'
}

const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString().replace(/,/g, '<br/>')
}

// 事件处理函数
const handleTitleChange = () => {
  newsStore.resetPublishedPage()
}

const handleStatusChange = () => {
  newsStore.resetPublishedPage()
}

const handleDateChange = (dates, dateStrings) => {
  if (dates) {
    newsStore.setDateRange(dateStrings)
  } else {
    newsStore.setDateRange(null)
  }
}

const handlePageChange = (page) => {
  newsStore.publishedQuery.page = page
  newsStore.getPublishedList()
}

const handleSizeChange = (current, size) => {
  newsStore.publishedQuery.page = 1
  newsStore.publishedQuery.size = size
  newsStore.getPublishedList()
}

const handleViewDetail = (record) => {
  router.push(`/news-published/detail/${record.id}`)
}

const handleChangeStatus = async (id, enable) => {
  const actionText = enable === 1 ? '上架' : '下架'
  
  Modal.confirm({
    title: `确认${actionText}`,
    content: `确定要${actionText}这篇文章吗？`,
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      const success = await newsStore.changeNewsStatus(id, enable)
      if (success) {
        newsStore.getPublishedList()
      }
    }
  })
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style> 