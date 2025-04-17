<template>
  <a-image :src="imgSrc">
    <template #placeholder>
      <div class="image-slot">
        <a-spin />
      </div>
    </template>
    <template #fallback>
      <div class="image-slot">
        <a-alert message="图片加载失败" type="error" />
      </div>
    </template>
  </a-image>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  imgSrc: {
    type: String,
    required: true
  }
})
</script>

<style scoped>
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 200px;
  background-color: #f5f5f5;
}

.a-image {
  width: 100%;
  margin-top: 20px;
  margin-bottom: 10px;
}
</style> 