<template>
  <div class="publish-container">
    <a-card class="form-card" :bordered="false">
      <a-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        layout="vertical"
      >
        <!-- 标题 -->
        <a-form-item name="title" required>
          <a-input
            v-model:value="formData.title"
            placeholder="请输入标题"
            class="title-input"
            :maxlength="30"
            show-count
          />
        </a-form-item>

        <!-- 封面 -->
        <a-form-item label="封面" name="type">
          <a-radio-group v-model:value="formData.type" @change="handleCoverTypeChange">
            <a-radio value="-1">无封面</a-radio>
            <a-radio value="1">设置封面</a-radio>
          </a-radio-group>

          <!-- 封面图片上传 -->
          <div v-if="formData.type === '1'" class="cover-container">
            <div class="cover-image-item">
              <div v-if="singleCover" class="image-preview">
                <img :src="singleCover" class="preview-img" />
              </div>
              <div v-else class="upload-placeholder">
                <picture-outlined />
                <div>封面预览</div>
              </div>
            </div>
            <div class="cover-actions">
              <a-button type="primary" @click="openImageSelector('cover')">
                <template #icon><picture-outlined /></template>
                从素材库选择
              </a-button>
              <a-button @click="showUploadDialog = true">
                <template #icon><upload-outlined /></template>
                上传图片
              </a-button>
            </div>
          </div>
        </a-form-item>

        <!-- 内容 - 替换为wangEditor -->
        <a-form-item name="content" required label="正文内容">
          <div class="editor-container">
            <Toolbar
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              :mode="mode"
              class="editor-toolbar"
            />
            <Editor
              v-model="editorContent"
              :defaultConfig="editorConfig"
              :mode="mode"
              @onChange="handleEditorChange"
              @onCreated="handleEditorCreated"
              class="editor-content"
            />
          </div>
        </a-form-item>

        <div class="form-options">
          <a-row :gutter="24">
            <!-- 频道 -->
            <a-col :span="8">
              <a-form-item label="频道" name="channelId" required>
                <a-select
                  v-model:value="formData.channelId"
                  placeholder="请选择频道"
                  style="width: 100%"
                  :options="channelList.map(item => ({
                    value: item.id,
                    label: item.name
                  }))"
                >
                </a-select>
              </a-form-item>
            </a-col>

            <!-- 标签 -->
            <a-col :span="8">
              <a-form-item label="标签" name="labels">
                <a-input
                  v-model:value="formData.labels"
                  placeholder="请输入标签"
                  :maxlength="20"
                  show-count
                />
              </a-form-item>
            </a-col>

            <!-- 发布时间 -->
            <a-col :span="8">
              <a-form-item label="定时发布" name="publishTime">
                <a-date-picker
                  v-model:value="formData.publishTime"
                  show-time
                  format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择发布时间"
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </div>

        <div class="action-buttons">
          <a-space>
            <a-button size="large" @click="goBack">返回</a-button>
            <a-button size="large" type="primary" ghost @click="saveAsDraft">存为草稿</a-button>
            <a-button 
              size="large" 
              type="primary" 
              :disabled="!isFormValid" 
              @click="submitArticle"
            >发布文章</a-button>
          </a-space>
        </div>
      </a-form>
    </a-card>

    <!-- 素材选择弹窗 -->
    <a-modal
      v-model:open="showMaterialDialog"
      title="素材库"
      width="1200px"
      :footer="null"
      :destroyOnClose="true"
    >
      <Material @selectMaterial="handleMaterialSelect" :isSelect="true" />
    </a-modal>

    <!-- 上传图片弹窗 -->
    <a-modal
      v-model:open="showUploadDialog"
      title="上传图片"
      width="600px"
      :footer="null"
      @cancel="handleUploadCancel"
    >
      <Upload :dialogVisible="showUploadDialog" @uploadSuccess="handleUploadSuccess" />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, onBeforeUnmount, shallowRef, nextTick, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { getChannels, getArticleById, submitArticle as submitArticleApi } from '@/api/content'
import { getAllImgData } from '@/api/material'
import { uploadImg } from '@/api/material'
import Upload from '@/components/Upload/index.vue'
import Material from '@/views/material/index.vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { Boot } from '@wangeditor/editor'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)

