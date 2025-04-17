<template>
  <div class="material-container">
    <a-card :bordered="false">
      <div class="filter">
        <div class="filter-container">
          <a-tabs v-model:activeKey="activeTab" @change="handleTabChange">
            <a-tab-pane key="0" tab="全部"></a-tab-pane>
            <a-tab-pane key="1" tab="收藏"></a-tab-pane>
          </a-tabs>
        </div>
        <div>
          <span class="total">已上传{{ total }}张素材</span>
          <a-button type="primary" @click="showPicDialog = true">
            <template #icon><upload-outlined /></template>
            上传素材
          </a-button>
        </div>
      </div>
      
      <a-spin :spinning="loading">
        <div v-if="list.length" class="content-card">
          <a-card 
            v-for="(img, index) in list" 
            :key="img.id" 
            :bordered="true" 
            hoverable
            class="image-card"
            :class="{ 'selected': isSelect && selectedUrl === img.url }"
            @click="isSelect ? handleSelect(img) : null"
          >
            <template #cover>
              <a-image
                :src="img.url"
                class="image"
                :preview-config="{
                  src: img.url,
                  current: index
                }"
                :preview-group-props="{ 
                  current: index,
                  items: previewImages
                }"
              />
            </template>
            <template #actions>
              <template v-if="isSelect">
                <div class="action-item" @click.stop="handleSelect(img)">
                  <a-button type="text">
                    <template #icon><check-outlined /></template>
                    选择
                  </a-button>
                </div>
              </template>
              <template v-else>
                <div class="action-item" @click.stop="collectOrCancelImg(img)">
                  <a-button type="text">
                    <template #icon><star-outlined v-if="!img.isCollection" /><star-filled v-else style="color: #1890ff" /></template>
                    {{ img.isCollection ? '已收藏' : '收藏' }}
                  </a-button>
                </div>
                <div class="action-item" @click.stop="delImgItem(img)">
                  <a-button type="text">
                    <template #icon><delete-outlined /></template>
                    删除
                  </a-button>
                </div>
              </template>
            </template>
          </a-card>
        </div>
        <a-empty v-else description="暂无数据" />
      </a-spin>

      <div class="footer">
        <div></div>
        <Pagination 
          v-show="total > 0" 
          :total="total" 
          :page="listQuery.page"
          :limit="listQuery.size"
          @pagination="handlePaginationChange" 
        />
      </div>
    </a-card>

    <a-modal
      v-model:open="showPicDialog"
      title="上传素材"
      width="600px"
      :footer="null"
    >
      <Upload :dialogVisible="showPicDialog" @uploadSuccess="uploadSuccess" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, defineProps, defineEmits } from 'vue'
import { message, Image, Modal } from 'ant-design-vue'
import { getAllImgData, collectMaterial, cancelCollectMaterial, deleteMaterial } from '@/api/material'
import myMixin from '@/utils/mixins'
import Upload from '@/components/Upload/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { StarOutlined, StarFilled, DeleteOutlined, CheckOutlined } from '@ant-design/icons-vue'

const mixinMethods = myMixin.methods

const props = defineProps({
  isSelect: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['selectMaterial'])

// 数据
const total = ref(0)
const list = ref([])
const imgChange = ref(false)
const showPicDialog = ref(false)
const activeTab = ref('0')
const loading = ref(false)
const previewImages = ref([])

const listQuery = reactive({
  page: 1,
  size: 10,
  isCollection: null
})

// 选择相关
const selectedUrl = ref('')

// 初始化
onMounted(() => {
  getList()
})

// 监听列表变化，更新预览图片数组
watch(() => list.value, (newList) => {
  previewImages.value = newList.map(item => item.url)
}, { immediate: true })

// 获取素材列表
const getList = async () => {
  loading.value = true
  try {
    const result = await getAllImgData(listQuery)
    if (result.code === 0) {
      list.value = result.data || []
      total.value = result.total || 0
    } else {
      message.error(result.errorMessage || '获取素材列表失败')
    }
  } catch (error) {
    console.error('获取素材列表失败:', error)
    message.error('获取素材列表失败')
  } finally {
    loading.value = false
  }
}

// 收藏或取消收藏
const collectOrCancelImg = async (img) => {
  try {
    const api = img.isCollection ? cancelCollectMaterial : collectMaterial
    const result = await api(img.id.toString())
    if (result.code === 0) {
      img.isCollection = !img.isCollection
      message.success(img.isCollection ? '收藏成功' : '取消收藏成功')
      // 如果在收藏页面取消收藏，需要刷新列表
      if (activeTab.value === '1' && !img.isCollection) {
        getList()
      }
    } else {
      message.error(result.errorMessage || '操作失败')
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 删除图片
const delImgItem = async (img) => {
  try {
    const confirmResult = await new Promise((resolve) => {
      Modal.confirm({
        title: '确认删除',
        content: '确定要删除这张图片吗？',
        okText: '确定',
        cancelText: '取消',
        onOk: () => resolve(true),
        onCancel: () => resolve(false)
      })
    })

    if (!confirmResult) return

    const result = await deleteMaterial(img.id)
    if (result.code === 0) {
      message.success('删除成功')
      getList()  // 刷新列表
    } else {
      message.error(result.errorMessage || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

// 上传成功
const uploadSuccess = () => {
  imgChange.value = true
  showPicDialog.value = false  // 关闭弹窗
  getList()  // 刷新列表
}

// 关闭上传弹窗
const closeModal = () => {
  showPicDialog.value = false
  if (imgChange.value) {
    getList()
    imgChange.value = false
  }
}

// 分页变化
const handlePaginationChange = (val) => {
  listQuery.page = val.page
  listQuery.size = val.limit
  getList()
}

// 切换收藏状态
const handleTabChange = () => {
  list.value = []  // 清空列表，避免闪烁
  listQuery.page = 1
  listQuery.size = 10
  listQuery.isCollection = activeTab.value
  getList()
}

const handleSelect = (img) => {
  emit('selectMaterial', img.url)
}
</script>

<style lang="scss" scoped>
.material-container {
  padding: 0;
  min-height: 100vh;
  
  .filter {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 96px;
    padding: 0 20px;
    
    .filter-container {
      :deep(.ant-tabs-nav) {
        margin-bottom: 0;
      }
      
      :deep(.ant-tabs-tab) {
        padding: 8px 16px;
      }
    }
    
    .total {
      margin-right: 16px;
      color: rgba(0, 0, 0, 0.45);
    }
  }
  
  .content-card {
    display: flex;
    flex-wrap: wrap;
    padding: 0 20px;
    
    .image-card {
      width: calc((100% - 80px) / 5);  // 一行5个，间隔20px
      margin: 20px 20px 0 0;  // 右边距20px
      border-radius: 8px;
      overflow: hidden;
      
      &:nth-child(5n) {  // 每行第5个元素
        margin-right: 0;  // 移除右边距
      }

      &.selected {
        border: 2px solid #1890ff;
      }
      
      :deep(.ant-image) {
        width: 100%;
        height: 186px;
        display: block;
        cursor: pointer;
        
        .ant-image-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }
      
      .action-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        cursor: pointer;
        font-size: 12px;
        
        span {
          margin-top: 4px;
        }
      }
    }
  }

  .footer {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 16px 20px;
  }
}
</style> 