<template>
  <div class="wrapper">
    <div class="login-box clearfix">
      <div class="inline-block welcome-box fl">
        <div class="title-container">
          <div class="welcome">欢迎使用</div>
          <div class="project">Tech-news 自媒体人系统</div>
        </div>
      </div>
      <div class="inline-block form-box fr">
        <div class="form-container">
          <a-form
            :model="formState"
            name="loginForm"
            :rules="rules"
            ref="formRef"
            class="login-form"
          >
            <div class="input-label name-input-label">手机号 / phone</div>
            <a-form-item name="phone" class="name-input-box">
              <a-input
                v-model:value="formState.phone"
                placeholder="请输入手机号"
              ></a-input>
            </a-form-item>
            <div class="input-label password-input-label">密码 / password</div>
            <a-form-item name="password" class="password-input-box">
              <a-input-password
                v-model:value="formState.password"
                placeholder="请输入密码"
              ></a-input-password>
            </a-form-item>
            <a-form-item name="agreement" class="agreement-input-box">
              <a-checkbox v-model:checked="formState.agreement">我已阅读并同意用户协议和隐私条款</a-checkbox>
            </a-form-item>
            <a-form-item class="login-btn" :class="validateState ? 'enabled' : ''">
              <a-button type="primary" :disabled="!validateState" @click="submitForm">登 录</a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { loginByUsername } from '@/api/login'
import { useUserStore } from '@/stores'
import { encryptPassword } from '@/utils/crypto'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)

const formState = reactive({
  phone: '',
  password: '',
  agreement: true
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'change' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'change' }
  ],
  agreement: [
    { 
      validator: (rule, value) => {
        if (!value) {
          return Promise.reject('请阅读并同意协议')
        }
        return Promise.resolve()
      },
      trigger: 'change'
    }
  ]
}

const validateState = computed(() => {
  return formState.phone !== '' && formState.password !== '' && formState.agreement
})

const submitForm = async () => {
  try {
    await formRef.value.validate()
    const { phone, password } = formState

    const loginData = {
      phone,
      password: encryptPassword(password)
    }

    const res = await loginByUsername(loginData)
    
    if (res.code === 0) {
      // 保存用户信息和token
      const userInfo = {
        ...res.data.user,
        phone,
        token: res.data.token
      }
      userStore.setUser(userInfo)
      
      message.success('登录成功')
      
      // 获取重定向地址，如果没有则跳转到首页
      const redirect = router.currentRoute.value.query.redirect || '/home'
      router.push(redirect)
    } else {
      message.error(res.errorMessage || '登录失败')
    }
  } catch (err) {
    console.log('登录失败:', err)
  }
}
</script>

<style lang="scss" scoped>
.wrapper {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
  min-width: 1070px;
  overflow-x: auto;

  .login-box {
    width: 1070px;
    height: 594px;
    background: #FFFFFF;
    box-shadow: 0px 0px 30px 15px rgba(25, 25, 99, 0.06);
    border-radius: 18px;
    display: flex;

    .welcome-box {
      width: 60%;
      height: 100%;
      background: linear-gradient(180deg, #597EF7 0%, #3175FB 100%);
      border-radius: 18px 0px 0px 18px;
      display: flex;
      align-items: center;
      justify-content: center;

      .title-container {
        text-align: center;

        .welcome {
          font-size: 40px;
          font-family: PingFangSC-Thin, PingFang SC;
          font-weight: 100;
          color: #FFFFFF;
          line-height: 56px;
        }

        .project {
          font-size: 32px;
          font-family: PingFangSC-Semibold, PingFang SC;
          font-weight: 600;
          color: #FFFFFF;
          line-height: 45px;
          margin-top: 10px;
        }
      }
    }

    .form-box {
      width: 40%;
      height: 100%;
      background: #FFFFFF;
      border-radius: 0px 18px 18px 0px;
      display: flex;
      align-items: center;

      .form-container {
        width: 100%;
      }

      .login-form {
        width: 300px;
        margin: 0 auto;
        text-align: center;

        .input-label {
          font-size: 16px;
          font-weight: 400;
          color: #BAC0CD;
          line-height: 22px;
          text-align: left;
        }

        .name-input-label {
          margin-top: 0;
        }

        .name-input-box {
          margin-top: 13px;
        }

        .password-input-label {
          margin-top: 24px;
        }

        .password-input-box {
          margin-top: 13px;
        }

        .agreement-input-box {
          margin-top: 40px;
          text-align: left;
          
          :deep(.ant-checkbox) + span {
            color: #BAC0CD;
          }
        }

        :deep(.ant-input),
        :deep(.ant-input-password) {
          width: 300px;
          height: 50px;
          background: #F3F4F7;
          border-radius: 30px;
          border: 1px solid #D5E6FF;
          text-align: left;
          padding-left: 20px;
        }
        
        :deep(.ant-input) {
          line-height: 48px;
        }

        :deep(.ant-input-password) {
          display: flex;
          align-items: center;
          padding-right: 11px;
        }

        :deep(.ant-input-password .ant-input) {
          height: 46px;
          line-height: 46px;
          border: none;
          box-shadow: none;
          background: transparent;
          padding-left: 0;
        }
        
        :deep(.ant-input-password-icon) {
          margin-right: 10px;
          color: #BAC0CD;
        }

        :deep(.ant-input:hover),
        :deep(.ant-input:focus),
        :deep(.ant-input-password:hover),
        :deep(.ant-input-password:focus-within),
        :deep(.ant-input-password:hover .ant-input),
        :deep(.ant-input-password:focus-within .ant-input) {
          border-color: #3175FB;
        }

        :deep(.ant-form-item-has-error .ant-input),
        :deep(.ant-form-item-has-error .ant-input-password .ant-input) {
          border-color: #FF5C5C;
        }

        .login-btn {
          :deep(.ant-btn) {
            width: 300px;
            height: 60px;
            margin-top: 18px;
            background: #BFD0F2;
            border-radius: 30px;
            font-size: 18px;
            font-weight: 400;
            color: #FFFFFF;
            border: none;
          }
        }

        .enabled {
          :deep(.ant-btn-primary) {
            background: #3175FB;
          }
        }
      }
    }
  }
}

// 清除浮动样式
.clearfix:after {
  content: "";
  display: block;
  height: 0;
  clear: both;
  visibility: hidden;
}
.clearfix {
  zoom: 1;
}
.fl {
  float: left;
}
.fr {
  float: right;
}
.inline-block {
  display: inline-block;
}
.block {
  display: block;
}
</style> 