<template>
  <div class="time-filter">
    <a-card :bordered="false">
      <a-form layout="inline">
        <a-form-item label="时间范围">
          <a-radio-group v-model:value="timeRange" button-style="solid" @change="handleRangeChange">
            <a-radio-button value="today">今日</a-radio-button>
            <a-radio-button value="yesterday">昨日</a-radio-button>
            <a-radio-button value="week">近7天</a-radio-button>
            <a-radio-button value="month">近30天</a-radio-button>
            <a-radio-button value="custom">自定义</a-radio-button>
          </a-radio-group>
        </a-form-item>
        
        <a-form-item v-if="timeRange === 'custom'">
          <a-range-picker
            v-model:value="dateRange"
            format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            :allowClear="false"
            @change="handleDateChange"
          />
        </a-form-item>
        
        <a-form-item>
          <a-button type="primary" @click="applyFilter">
            <template #icon><SearchOutlined /></template>
            筛选
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'

// 时间范围选择
const timeRange = ref('week')
// 自定义日期范围
const dateRange = ref([])

// 不可选择的日期（今天之后的日期）
const disabledDate = (current) => {
  return current && current > dayjs().endOf('day')
}

// 时间范围变化处理
const handleRangeChange = (e) => {
  if (e.target.value !== 'custom') {
    // 非自定义时，根据选择设置日期范围
    const today = dayjs()
    
    switch (e.target.value) {
      case 'today':
        dateRange.value = [today, today]
        break
      case 'yesterday':
        const yesterday = today.subtract(1, 'day')
        dateRange.value = [yesterday, yesterday]
        break
      case 'week':
        dateRange.value = [today.subtract(6, 'day'), today]
        break
      case 'month':
        dateRange.value = [today.subtract(29, 'day'), today]
        break
    }
  }
}

// 日期选择变化
const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    dateRange.value = dates
  }
}

// 应用筛选
const applyFilter = () => {
  let startDate = ''
  let endDate = ''
  
  if (dateRange.value && dateRange.value.length === 2) {
    startDate = dateRange.value[0].format('YYYY-MM-DD')
    endDate = dateRange.value[1].format('YYYY-MM-DD')
  }
  
  // 触发筛选事件
  emit('filter', {
    timeRange: timeRange.value,
    startDate,
    endDate
  })
}

// 初始化日期范围
watch(() => timeRange.value, (newValue) => {
  handleRangeChange({ target: { value: newValue } })
}, { immediate: true })

// 定义事件
const emit = defineEmits(['filter'])
</script>

<style lang="scss" scoped>
.time-filter {
  margin-bottom: 24px;
  
  :deep(.ant-card-body) {
    padding: 16px;
  }
}
</style> 