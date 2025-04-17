<template>
  <div class="empty">
    <img class="img" src="@/assets/img_nodata@2x.png">
    <span class="tips">没有数据</span>
  </div>
</template>

<script setup>
// 空数据展示组件
</script>

<style lang="scss" scoped>
.empty {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 583px;

  .img {
    width: 245px;
    height: 201px;
  }

  .tips {
    color: #C0C4CC;
  }
}
</style> 