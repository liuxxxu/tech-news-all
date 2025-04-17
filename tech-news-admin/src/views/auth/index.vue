<template>
  <div class="page-container">
    <div class="page-title">实名认证</div>
    
    <!-- 搜索表单 -->
    <div class="search-card">
      <a-form layout="inline">
        <a-form-item label="审核状态">
          <a-radio-group v-model:value="query.status" @change="handleStatusChange">
            <a-radio v-for="item in statusList" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 数据表格 -->
    <div class="table-card">
      <a-table
        :columns="columns"
        :data-source="list"
        :loading="loading"
        :pagination="false"
        rowKey="id"
      >
        <template #bodyCell="{ column, record, index }">
          <!-- 序号列 -->
          <template v-if="column.dataIndex === 'index'">
            {{ (query.page - 1) * query.size + index + 1 }}
          </template>
          
          <!-- 认证类型列 -->
          <template v-if="column.dataIndex === 'type'">
            实名认证
          </template>
          
          <!-- 状态列 -->
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <!-- 操作列 -->
          <template v-if="column.dataIndex === 'action'">
            <a-button type="link" @click="handleView(record)">查看</a-button>
            <a-button 
              type="link" 
              :disabled="record.status !== 1"
              @click="handlePass(record)"
            >通过</a-button>
            <a-button 
              type="link" 
              danger
              :disabled="record.status !== 1"
              @click="handleFail(record)"
            >驳回</a-button>
          </template>
        </template>
      </a-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <a-pagination
          v-model:current="query.page"
          :total="total"
          :pageSize="query.size"
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
import { onMounted, ref, h } from 'vue'
import { useRouter } from 'vue-router'
import { Modal, Input, message } from 'ant-design-vue'
import { findAuthList, authPass, authFail } from '@/api/auth'

// 获取router
const router = useRouter()

// 状态列表
const statusList = [
  { value: 0, label: '全部' },
  { value: 1, label: '待审核', color: 'blue' },
  { value: 2, label: '未通过', color: 'red' },
  { value: 9, label: '已通过', color: 'green' }
]

// 表格列定义
const columns = [
  { title: '序号', dataIndex: 'index', width: 80, align: 'center' },
  { 
    title: '用户ID', 
    dataIndex: 'userId', 
    align: 'center',
    customRender: ({ text }) => text || '-'
  },
  { title: '姓名', dataIndex: 'name', align: 'center' },
  { title: '身份证号', dataIndex: 'idno', align: 'center' },
  { 
    title: '申请时间', 
    dataIndex: 'createdTime', 
    align: 'center',
    customRender: ({ text }) => {
      if (!text) return '-'
      return new Date(text).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  },
  { 
    title: '状态', 
    dataIndex: 'status', 
    align: 'center',
    customRender: ({ text }) => getStatusText(text)
  },
  { 
    title: '驳回原因', 
    dataIndex: 'reason', 
    align: 'center',
    customRender: ({ text }) => text || '-'
  },
  { 
    title: '操作', 
    dataIndex: 'action', 
    width: 250, 
    align: 'center'
  }
]

// 数据状态
const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({
  page: 1,
  size: 10,
  status: 0
})

// 获取状态文本
const getStatusText = (status) => {
  const item = statusList.find(item => item.value === status)
  return item ? item.label : '未知状态'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const item = statusList.find(item => item.value === status)
  return item ? item.color : 'default'
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await findAuthList(query.value)
    if (res.code === 0) {
      list.value = res.data || []
      total.value = res.data?.length || 0
    }
  } catch (error) {
    console.error('获取认证列表失败:', error)
    message.error('获取认证列表失败')
  } finally {
    loading.value = false
  }
}

// 生命周期钩子
onMounted(() => {
  getList()
})

// 事件处理函数
const handleStatusChange = () => {
  query.value.page = 1
  getList()
}

const handlePageChange = (page) => {
  query.value.page = page
  getList()
}

const handleSizeChange = (current, size) => {
  query.value.page = 1
  query.value.size = size
  getList()
}

const handleView = (record) => {
  router.push({
    path: '/auth/detail',
    query: {
      id: record.id
    }
  })
}

const handlePass = (record) => {
  Modal.confirm({
    title: '确认',
    content: '确定要通过该用户的实名认证吗？',
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await authPass({ 
          id: record.id,
          status: record.status
        })
        if (res.code === 0) {
          message.success('审核通过成功')
          getList()
        }
      } catch (error) {
        console.error('审核通过失败:', error)
      }
    }
  })
}

// 驳回理由
const reasonText = ref('')

const handleFail = (record) => {
  reasonText.value = ''
  
  Modal.confirm({
    title: '驳回原因',
    content: () => h('div', [
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
      try {
        const res = await authFail({ 
          id: record.id,
          status: record.status,
          reason: reasonText.value
        })
        if (res.code === 0) {
          message.success('驳回成功')
          getList()
        }
      } catch (error) {
        console.error('驳回失败:', error)
        message.error('驳回失败')
      }
    }
  })
}
</script>

<style scoped>
.page-container {
  min-height: 100%;
  padding: 16px;
  background-color: #f0f2f5;
}


.search-card {
  padding: 24px;
  background: #fff;
  border-radius: 2px;
  margin-bottom: 16px;
}

.table-card {
  padding: 24px;
  background: #fff;
  border-radius: 2px;
}

.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style> 