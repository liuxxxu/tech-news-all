<template>
  <div class="category-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-text="返回"
      left-arrow
      right-text="完成"
      @click-left="onBackClick"
      @click-right="onDoneClick"
      fixed
      class="category-nav"
    />

    <!-- 我的分类 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">我的分类</span>
        <span class="section-subtitle">拖动调整顺序</span>
      </div>
      
      <div class="category-grid">
        <div 
          v-for="(item, index) in myCategories" 
          :key="item.id" 
          class="category-item"
          :class="{ 'not-removable': !item.removable }"
        >
          {{ item.name }}
          <van-icon 
            v-if="item.removable" 
            name="cross" 
            class="remove-icon" 
            @click="removeCategory(item)" 
          />
        </div>
      </div>

      <div class="tip-text">
        提示：长按拖动分类可调整顺序，点击删除图标可移除分类（推荐和热点不可删除）
      </div>
    </div>

    <!-- 更多分类 -->
    <div class="section">
      <div class="section-header">
        <span class="section-title">更多分类</span>
        <span class="section-subtitle">点击添加分类</span>
      </div>
      
      <div class="category-grid">
        <div 
          v-for="item in moreCategories" 
          :key="item.id" 
          class="category-item"
          @click="addCategory(item)"
        >
          {{ item.name }}
          <van-icon name="plus" class="add-icon" />
        </div>
      </div>
    </div>

    <!-- 底部重置按钮 -->
    <div class="reset-button">
      <van-button block plain type="primary" @click="resetCategories">
        恢复默认
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast } from '../../utils/vant-ui'

const router = useRouter()

// 我的分类列表
const myCategories = ref([
  { id: 1, name: '推荐', removable: false },
  { id: 3, name: '科技', removable: true },
  { id: 4, name: '财经', removable: true },
  { id: 5, name: '体育', removable: true },
  { id: 6, name: '娱乐', removable: true },
  { id: 7, name: '军事', removable: true },
  { id: 8, name: '国际', removable: true },
])

// 更多分类列表
const moreCategories = ref([
  { id: 9, name: '时政', removable: true },
  { id: 10, name: '文化', removable: true },
  { id: 11, name: '健康', removable: true },
  { id: 12, name: '教育', removable: true },
  { id: 13, name: '汽车', removable: true },
  { id: 14, name: '旅游', removable: true },
  { id: 15, name: '美食', removable: true },
  { id: 16, name: '数码', removable: true },
  { id: 17, name: '游戏', removable: true },
  { id: 18, name: '房产', removable: true },
  { id: 19, name: '时尚', removable: true },
  { id: 20, name: '育儿', removable: true },
])

// 返回上一页
const onBackClick = () => {
  router.back()
}

// 完成分类管理
const onDoneClick = () => {
  showSuccessToast('保存成功')
  router.back()
}

// 保存分类设置
const saveCategories = () => {
  // 保存分类的方法，调用API保存用户设置
  showSuccessToast('保存成功')
}

// 添加分类
const addCategory = (item) => {
  // 检查是否已经存在于我的分类
  if (myCategories.value.some(c => c.id === item.id)) {
    showToast('该分类已添加')
    return
  }

  // 添加到我的分类
  myCategories.value.push({
    id: item.id,
    name: item.name,
    removable: true
  })
  showSuccessToast('添加成功')
}

// 移除分类
const removeCategory = (item) => {
  if (!item.removable) {
    showToast('该分类不可移除')
    return
  }

  // 从我的分类中移除
  const index = myCategories.value.findIndex(c => c.id === item.id)
  if (index !== -1) {
    myCategories.value.splice(index, 1)
    showSuccessToast('移除成功')
  }
}

// 重置分类
const resetCategories = () => {
  // 重置为默认分类
  myCategories.value = [
    { id: 1, name: '推荐', removable: false },
    { id: 2, name: '热点', removable: false },
    { id: 3, name: '科技', removable: true },
    { id: 4, name: '财经', removable: true },
    { id: 5, name: '体育', removable: true }
  ]

  showToast('已恢复默认设置')
}
</script>

<style scoped>
.category-container {
  padding-top: 46px;
  padding-bottom: 20px;
  background-color: #f7f8fa;
  min-height: 100vh;
}

.category-nav {
  border-bottom: 1px solid #f0f0f0;
}

.category-nav :deep(.van-nav-bar__title) {
  font-weight: 500;
}

.category-nav :deep(.van-nav-bar__text) {
  color: #3478F6;
}

.section {
  margin: 16px;
  padding: 16px;
  background-color: #fff;
  border-radius: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.section-subtitle {
  font-size: 14px;
  color: #999;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.category-item {
  position: relative;
  height: 44px;
  background-color: #f7f8fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 14px;
  color: #333;
  border: 1px solid #f0f0f0;
}

.not-removable {
  color: #3478F6;
}

.remove-icon, .add-icon {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 16px;
  height: 16px;
  line-height: 16px;
  text-align: center;
  background-color: #f56c6c;
  color: white;
  border-radius: 50%;
  font-size: 12px;
}

.add-icon {
  background-color: #3478F6;
}

.tip-text {
  margin-top: 16px;
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}

.reset-button {
  margin: 24px 16px;
}
</style> 