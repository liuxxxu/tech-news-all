<template>
  <a-modal
    title="编辑敏感词"
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
import { ref, reactive, defineProps, defineEmits, watch } from 'vue'
import { useSensitiveStore } from '@/store/sensitive'

// 定义props和emits
const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  sensitive: {
    type: Object,
    required: true,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 表单ref
const formRef = ref()

// 表单数据
const formState = reactive({
  id: 0,
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

// 监听sensitive变化
watch(() => props.sensitive, (newSensitive) => {
  if (newSensitive && props.visible) {
    formState.id = newSensitive.id
    formState.sensitives = newSensitive.sensitives
  }
}, { deep: true, immediate: true })

// 监听visible变化
watch(() => props.visible, (newVisible) => {
  if (newVisible && props.sensitive) {
    formState.id = props.sensitive.id
    formState.sensitives = props.sensitive.sensitives
  }
})

// 处理确认
const handleOk = () => {
  formRef.value.validate().then(async () => {
    const success = await sensitiveStore.updateSensitive(formState)
    if (success) {
      emit('update:visible', false)
      emit('success')
    }
  }).catch(error => {
    console.error('验证失败:', error)
  })
}

// 处理取消
const handleCancel = () => {
  emit('update:visible', false)
}
</script> 