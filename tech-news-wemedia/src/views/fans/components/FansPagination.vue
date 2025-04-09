<template>
  <div class="fans-pagination">
    <div class="header">
      <div class="title">数据列表</div>
      <a-range-picker
        v-model:value="dateRange"
        format="YYYY-MM-DD"
        :placeholder="['开始日期', '结束日期']"
        @change="handleDateChange"
      />
    </div>
    
    <a-table
      :dataSource="list"
      :columns="columns"
      :pagination="false"
      :loading="loading"
      bordered
      :scroll="{ x: 800 }"
    />
    
    <Pagination 
      v-show="total > 0" 
      :total="total" 
      v-model:page="listQuery.page" 
      v-model:limit="listQuery.size" 
      @pagination="getList" 
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import Pagination from '@/components/Pagination/index.vue'

// 查询条件
const dateRange = ref([])
const listQuery = reactive({
  page: 1,
  size: 10,
  startDate: '',
  endDate: ''
})

// 表格基础数据
const list = ref([])
const total = ref(0)
const loading = ref(false)

// 表格列定义
const columns = [
  {
    title: '时间',
    dataIndex: 'dateTime',
    key: 'dateTime',
    width: 150,
    align: 'center'
  },
  {
    title: '粉丝数量',
    dataIndex: 'fansNum',
    key: 'fansNum',
    align: 'center'
  },
  {
    title: '粉丝阅读量',
    dataIndex: 'readNum',
    key: 'readNum',
    align: 'center'
  },
  {
    title: '粉丝收益(元)',
    dataIndex: 'income',
    key: 'income',
    align: 'center'
  },
  {
    title: '取消关注量',
    dataIndex: 'cancelNum',
    key: 'cancelNum',
    align: 'center'
  }
]

// 日期选择回调
const handleDateChange = (dates, dateStrings) => {
  if (dates) {
    listQuery.startDate = dateStrings[0]
    listQuery.endDate = dateStrings[1]
  } else {
    listQuery.startDate = ''
    listQuery.endDate = ''
  }
  
  listQuery.page = 1
  getList()
}

// 获取列表数据
const getList = () => {
  loading.value = true
  
  // 模拟数据，实际项目中应从API获取
  setTimeout(() => {
    list.value = [
      {
        key: '1',
        dateTime: '2023-03-25',
        fansNum: 50,
        readNum: 0,
        income: 0,
        cancelNum: '3,365'
      },
      {
        key: '2',
        dateTime: '2023-03-24',
        fansNum: 150,
        readNum: 2,
        income: 2,
        cancelNum: '3,300'
      },
      {
        key: '3',
        dateTime: '2023-03-23',
        fansNum: 50,
        readNum: 6,
        income: 6,
        cancelNum: '3,223'
      },
      {
        key: '4',
        dateTime: '2023-03-22',
        fansNum: 150,
        readNum: 8,
        income: 8,
        cancelNum: '3,210'
      },
      {
        key: '5',
        dateTime: '2023-03-21',
        fansNum: 125,
        readNum: 9,
        income: 9,
        cancelNum: '3,108'
      },
      {
        key: '6',
        dateTime: '2023-03-20',
        fansNum: 150,
        readNum: 29,
        income: 29,
        cancelNum: '3,034'
      }
    ]
    total.value = 6
    loading.value = false
  }, 600)
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.fans-pagination {
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-top: 20px;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .title {
      font-size: 18px;
      font-weight: 600;
      color: rgba(0, 0, 0, 0.85);
      position: relative;
      padding-left: 16px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 4px;
        height: 18px;
        background-color: #1890ff;
        border-radius: 2px;
      }
    }
  }
}
</style> 