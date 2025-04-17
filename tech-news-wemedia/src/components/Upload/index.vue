<template>
  <div class="uploader">
    <a-upload
      ref="uploadRef"
      :file-list="fileList"
      :show-upload-list="false"
      :before-upload="beforeUpload"
      :multiple="false"
      :maxCount="1"
      accept=".jpg,.png,.jpeg"
      @change="handleChange"
      @remove="handleRemove"
      class="upload-box"
    >
      <div class="upload-area" v-if="!uploadImgUrl">
        <plus-outlined />
        <span>选择图片</span>
      </div>
      <img v-else :src="uploadImgUrl" class="avatar">
    </a-upload>
    <div class="upload-tip">支持扩展名：jpg、jpeg、png，文件不得大于5MB</div>
    <a-button 
      type="primary" 
      class="upload-button" 
      :disabled="!fileList.length" 
      @click="fnUpload"
    >
      上传
    </a-button>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { uploadImg } from '@/api/material'
import { PlusOutlined } from '@ant-design/icons-vue'

const props = defineProps({
  dialogVisible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['uploadSuccess'])

const uploadRef = ref(null)
const fileList = ref([])
const uploadImgUrl = ref('')

watch(() => props.dialogVisible, (newVal) => {
  if (!newVal) {
    clearFiles()
  }
})

// 检查图片格式和大小
const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isJPG) {
    message.error('上传图片只能是jpg/jpeg/png格式')
    return false
  }
  if (!isLt5M) {
    message.error('上传图片大小不能超过5MB')
    return false
  }

  // 预览选择的文件
  const reader = new FileReader()
  reader.onload = (e) => {
    uploadImgUrl.value = e.target.result
  }
  reader.readAsDataURL(file)
  
  // 阻止自动上传
  return false
}

// 文件列表变化
const handleChange = ({ fileList: newFileList }) => {
  fileList.value = [...newFileList].slice(-1)
  if (newFileList.length === 0) {
    uploadImgUrl.value = ''
  }
}

// 移除文件
const handleRemove = () => {
  clearFiles()
}

// 上传图片
const fnUpload = async () => {
  if (!fileList.value.length) {
    message.error('请选择图片')
    return
  }

  const file = fileList.value[0].originFileObj
  const formData = new FormData()
  formData.append('prefix', 'wemedia')
  formData.append('fileName', file.name)
  formData.append('file', file)

  try {
    const result = await uploadImg(formData)
    if (result.code === 0) {
      message.success('上传成功')
      uploadImgUrl.value = result.data
      emit('uploadSuccess', result.data)
    } else {
      message.error(result.errorMessage || '上传失败')
    }
  } catch (error) {
  }
}

// 清除文件
const clearFiles = () => {
  fileList.value = []
  uploadImgUrl.value = ''
}
</script>

<style lang="scss" scoped>
.uploader {
  text-align: center;
  width: 300px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;

  .upload-box {
    width: 160px;
    height: 160px;
    margin-bottom: 16px;

    :deep(.ant-upload) {
      width: 160px !important;
      height: 160px !important;
      background-color: #fafafa;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      transition: border-color 0.3s;
      display: flex !important;
      justify-content: center;
      align-items: center;
      overflow: hidden;
      padding: 0 !important;

      &:hover {
        border-color: #1890ff;
      }
    }

    :deep(.ant-upload-select) {
      width: 160px !important;
      height: 160px !important;
      display: block !important;
    }
  }

  .upload-area {
    width: 160px;
    height: 160px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    gap: 12px;

    .anticon {
      font-size: 32px;
    }

    span {
      font-size: 14px;
    }
  }

  .avatar {
    width: 160px;
    height: 160px;
    object-fit: cover;
    display: block;
  }

  .upload-tip {
    color: #999;
    font-size: 12px;
    margin: 8px 0;
    text-align: center;
    width: 100%;
  }

  .upload-button {
    margin-top: 8px;
    width: 100%;
  }
}
</style> 