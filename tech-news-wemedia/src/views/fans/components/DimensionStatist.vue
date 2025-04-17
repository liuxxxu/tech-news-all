<template>
  <div class="dimension-container">
    <div class="stat-row">
      <div 
        v-for="(item, index) in statistList" 
        :key="index" 
        class="stat-item"
        :style="{
          backgroundColor: colorPlate1[index % colorPlate1.length]
        }"
      >
        <div class="icon-box" :style="{ backgroundColor: colorPlate2[index % colorPlate2.length] }">
          <a-avatar :size="40" :style="{ backgroundColor: colorPlate3[index % colorPlate3.length] }">
            {{ getFirstChar(item.text) }}
          </a-avatar>
        </div>
        <div class="content">
          <div class="number">{{ item.number }}</div>
          <div class="text">{{ item.text }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 基础数据
const statistList = ref([
  {
    number: '3,356',
    text: '累计粉丝数量'
  },
  {
    number: '3,235',
    text: '粉丝累计阅读量'
  },
  {
    number: '2,567',
    text: '粉丝收益(元)'
  },
  {
    number: '834',
    text: '取消关注量'
  }
])

// 配色方案
const colorPlate1 = ['#E2F6E9', '#FFF0E9', '#F6F3FF', '#E5F6FF']
const colorPlate2 = ['#C3E8D1', '#FFDAC9', '#D9D3FF', '#B7E6FF']
const colorPlate3 = ['#3BD396', '#FF562D', '#5A3ED4', '#2C71FF']

// 获取文本首字符
const getFirstChar = (text) => {
  return text ? text.charAt(0) : ''
}
</script>

<style lang="scss" scoped>
.dimension-container {
  padding: 0 20px;
  
  .stat-row {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    
    .stat-item {
      width: 23%;
      height: 120px;
      display: flex;
      align-items: center;
      padding: 0 20px;
      border-radius: 8px;
      margin-bottom: 20px;
      
      .icon-box {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 20px;
      }
      
      .content {
        flex: 1;
        
        .number {
          font-size: 24px;
          font-weight: 600;
          color: #000;
          margin-bottom: 10px;
        }
        
        .text {
          font-size: 14px;
          color: rgba(0, 0, 0, 0.65);
        }
      }
    }
  }
}
</style> 