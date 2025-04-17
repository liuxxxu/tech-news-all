<template>
  <div class="reset-password-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-arrow
      @click-left="onBack"
      fixed
      left-arrow-color="#3478F6"
    />

    <!-- 表单内容 -->
    <div class="form-content">
      <van-form @submit="onSubmit">
        <!-- 旧密码 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.oldPassword"
            name="oldPassword"
            label="旧密码"
            type="password"
            placeholder="请输入当前密码"
            :rules="[{ required: true, message: '请填写当前密码' }]"
          />
        </van-cell-group>

        <!-- 新密码 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.newPassword"
            name="newPassword"
            label="新密码"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入新密码"
            :rules="[{ required: true, message: '请填写新密码' }]"
            :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
            @click-right-icon="showPassword = !showPassword"
          />
        </van-cell-group>

        <!-- 密码强度指示器 -->
        <div class="password-strength">
          <div class="strength-bar">
            <div class="bar-item" :class="{ active: passwordStrength >= 1 }"></div>
            <div class="bar-item" :class="{ active: passwordStrength >= 2 }"></div>
            <div class="bar-item" :class="{ active: passwordStrength >= 3 }"></div>
          </div>
          <span class="strength-text">密码强度：{{ strengthText }}</span>
        </div>

        <!-- 确认新密码 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.confirmPassword"
            name="confirmPassword"
            label="确认新密码"
            :type="showConfirmPassword ? 'text' : 'password'"
            placeholder="请再次输入新密码"
            :rules="[
              { required: true, message: '请确认新密码' },
              { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
            ]"
            :right-icon="showConfirmPassword ? 'eye-o' : 'closed-eye'"
            @click-right-icon="showConfirmPassword = !showConfirmPassword"
          />
        </van-cell-group>

        <!-- 提交按钮 -->
        <div class="submit-btn">
          <van-button round block type="primary" native-type="submit">
            确认修改
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { resetPassword } from '@/api/user'
import { useUserStore } from '@/stores'
import { encryptPassword } from '@/utils/crypto'

const router = useRouter()
const userStore = useUserStore()
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// 表单数据
const formData = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = formData.value.newPassword
  if (!password) return 0
  
  let strength = 0
  // 长度大于8
  if (password.length >= 8) strength++
  // 包含数字和字母
  if (/[a-zA-Z]/.test(password) && /[0-9]/.test(password)) strength++
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(password)) strength++
  
  return strength
})

const strengthText = computed(() => {
  const strength = passwordStrength.value
  return strength === 0 ? '弱' : strength === 1 ? '弱' : strength === 2 ? '中' : '强'
})

// 校验确认密码
const validateConfirmPassword = (val) => {
  return val === formData.value.newPassword
}

// 返回上一页
const onBack = () => {
  router.back()
}

// 提交表单
const onSubmit = async () => {
  try {
    // 验证密码强度
    if (passwordStrength.value < 2) {
      throw new Error('密码强度不足，请设置更复杂的密码')
    }

    await resetPassword({
      oldPassword: encryptPassword(formData.value.oldPassword),
      newPassword: encryptPassword(formData.value.newPassword)
    })
    
    showSuccessToast('修改密码成功')
    // 退出登录
    userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}
</script>

<style scoped>
.reset-password-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 20px;
  padding-top: 46px;
}

.form-content {
  padding: 20px 0;
}

.submit-btn {
  padding: 20px 16px;
  margin-top: 24px;
}

.van-cell-group {
  margin-bottom: 12px;
}

.password-strength {
  padding: 0 16px;
  margin-top: 8px;
  margin-bottom: 16px;
}

.strength-bar {
  display: flex;
  gap: 4px;
}

.bar-item {
  flex: 1;
  height: 4px;
  background-color: #ebedf0;
  border-radius: 2px;
}

.bar-item.active:nth-child(1) {
  background-color: #ee0a24;
}

.bar-item.active:nth-child(2) {
  background-color: #ff976a;
}

.bar-item.active:nth-child(3) {
  background-color: #07c160;
}

.strength-text {
  font-size: 12px;
  color: #969799;
  margin-top: 4px;
  display: inline-block;
}
</style> 