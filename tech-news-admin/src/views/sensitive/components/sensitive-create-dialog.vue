<template>
  <a-modal
    title="新增敏感词"
    :visible="visible"
    @cancel="handleCancel"
    :maskClosable="false"
    :keyboard="false"
    @ok="handleOk"
    width="500px"
    cancelText="取消"
    okText="确定"
  >
    <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
    >
      <a-form-item label="敏感词" name="sensitives">
        <a-input 
          v-model:value="formState.sensitives" 
          placeholder="请输入敏感词" 
          :maxlength="10" 
          show-count
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, defineProps, defineEmits } from 'vue'
import { useSensitiveStore } from '@/store/sensitive'

// 定义props和emits
const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 表单ref
const formRef = ref()

// 表单数据
const formState = reactive({
  sensitives: ''
})

// 表单校验规则
const rules = {
  sensitives: [
    { required: true, message: '请输入敏感词', trigger: 'blur' }
  ]
}

// 获取sensitiveStore
const sensitiveStore = useSensitiveStore()

// 重置表单
const resetForm = () => {
  formRef.value.resetFields()
  formState.sensitives = ''
}

// 处理确认
const handleOk = () => {
  formRef.value.validate().then(async () => {
    const success = await sensitiveStore.saveSensitive(formState)
    if (success) {
      resetForm()
      emit('update:visible', false)
      emit('success')
    }
  }).catch(error => {
    console.error('验证失败:', error)
  })
}

// 处理取消
const handleCancel = () => {
  resetForm()
  emit('update:visible', false)
}
</script> 