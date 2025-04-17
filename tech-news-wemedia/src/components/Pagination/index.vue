<template>
  <div :class="{'hidden': hidden}" class="pagination-container">
    <a-pagination
      v-model:current="currentPage"
      v-model:pageSize="pageSize"
      :total="total"
      :pageSizeOptions="pageSizes"
      :showSizeChanger="true"
      :showTotal="(total) => `共 ${total} 条`"
      @change="handleCurrentChange"
      @showSizeChange="handleSizeChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { scrollTo } from '@/utils/scroll-to'

const props = defineProps({
  total: {
    required: true,
    type: Number
  },
  page: {
    type: Number,
    default: 1
  },
  limit: {
    type: Number,
    default: 20
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 30, 50]
  },
  autoScroll: {
    type: Boolean,
    default: true
  },
  hidden: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

const currentPage = computed({
  get: () => props.page,
  set: (val) => {
    emit('update:page', val)
  }
})

const pageSize = computed({
  get: () => props.limit,
  set: (val) => {
    emit('update:limit', val)
  }
})

const handleSizeChange = (pageSize) => {
  currentPage.value = 1
  emit('pagination', { page: currentPage.value, limit: pageSize })
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
}

const handleCurrentChange = (page) => {
  emit('pagination', { page, limit: pageSize.value })
  if (props.autoScroll) {
    scrollTo(0, 800)
  }
}
</script>

<style scoped>
.pagination-container {
  padding: 32px 0;
  text-align: center;
}
.pagination-container.hidden {
  display: none;
}
</style> 