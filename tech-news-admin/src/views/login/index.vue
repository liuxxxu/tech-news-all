<template>
  <div class="login-container">
    <div class="login-content">
      <!-- 欢迎语 -->
      <div class="welcome-text">
        <h2>tech-news 管理后台</h2>
      </div>

      <!-- 登录表单 -->
      <a-form
        :model="loginForm"
        @finish="onSubmit"
        class="login-form"
      >
<<<<<<< HEAD
        <!-- 用户名输入 -->
        <a-form-item
          name="phone"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input
            v-model:value="loginForm.phone"
            placeholder="请输入用户名"
=======
        <!-- 手机号输入 -->
        <a-form-item
          name="phone"
          :rules="[{ required: true, message: '请输入手机号' }]"
        >
          <a-input
            v-model:value="loginForm.phone"
            placeholder="请输入手机号"
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
            size="large"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <!-- 密码输入 -->
        <a-form-item
          name="password"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <a-input-password
            v-model:value="loginForm.password"
            placeholder="请输入密码"
            size="large"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <!-- 记住密码 -->
        <a-form-item>
          <a-checkbox v-model:checked="loginForm.remember">记住密码</a-checkbox>
        </a-form-item>

        <!-- 登录按钮 -->
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { loginByPassword, getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores'
import { encryptPassword } from '@/utils/crypto'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  phone: '',
  password: '',
  remember: false
})

const onSubmit = async (values) => {
  try {
    const res = await loginByPassword({
<<<<<<< HEAD
      name: values.phone,
=======
      name: values.name,
>>>>>>> 21591dd9b99b39840b29124e911a94251dc568f9
      password: encryptPassword(values.password)
    })
    
    if (res.code === 0) {
      // 保存用户信息和token
      const userInfo = {
        name: values.name,
        token: res.data.token
      }
      userStore.setUser(userInfo)
      localStorage.setItem('token', res.data.token)
      
      message.success('登录成功')
      
      // 获取重定向地址，如果没有则跳转到首页
      const redirect = router.currentRoute.value.query.redirect || '/home'
      router.push(redirect)
    }
  } catch (error) {
    console.error('登录失败:', error)
    message.error('登录失败，请检查用户名和密码')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.login-content {
  width: 368px;
  padding: 32px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.welcome-text {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-text h2 {
  font-size: 33px;
  font-weight: 600;
  margin-bottom: 12px;
}

.welcome-text p {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
}

.login-form {
  margin-top: 24px;
}
</style> 