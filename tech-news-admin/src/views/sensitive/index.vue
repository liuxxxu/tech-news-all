<template>
  <div class="page-container">
    <div class="page-title">敏感词管理</div>
    
    <!-- 搜索表单 -->
    <div class="form-container">
      <a-form layout="inline">
        <a-form-item label="敏感词">
          <a-input
            v-model:value="sensitiveStore.query.name"
            placeholder="请输入敏感词"
            allowClear
            @change="handleNameChange"
          />
        </a-form-item>
      </a-form>
      
      <div class="flex-between mb-16">
        <div></div>
        <a-button type="primary" @click="handleCreate">
          <template #icon><PlusOutlined /></template>
          新建
        </a-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="sensitiveStore.list"
        :loading="sensitiveStore.loading"
        :pagination="false"
        rowKey="id"
      >
        <template #bodyCell="{ column, record, index }">
          <!-- 序号列 -->
          <template v-if="column.dataIndex === 'index'">
            {{ (sensitiveStore.query.page - 1) * sensitiveStore.query.size + index + 1 }}
          </template>
          
          <!-- 创建时间列 -->
          <template v-if="column.dataIndex === 'createdTime'">
            {{ formatDateTime(record.createdTime) }}
          </template>
          
          <!-- 操作列 -->
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-button 
              type="link" 
              danger
              @click="handleDelete(record.id)"
            >删除</a-button>
          </template>
        </template>
      </a-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container" v-if="sensitiveStore.total > 0">
        <a-pagination
          v-model:current="sensitiveStore.query.page"
          :total="sensitiveStore.total"
          :pageSize="sensitiveStore.query.size"
          showSizeChanger
          showQuickJumper
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>
    
    <!-- 创建敏感词对话框 -->
    <sensitive-create-dialog 
      v-model:visible="createDialogVisible" 
      @success="handleSuccess"
    />
    
    <!-- 编辑敏感词对话框 -->
    <sensitive-edit-dialog 
      v-model:visible="editDialogVisible"
      :sensitive="currentSensitive"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useSensitiveStore } from '@/store/sensitive'
import { PlusOutlined } from '@ant-design/icons-vue'
import { Modal } from 'ant-design-vue'
import SensitiveCreateDialog from './components/sensitive-create-dialog.vue'
import SensitiveEditDialog from './components/sensitive-edit-dialog.vue'

// 使用状态存储
const sensitiveStore = useSensitiveStore()

// 对话框状态
const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentSensitive = ref({})

// 表格列定义
const columns = [
  { title: '序号', dataIndex: 'index', width: 80, align: 'center' },
  { title: '敏感词', dataIndex: 'sensitives', align: 'center' },
  { title: '创建时间', dataIndex: 'createdTime', align: 'center' },
  { title: '操作', dataIndex: 'action', width: 200, align: 'center' }
]

// 生命周期钩子
onMounted(() => {
  sensitiveStore.getList()
})

// 日期格式化
const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString().replace(/,/g, '<br/>')
}

// 事件处理函数
const handleNameChange = () => {
  sensitiveStore.resetPage()
}

const handlePageChange = (page) => {
  sensitiveStore.query.page = page
  sensitiveStore.getList()
}

const handleSizeChange = (current, size) => {
  sensitiveStore.query.page = 1
  sensitiveStore.query.size = size
  sensitiveStore.getList()
}

const handleCreate = () => {
  createDialogVisible.value = true
}

const handleEdit = (record) => {
  currentSensitive.value = record
  editDialogVisible.value = true
}

const handleDelete = (id) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除该敏感词吗？删除后无法恢复。',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    onOk: () => {
      return sensitiveStore.deleteSensitive(id)
    }
  })
}

const handleSuccess = () => {
  sensitiveStore.getList()
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style> 