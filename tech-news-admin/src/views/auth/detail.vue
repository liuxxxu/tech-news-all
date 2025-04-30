<template>
  <div class="page-container">
    <div class="detail-header">
      <a-page-header
        :title="'返回'"
        @back="goBack"
        style="padding: 0"
      >
        <template #subTitle>实名认证</template>
      </a-page-header>
      
      <div v-if="authInfo.status === 1" class="operation-buttons">
        <a-button type="primary" @click="handlePass">
          <template #icon><CheckOutlined /></template>
          通过
        </a-button>
        <a-button type="primary" danger class="ml-10" @click="handleFail">
          <template #icon><CloseOutlined /></template>
          驳回
        </a-button>
      </div>
      
      <div v-else-if="authInfo.status === 9" class="status-info">
        <span class="status-label">状态：</span>
        <a-tag color="success">审核通过</a-tag>
      </div>
      
      <div v-else-if="authInfo.status === 2" class="status-info">
        <span class="status-label">状态：</span>
        <a-tag color="error">已驳回</a-tag>
        <div class="reason">理由：{{ authInfo.reason }}</div>
      </div>
    </div>
    
    <div class="content">
      <a-row :gutter="30" class="mb-20">
        <a-col :span="4">
          <span class="label">用户ID</span>
          <span class="value">{{ authInfo.userId || '-' }}</span>
        </a-col>
        <a-col :span="4">
          <span class="label">姓名</span>
          <span class="value">{{ authInfo.name }}</span>
        </a-col>
        <a-col :span="8">
          <span class="label">身份证号</span>
          <span class="value">{{ authInfo.idno }}</span>
        </a-col>
        <a-col :span="8">
          <span class="label">申请时间</span>
          <span class="value">{{ formatTime(authInfo.createdTime) }}</span>
        </a-col>
      </a-row>
      
      <a-row :gutter="30">
        <a-col :span="11">
          <detail-image :img-src="authInfo.frontImage" />
          <div class="image-label">
            <span class="triangle">▲</span>身份证正面
          </div>
        </a-col>
        <a-col :span="11">
          <detail-image :img-src="authInfo.backImage" />
          <div class="image-label">
            <span class="triangle">▲</span>身份证背面
          </div>
        </a-col>
      </a-row>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, h } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Modal, Input, message } from 'ant-design-vue'
import { CheckOutlined, CloseOutlined } from '@ant-design/icons-vue'
import { authPass, authFail } from '@/api/auth'
import DetailImage from './components/detail-image.vue'

// 获取router和route
const router = useRouter()
const route = useRoute()

// 认证信息
const authInfo = reactive({
  id: '',
  userId: '',
  name: '',
  idno: '',
  frontImage: '',
  backImage: '',
  holdImage: '',
  liveImage: '',
  status: 0,
  reason: '',
  createdTime: ''
})

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期钩子
onMounted(() => {
  const record = route.query.record
  if (!record) {
    message.error('认证信息不能为空')
    router.back()
    return
  }
  
  try {
    const data = JSON.parse(record)
    Object.keys(authInfo).forEach(key => {
      if (key in data) {
        authInfo[key] = data[key]
      }
    })
  } catch (error) {
    console.error('解析认证信息失败:', error)
    message.error('解析认证信息失败')
    router.back()
  }
})

// 返回上一页
const goBack = () => {
  router.back()
}

// 通过认证
const handlePass = () => {
  Modal.confirm({
    title: '确认',
    content: '确定要通过该用户的实名认证吗？',
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      try {
        const res = await authPass({ 
          id: authInfo.id,
          status: authInfo.status
        })
        if (res.code === 0) {
          message.success('审核通过成功')
          authInfo.status = 9
        }
      } catch (error) {
        console.error('审核通过失败:', error)
      }
    }
  })
}

// 驳回认证
const handleFail = () => {
  Modal.confirm({
    title: '驳回原因',
    content: () => h('div', [
      h(Input.TextArea, {
        placeholder: '请输入驳回原因',
        rows: 4,
        onChange: (e) => {
          reasonText = e.target.value
        }
      })
    ]),
    okText: '确定',
    cancelText: '取消',
    async onOk() {
      if (!reasonText.trim()) {
        message.warning('请输入驳回原因')
        return Promise.reject()
      }
      
      try {
        const res = await authFail({ 
          id: authInfo.id,
          status: authInfo.status,
          reason: reasonText
        })
        if (res.code === 0) {
          message.success('驳回成功')
          authInfo.status = 2
          authInfo.reason = reasonText
        }
      } catch (error) {
        console.error('驳回失败:', error)
      }
    }
  })
}

let reasonText = ''
</script>

<style scoped>
.page-container {
  background-color: #fff;
  border-radius: 4px;
  padding: 24px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
}

.status-info {
  text-align: right;
}

.status-label {
  margin-right: 8px;
  color: rgba(0, 0, 0, 0.45);
}

.reason {
  margin-top: 8px;
  color: #ff4d4f;
}

.content {
  margin-top: 24px;
}

.label {
  margin-right: 12px;
  color: rgba(0, 0, 0, 0.45);
}

.value {
  font-weight: 500;
}

.mb-20 {
  margin-bottom: 20px;
}

.ml-10 {
  margin-left: 10px;
}

.image-label {
  margin-top: 8px;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
}

.triangle {
  margin-right: 4px;
  color: #1890ff;
}
</style> 