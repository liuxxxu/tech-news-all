<template>
  <div class="page-container">
    <div class="page-title">频道管理</div>
    
    <!-- 搜索表单 -->
    <div class="form-container">
      <a-form layout="inline">
        <a-form-item label="频道名称">
          <a-input
            v-model:value="channelStore.query.name"
            placeholder="请输入频道名称"
            allowClear
            @change="handleNameChange"
          />
        </a-form-item>
        <a-form-item label="账号状态">
          <a-select
            v-model:value="channelStore.query.status"
            placeholder="请选择状态"
            style="width: 200px"
            allowClear
            @change="handleStatusChange"
          >
            <a-select-option v-for="(item, index) in statusList" :key="index" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 操作栏 -->
    <div class="flex-between mb-16">
      <div></div>
      <a-button type="primary" @click="handleCreate">
        <PlusOutlined /> 新建
      </a-button>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="channelStore.list"
        :loading="channelStore.loading"
        :pagination="false"
        rowKey="id"
      >
        <template #bodyCell="{ column, record, index }">
          <!-- 序号列 -->
          <template v-if="column.dataIndex === 'index'">
            {{ (channelStore.query.page - 1) * channelStore.query.size + index + 1 }}
          </template>
          
          <!-- 默认频道 -->
          <template v-if="column.dataIndex === 'isDefault'">
            {{ record.isDefault ? '是' : '否' }}
          </template>
          
          <!-- 状态列 -->
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="record.status ? 'green' : 'red'">
              {{ record.status ? '启用' : '禁用' }}
            </a-tag>
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
              :disabled="record.status"
              @click="handleUpdateStatus(record.id, record.name, true)"
            >启用</a-button>
            <a-button 
              type="link" 
              danger 
              :disabled="!record.status"
              @click="handleUpdateStatus(record.id, record.name, false)"
            >禁用</a-button>
            <a-button 
              type="link" 
              danger
              @click="handleDelete(record.id)"
            >删除</a-button>
          </template>
        </template>
      </a-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container" v-if="channelStore.total > 0">
        <a-pagination
          v-model:current="channelStore.query.page"
          :total="channelStore.total"
          :pageSize="channelStore.query.size"
          showSizeChanger
          showQuickJumper
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
        />
      </div>
    </div>
    
    <!-- 创建频道对话框 -->
    <channel-create-dialog 
      v-model:visible="createDialogVisible" 
      @success="handleSuccess"
    />
    
    <!-- 编辑频道对话框 -->
    <channel-edit-dialog 
      v-model:visible="editDialogVisible"
      :channel="currentChannel"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useChannelStore } from '@/stores/channel'
import { PlusOutlined } from '@ant-design/icons-vue'
import { Modal } from 'ant-design-vue'
import ChannelCreateDialog from './components/channel-create-dialog.vue'
import ChannelEditDialog from './components/channel-edit-dialog.vue'

// 状态列表
const statusList = [
  { label: '全部', value: undefined },
  { label: '启用', value: 1 },
  { label: '禁用', value: 0 }
]

// 表格列定义
const columns = [
  { title: '序号', dataIndex: 'index', width: 80, align: 'center' },
  { title: '频道名称', dataIndex: 'name', align: 'center' },
  { title: '描述', dataIndex: 'description', align: 'center' },
  { title: '是否默认频道', dataIndex: 'isDefault', align: 'center' },
  { title: '状态', dataIndex: 'status', align: 'center' },
  { title: '排序', dataIndex: 'ord', align: 'center' },
  { title: '创建时间', dataIndex: 'createdTime', align: 'center' },
  { title: '操作', dataIndex: 'action', width: 240, align: 'center' }
]

// 使用channelStore
const channelStore = useChannelStore()

// 对话框状态
const createDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentChannel = ref({})

// 生命周期钩子
onMounted(() => {
  channelStore.getList()
})

// 日期格式化
const formatDateTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString()
}

// 事件处理函数
const handleNameChange = () => {
  channelStore.resetPage()
}

const handleStatusChange = () => {
  channelStore.resetPage()
}

const handlePageChange = (page) => {
  channelStore.query.page = page
  channelStore.getList()
}

const handleSizeChange = (current, size) => {
  channelStore.query.page = 1
  channelStore.query.size = size
  channelStore.getList()
}

const handleCreate = () => {
  createDialogVisible.value = true
}

const handleEdit = (record) => {
  currentChannel.value = record
  editDialogVisible.value = true
}

const handleUpdateStatus = async (id, name, status) => {
  await channelStore.updateChannelStatus(id, name, status)
}

const handleDelete = (id) => {
  Modal.confirm({
    title: '确认删除',
    content: '确定要删除该频道吗？删除后无法恢复。',
    okText: '确定',
    okType: 'danger',
    cancelText: '取消',
    onOk: async () => {
      await channelStore.deleteChannel(id)
    }
  })
}

const handleSuccess = () => {
  channelStore.getList()
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style> 