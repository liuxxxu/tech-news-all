<template>
  <a-modal
    title="编辑频道"
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
      <a-form-item label="频道名称" name="name">
        <a-input 
          v-model:value="formState.name" 
          placeholder="请输入频道名称" 
          :maxlength="10" 
          show-count
        />
      </a-form-item>
      <a-form-item label="频道描述" name="description">
        <a-input 
          v-model:value="formState.description" 
          placeholder="请输入频道描述" 
          :maxlength="16" 
          show-count
        />
      </a-form-item>
      <a-form-item label="是否启用" name="status">
        <a-radio-group v-model:value="formState.status">
          <a-radio :value="1">启用</a-radio>
          <a-radio :value="0">禁用</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="排序" name="ord">
        <a-input-number
          v-model:value="formState.ord"
          :min="1"
          :max="99"
          placeholder="请输入排序"
          style="width: 100%"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup>
import { ref, reactive, defineProps, defineEmits, watch } from 'vue'
import { useChannelStore } from '@/stores/channel'

// 定义props和emits
const props = defineProps({
  visible: {
    type: Boolean,
    required: true
  },
  channel: {
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
  name: '',
  description: '',
  status: 1,
  ord: 1
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入频道名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入频道描述', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择是否启用', trigger: 'change' }
  ]
}

// 获取channelStore
const channelStore = useChannelStore()

// 监听channel变化
watch(() => props.channel, (newChannel) => {
  if (newChannel && props.visible) {
    formState.id = newChannel.id
    formState.name = newChannel.name
    formState.description = newChannel.description
    formState.status = newChannel.status ? 1 : 0
    formState.ord = newChannel.ord
  }
}, { deep: true, immediate: true })

// 监听visible变化
watch(() => props.visible, (newVisible) => {
  if (newVisible && props.channel) {
    formState.id = props.channel.id
    formState.name = props.channel.name
    formState.description = props.channel.description
    formState.status = props.channel.status ? 1 : 0
    formState.ord = props.channel.ord
  }
})

// 处理确认
const handleOk = () => {
  formRef.value.validate().then(async () => {
    const success = await channelStore.updateChannel(formState)
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