// 获取当前登录用户信息
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

// 是否是编辑模式
const isEdit = ref(false)
const articleId = ref(null)

// wangEditor相关
const editorRef = shallowRef()
const editorContent = ref('')
const mode = ref('default')

// 编辑器配置
const toolbarConfig = {
  excludeKeys: ['uploadImage', 'insertImage'], // 排除默认的图片上传和插入按钮
}

// 编辑器配置
const editorConfig = {
  placeholder: '请输入正文内容...',
  MENU_CONF: {}
}

// 表单数据
const formData = reactive({
  id: null,
  title: '',
  channelId: undefined,
  labels: '',
  type: '-1',
  publishTime: null,  // 默认为 null，不从后端获取
  images: '',
  content: '',
  statusOld: undefined
})

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, message: '标题不能为空', trigger: 'blur' }
  ],
  channelId: [
    { required: true, message: '请选择频道', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'change' }
  ]
}

// 频道数据
const channelList = ref([])

// 封面图片数据
const singleCover = ref('')

// 素材选择相关
const showMaterialDialog = ref(false)
const showUploadDialog = ref(false)
const materialTab = ref('0')
const materialList = ref([])
const materialTotal = ref(0)
const materialQuery = reactive({
  page: 1,
  size: 20,
  isCollection: '0'
})
const selectedMaterial = ref('')
const currentSelector = ref({
  type: 'single',  // 默认为封面上传模式
  index: -1
})

// 监听素材选择Tab变化
watch(materialTab, (newVal) => {
  getMaterialList(newVal)
})

// 确保Boot引用正确
console.log('Boot对象:', Boot)

// 组件销毁前，销毁编辑器实例
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

// 添加自定义工具栏按钮
const addCustomToolbarButtons = () => {
  if (!editorRef.value) return
  
  // 获取工具栏DOM元素
  const toolbar = document.querySelector('.w-e-toolbar')
  if (!toolbar) return
  
  // 查找上传图片按钮位置
  const uploadGroup = toolbar.querySelector('.w-e-bar-item-group:nth-child(7)')
  if (!uploadGroup) return
  
  // 创建素材库按钮
  const materialBtn = document.createElement('div')
  materialBtn.className = 'w-e-bar-item w-e-bar-item-image'
  materialBtn.innerHTML = `
    <button class="w-e-bar-item-btn">
      <svg viewBox="0 0 1024 1024" class="w-e-icon">
        <path d="M842.688 99.072H181.248c-45.568 0-82.432 36.864-82.432 82.432v660.928c0 45.568 36.864 82.432 82.432 82.432h661.504c45.568 0 82.432-36.864 82.432-82.432V181.504c0-45.568-36.864-82.432-82.496-82.432z m-4.096 700.416H185.344c-14.336 0-26.112-11.776-26.112-26.112V212.992c0-14.336 11.776-26.112 26.112-26.112h653.312c14.336 0 26.112 11.776 26.112 26.112v560.384c-0.064 14.336-11.84 26.112-26.176 26.112z" fill="#515151"></path>
        <path d="M330.24 330.24m-82.944 0a82.944 82.944 0 1 0 165.888 0 82.944 82.944 0 1 0-165.888 0Z" fill="#FF9248"></path>
        <path d="M342.528 462.848h-24.576c-93.184 0-169.472 76.288-169.472 169.472v83.968c0 6.144 4.608 10.752 10.752 10.752h342.016c6.144 0 10.752-4.608 10.752-10.752v-83.968c0-93.184-76.288-169.472-169.472-169.472zM730.112 363.008h-150.016c-6.144 0-10.752 4.608-10.752 10.752s4.608 10.752 10.752 10.752h150.016c6.144 0 10.752-4.608 10.752-10.752s-4.608-10.752-10.752-10.752zM730.112 462.848h-251.392c-6.144 0-10.752 4.608-10.752 10.752s4.608 10.752 10.752 10.752h251.392c6.144 0 10.752-4.608 10.752-10.752s-4.608-10.752-10.752-10.752zM730.112 562.688H521.728c-6.144 0-10.752 4.608-10.752 10.752s4.608 10.752 10.752 10.752h208.384c6.144 0 10.752-4.608 10.752-10.752s-4.608-10.752-10.752-10.752z" fill="#FF9248"></path>
      </svg>
      <span class="w-e-bar-item-text">素材库</span>
    </button>
  `
  
  // 添加点击事件
  materialBtn.addEventListener('click', () => {
    // 打开素材库弹窗
    currentSelector.value = { type: 'editor', index: -1 }
    selectedMaterial.value = ''
    showMaterialDialog.value = true
    getMaterialList()
  })
  
  // 创建上传按钮
  const uploadBtn = document.createElement('div')
  uploadBtn.className = 'w-e-bar-item w-e-bar-item-image'
  uploadBtn.innerHTML = `
    <button class="w-e-bar-item-btn">
      <svg viewBox="0 0 1024 1024" class="w-e-icon">
        <path d="M480 352v480c0 17.664 14.336 32 32 32s32-14.336 32-32V352l128 128c12.512 12.512 32.768 12.512 45.248 0s12.512-32.768 0-45.248l-183.04-183.04c-3.008-3.008-6.656-5.376-10.72-6.944-7.872-3.008-16.608-3.008-24.512 0-4.032 1.568-7.68 3.904-10.72 6.944l-183.04 183.04c-12.512 12.512-12.512 32.768 0 45.248 12.512 12.512 32.768 12.512 45.248 0l129.536-128zM864 800c0 17.664 14.336 32 32 32s32-14.336 32-32V254.016c0-70.592-57.408-128-128-128H224c-70.592 0-128 57.408-128 128V800c0 17.664 14.336 32 32 32s32-14.336 32-32V254.016c0-35.296 28.704-64 64-64h576c35.296 0 64 28.704 64 64V800z" fill="#595959"></path>
      </svg>
      <span class="w-e-bar-item-text">上传图片</span>
    </button>
  `
  
  // 添加点击事件
  uploadBtn.addEventListener('click', () => {
    currentSelector.value = { type: 'editor', index: -1 }
    showUploadDialog.value = true
  })
  
  // 插入到工具栏
  uploadGroup.appendChild(materialBtn)
  uploadGroup.appendChild(uploadBtn)
}

