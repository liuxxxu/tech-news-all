<template>
  <div class="page-container">
    <div class="page-title">内容审核</div>
    
    <!-- 搜索表单 -->
    <div class="form-container">
      <a-form layout="inline">
        <a-form-item label="搜索内容">
          <a-input
            v-model:value="newsStore.auditQuery.title"
            placeholder="请输入内容标题"
            allowClear
          />
        </a-form-item>
        <a-form-item label="审核状态">
          <a-select
            v-model:value="newsStore.auditQuery.status"
            placeholder="请选择状态"
            style="width: 200px"
            allowClear
          >
            <a-select-option v-for="(item, index) in statusList" :key="index" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="newsStore.auditList"
        :loading="newsStore.auditLoading"
        :pagination="false"
        rowKey="id"
      >
        <template #bodyCell="{ column, record, index }">
          <!-- 序号列 -->
          <template v-if="column.dataIndex === 'index'">
            {{ (newsStore.auditQuery.page - 1) * newsStore.auditQuery.size + index + 1 }}
          </template>
          
          <!-- 类型列 -->
          <!-- <template v-if="column.dataIndex === 'type'">
            {{ getTypeLabel(record.type) }}
          </template> -->
          
          <!-- 发布时间列 -->
          <template v-if="column.dataIndex === 'publishTime'">
            {{ formatDateTime(record.publishTime) }}
          </template>
          
          <!-- 状态列 -->
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusLabel(record.status) }}
            </a-tag>
          </template>

          <!-- 原因列 -->
          <template v-if="column.dataIndex === 'reason'">
            {{ record.reason }}
          </template>
          
          <!-- 操作列 -->
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="handleViewDetail(record)">查看</a-button>
            <a-button 
              type="link" 
              :disabled="record.status !== 3"
              @click="handlePass(record.id)"
            >通过</a-button>
            <a-button 
              type="link" 
              danger
              :disabled="record.status !== 3"
              @click="handleFail(record.id)"
            >驳回</a-button>
          </template>
        </template>
      </a-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <a-pagination
          v-model:current="newsStore.auditQuery.page"
          :total="newsStore.auditTotal"
          :pageSize="newsStore.auditQuery.size"
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
import { ref, onMounted, h } from 'vue'
import { useRouter } from 'vue-router'
import { useNewsStore } from '@/stores/news'
import { Modal, Input, message } from 'ant-design-vue'

const router = useRouter()
const newsStore = useNewsStore()
const reasonText = ref('')

// 状态列表
const statusList = [
  { label: '全部', value: null },
  { label: '提交（待AI审核）', value: 1, color: 'blue' },
  { label: '审核未通过', value: 2, color: 'red' },
  { label: '待人工审核', value: 3, color: 'blue' },
  { label: '人工审核通过', value: 4, color: 'green' },
  { label: '审核通过（待发布）', value: 8, color: 'green' },
  { label: '已发布', value: 9, color: 'green' }
]

// 类型列表
const typeList = [
  { value: 0, label: '无图文章' },
  { value: 1, label: '单图文章' },
]

// 表格列定义
const columns = [
  { title: '序号', dataIndex: 'index', width: 80, align: 'center' },
  { title: '标题', dataIndex: 'title', align: 'center' },
  { title: '作者', dataIndex: 'authorName', align: 'center' },
//   { title: '类型', dataIndex: 'type', align: 'center' },
  { title: '标签', dataIndex: 'labels', align: 'center' },
  { title: '发布时间', dataIndex: 'publishTime', align: 'center' },
  { title: '状态', dataIndex: 'status', align: 'center' },
  { title: '原因', dataIndex: 'reason', align: 'center' },
  { title: '操作', dataIndex: 'action', width: 240, align: 'center' }
]

// 生命周期钩子
onMounted(() => {
  newsStore.getAuditList()
})

// 辅助方法
const getTypeLabel = (type) => {
  const item = typeList.find(item => item.value === type)
  return item ? item.label : '未知类型'
}

const getStatusLabel = (status) => {
  const item = statusList.find(item => item.value === status)
  return item ? item.label : '未知状态'
}

const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString().replace(/,/g, '<br/>')
}

// 事件处理函数
const handleSearch = () => {
  newsStore.resetAuditPage()
}

const handlePageChange = (page) => {
  newsStore.auditQuery.page = page
  newsStore.getAuditList()
}

const handleSizeChange = (current, size) => {
  newsStore.auditQuery.page = 1
  newsStore.auditQuery.size = size
  newsStore.getAuditList()
}

const handleViewDetail = (record) => {
  router.push(`/news/detail/${record.id}`)
}

const handlePass = async (id) => {
  const success = await newsStore.passNews({ id, status: 4 })
  if (success) {
    newsStore.getAuditList()
  }
}

const handleFail = (id) => {
  reasonText.value = ''
  
  Modal.confirm({
    title: '驳回原因',
    content: h('div', [
      h(Input.TextArea, {
        placeholder: '请输入驳回原因',
        rows: 4,
        onChange: (e) => {
          reasonText.value = e.target.value
        }
      })
    ]),
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      if (!reasonText.value.trim()) {
        message.warning('请输入驳回原因')
        return Promise.reject()
      }
      
      const success = await newsStore.failNews({ 
        id, 
        status: 2, 
        msg: reasonText.value 
      })
      
      if (success) {
        newsStore.getAuditList()
      }
    }
  })
}

// 获取状态颜色
const getStatusColor = (status) => {
  const item = statusList.find(item => item.value === status)
  return item ? item.color : 'default'
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style> 