// 处理编辑器创建完成
const handleEditorCreated = (editor) => {
  editorRef.value = editor
  
  // 在编辑器创建完成后添加自定义按钮
  
  // 使用nextTick确保DOM已更新
  setTimeout(() => {
    addCustomToolbarButtons()
  }, 200)
}

// 编辑器内容变化
const handleEditorChange = (editor) => {
  formData.content = editor.getHtml()
}

// 获取频道列表
const getChannelsList = async () => {
  try {
    const res = await getChannels()
    if (res.code === 0) {
      channelList.value = res.data || []
      console.log('频道列表:', channelList.value) // 添加日志
    } else {
      message.error(res.errorMessage || '获取频道列表失败')
    }
  } catch (error) {
    console.error('获取频道列表失败:', error)
    message.error('获取频道列表失败')
  }
}

// 获取文章详情
const getArticleDetail = async (id) => {
  try {
    const res = await getArticleById(id)
    if (res.code === 0 && res.data) {
      // 基本信息
      formData.id = res.data.id
      formData.title = res.data.title
      formData.channelId = res.data.channelId
      formData.labels = res.data.labels
      formData.type = res.data.type === 0 ? '-1' : '1'
      formData.statusOld = res.data.status
      
      // 内容
      if (res.data.content) {
        editorContent.value = res.data.content
        formData.content = res.data.content
      }
      
      // 封面图片
      if (res.data.cover) {
        singleCover.value = res.data.cover
      }
    } else {
      message.error(res.errorMessage || '获取文章详情失败')
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    message.error('获取文章详情失败')
  }
}

// 获取素材列表
const getMaterialList = async (tabKey = materialTab.value) => {
  try {
    const res = await getAllImgData({
      isCollection: tabKey
    })
    
    if (res.code === 0) {
      materialList.value = res.data || []
      materialTotal.value = res.total || 0
      console.log('素材列表:', materialList.value)
    } else {
      message.error(res.errorMessage || '获取素材列表失败')
    }
  } catch (error) {
    console.error('获取素材列表失败:', error)
    message.error('获取素材列表失败')
  }
}

// 处理素材选择
const handleMaterialSelect = (url) => {
  if (currentSelector.value.type === 'cover') {
    singleCover.value = url
    formData.type = '1'
  } else if (currentSelector.value.type === 'editor') {
    // 插入到编辑器
    if (editorRef.value) {
      editorRef.value.insertNode({
        type: 'paragraph',
        children: [{ text: '' }]
      })
      editorRef.value.insertNode({
        type: 'image',
        src: url,
        children: [{ text: '' }]
      })
      editorRef.value.insertNode({
        type: 'paragraph',
        children: [{ text: '' }]
      })
    }
  }
  showMaterialDialog.value = false
}

// 封面类型变化
const handleCoverTypeChange = () => {
  if (formData.type === '-1') {
    singleCover.value = ''
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 保存为草稿
const saveAsDraft = () => {
  saveArticle(true)
}

// 发布文章
const submitArticle = () => {
  saveArticle(false)
}

// 保存文章
const saveArticle = async (isDraft) => {
  try {
    await formRef.value.validate()
    
    // 验证内容
    if (!formData.content || formData.content === '<p><br></p>') {
      return message.error('请输入文章内容')
    }
    
    // 验证封面图片
    if (formData.type === '1' && !singleCover.value) {
      return message.error('请选择封面图片')
    }
    
    // 准备要提交的数据
    const submitData = {
      id: formData.id,
      title: formData.title,
      channelId: formData.channelId,
      labels: formData.labels || '',
      publishTime: formData.publishTime ? formData.publishTime.format('YYYY-MM-DD HH:mm:ss') : dayjs().format('YYYY-MM-DD HH:mm:ss'),
      content: formData.content,
      type: parseInt(formData.type),
      cover: formData.type === '1' ? singleCover.value : '',
      status: isDraft ? 0 : 1,
      statusOld: formData.statusOld
    }

    // 如果是无封面，将 type 从 -1 改为 0
    if (submitData.type === -1) {
      submitData.type = 0
    }
    
    // 提交
    const res = await submitArticleApi(submitData)
    
    if (res.code === 0) {
      message.success(isDraft ? '保存草稿成功' : '发布文章成功')
      router.push('/content/index')
    } else {
      message.error(res.errorMessage || (isDraft ? '保存草稿失败' : '发布文章失败'))
    }
  } catch (error) {
    console.error('保存失败:', error)
  }
}

// 处理上传成功后的逻辑
const handleUploadSuccess = (url) => {
  console.log('上传成功，图片URL:', url)  // 调试日志
  showUploadDialog.value = false
  
  if (currentSelector.value.type === 'cover') {
    formData.type = '1'  // 设置为有封面模式
    nextTick(() => {
      singleCover.value = url
      console.log('设置封面图片:', singleCover.value)  // 调试日志
    })
  } else if (currentSelector.value.type === 'editor') {
    // 将图片插入编辑器
    if (editorRef.value) {
      editorRef.value.insertNode({
        type: 'paragraph',
        children: [{ text: '' }]
      })
      editorRef.value.insertNode({
        type: 'image',
        src: url,
        children: [{ text: '' }]
      })
      editorRef.value.insertNode({
        type: 'paragraph',
        children: [{ text: '' }]
      })
    }
  }
}

// 处理上传弹窗关闭
const handleUploadCancel = () => {
  showUploadDialog.value = false
}

// 打开素材选择器
const openImageSelector = (type, index = 0) => {
  currentSelector.value = { type, index }
  showMaterialDialog.value = true
  getMaterialList()
}

// 表单验证状态
const isFormValid = computed(() => {
  return formData.title && 
         formData.channelId && 
         formData.content && 
         formData.content !== '<p><br></p>' && 
         (formData.type !== '1' || singleCover.value) // 如果选择了封面模式，必须有封面图片
})

// 监听表单数据变化
watch([() => formData.title, () => formData.channelId, () => formData.content, () => formData.type, singleCover], () => {
  // 当表单数据变化时，isFormValid 会自动重新计算
  console.log('表单验证状态:', isFormValid.value)
  console.log('当前选择的频道ID:', formData.channelId) // 添加日志
})

// 初始化
onMounted(async () => {
  await getChannelsList()
  
  // 获取路由参数中的文章ID
  const id = route.params.id
  if (id) {
    isEdit.value = true
    articleId.value = id
    await getArticleDetail(id)
  }
})
</script>

<style lang="scss" scoped>
.publish-container {
  padding: 0;
  min-height: 100vh;
  width: 100%;
  
  .form-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    margin: 0;
    width: 100%;
    
    :deep(.ant-card-body) {
      padding: 24px 32px;
      width: 100%;
      box-sizing: border-box;
    }
  }
  
  .title-input {
    font-size: 20px;
    padding: 16px;
    border-radius: 6px;
    margin-bottom: 8px;
    
    &:focus, &:hover {
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }
  
  .editor-container {
    border: 1px solid #d9d9d9;
    border-radius: 8px;
    overflow: hidden;
    
    .editor-toolbar {
      border-bottom: 1px solid #f0f0f0;
      
      :deep(.w-e-toolbar) {
        padding: 0 10px;
        
        .w-e-bar-item {
          margin: 0 3px;
          
          button {
            color: #595959;
            
            &:hover {
              background-color: #f4f4f5;
              color: #1890ff;
            }
          }
          
          &.w-e-active {
            button {
              background-color: #e6f7ff;
              color: #1890ff;
            }
          }
        }
        
        .w-e-bar-divider {
          margin: 0 6px;
        }
      }
    }
    
    .editor-content {
      min-height: 400px;
      max-height: 600px;
      overflow-y: auto;
      
      :deep(.w-e-text-container) {
        background-color: #fff;
        
        .w-e-text-placeholder {
          font-style: italic;
          color: #bfbfbf;
        }
      }
    }
  }
  
  .form-options {
    margin-top: 28px;
    padding: 16px 24px;
    border-radius: 8px;
  }
  
  .cover-container {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    margin-top: 12px;
    
    .cover-image-item {
      width: 240px;
      height: 160px;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      
      .image-preview {
        width: 100%;
        height: 100%;
        position: relative;
        background-color: #f5f5f5;
        
        .preview-img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          display: block;
        }
      }
      
      .upload-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background-color: #f5f5f5;
        border: 1px dashed #d9d9d9;
        color: #8c8c8c;

        :deep(.anticon) {
          font-size: 32px;
          margin-bottom: 8px;
        }
      }
    }
    
    .cover-actions {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-top: 0;
      
      :deep(.ant-btn) {
        width: 160px;
        height: 40px;
        border-radius: 6px;
      }
    }
  }
  
  .action-buttons {
    margin-top: 32px;
    text-align: center;
    
    :deep(.ant-btn) {
      min-width: 120px;
      border-radius: 6px;
      padding: 0 24px;
    }
  }
  
  .material-container {
    padding: 20px;
    
    .material-list {
      margin-top: 20px;
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      max-height: 400px;
      overflow-y: auto;
      padding: 4px;
      
      .material-item {
        width: 140px;
        height: 140px;
        border-radius: 8px;
        overflow: hidden;
        cursor: pointer;
        border: 2px solid transparent;
        transition: all 0.3s;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        position: relative;
        
        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
        }
        
        &.selected {
          border-color: #1890ff;
          box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.3);
          
          &::after {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(24, 144, 255, 0.1);
            pointer-events: none;
          }
        }
        
        .material-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          display: block;
        }
      }
    }
    
    .material-actions {
      margin-top: 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 0;
      border-top: 1px solid #f0f0f0;
    }
  }
  
  .material-modal {
    :deep(.ant-modal-content) {
      border-radius: 8px;
      overflow: hidden;
    }
    
    :deep(.ant-modal-header) {
      background-color: #f5f5f5;
      padding: 16px 24px;
    }
    
    :deep(.ant-modal-title) {
      font-weight: 600;
    }
    
    :deep(.ant-modal-body) {
      padding: 0;
    }
  }
}
</style> 