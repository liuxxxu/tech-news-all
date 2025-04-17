<template>
  <div class="login-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-arrow
      @click-left="onClickLeft"
      fixed
      class="login-navbar"
      :placeholder="false"
    />

    <div class="login-content">
      <!-- 欢迎语 -->
      <div class="welcome-text">
        <h2>欢迎</h2>
        <p>登录您的账号以继续</p>
      </div>

      <!-- 登录方式选择 -->
      <div class="login-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 0 }" 
          @click="activeTab = 0"
        >
          密码登录
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 1 }" 
          @click="activeTab = 1"
        >
          验证码登录
        </div>
      </div>

      <!-- 登录表单 -->
      <van-form @submit="onSubmit" class="login-form">
        <!-- 手机号输入 -->
        <van-field
          v-model="loginForm.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请填写手机号' }]"
          right-icon="phone-o"
        />

        <!-- 密码输入 -->
        <van-field
          v-if="activeTab === 0"
          v-model="loginForm.password"
          :type="showPassword ? 'text' : 'password'"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请填写密码' }]"
          :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
          @click-right-icon="showPassword = !showPassword"
        />

        <!-- 验证码输入 -->
        <van-field
          v-if="activeTab === 1"
          v-model="loginForm.code"
          name="code"
          label="验证码"
          placeholder="请输入验证码"
          :rules="[{ required: true, message: '请填写验证码' }]"
        >
          <template #button>
            <van-button 
              size="small" 
              type="default" 
              @click="getVerifyCode" 
              :disabled="isCounting"
            >
              {{ codeText }}
            </van-button>
          </template>
        </van-field>

        <!-- 记住密码 & 忘记密码 -->
        <div class="login-options" v-if="activeTab === 0">
          <van-checkbox v-model="loginForm.remember" shape="square" icon-size="14px">记住密码</van-checkbox>
          <span class="forgot-password" @click="onForgotPassword">忘记密码？</span>
        </div>

        <!-- 登录按钮 -->
        <div class="login-button">
          <van-button round block type="primary" native-type="submit">
            登录
          </van-button>
        </div>
      </van-form>

      <!-- 其他登录方式 -->
      <div class="other-login">
        <van-divider>其他登录方式</van-divider>
        <div class="social-login">
          <div class="social-icon wechat">
            <van-icon name="wechat" size="24" />
          </div>
          <div class="social-icon qq">
            <van-icon name="qq" size="24" />
          </div>
          <div class="social-icon weibo">
            <van-icon name="weibo" size="24" />
          </div>
        </div>
      </div>

      <!-- 注册入口 -->
      <div class="register-entry">
        <span>还没有账号？</span>
        <span class="register-link" @click="goToRegister">立即注册</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { showToast, showSuccessToast } from '../../utils/vant-ui';
import { loginByPassword, loginByCode, getVerifyCode as getVerifyCodeAPI, getUserInfo } from '@/api/user';
import { useUserStore } from '@/stores';
import { encryptPassword } from '@/utils/crypto';

const router = useRouter();
const userStore = useUserStore();
const activeTab = ref(0);
const showPassword = ref(false);
const countdown = ref(60);
const isCounting = ref(false);
const codeText = ref('获取验证码');

const loginForm = ref({
  phone: '',
  password: '',
  code: '',
  remember: false
});

const onSubmit = async (values) => {
  try {
    let res;
    if (activeTab.value === 0) {
      // 密码登录
      res = await loginByPassword({
        phone: values.phone,
        password: encryptPassword(values.password)
      });
    } else {
      // 验证码登录
      res = await loginByCode({
        phone: values.phone,
        code: values.code
      });
    }
    
    if (res.code === 0) {
      // 保存token
      userStore.setToken(res.data.token);
      
      // 获取用户详细信息
      const userInfoRes = await getUserInfo();
      if (userInfoRes.code === 0) {
        // 保存用户信息
        const userInfo = {
          ...userInfoRes.data,
          // 处理后端返回的null值
          id: userInfoRes.data.id || 0,
          image: userInfoRes.data.image || '',
          identityAuthentication: !!userInfoRes.data.identityAuthentication,
          followCount: userInfoRes.data.followCount || 0,
          fansCount: userInfoRes.data.fansCount || 0
        };
        userStore.setUserInfo(userInfo);
        showSuccessToast('登录成功');
        router.push('/');
      }
    }
  } catch (error) {
    console.error('登录失败:', error);
  }
};

const onClickLeft = () => {
  router.back();
};

const onForgotPassword = () => {
  console.log('忘记密码功能开发中');
  showToast('忘记密码功能开发中');
};

const goToRegister = () => {
  router.push('/register');
};

// 获取验证码
const getVerifyCode = async () => {
  if (isCounting.value) return;
  
  // 验证手机号
  if (!loginForm.value.phone) {
    showToast('请先输入手机号');
    return;
  }
  
  try {
    // 调用获取验证码接口
    await getVerifyCodeAPI(loginForm.value.phone);
    showSuccessToast('验证码已发送');
    isCounting.value = true;
    countdown.value = 60;
    
    const timer = setInterval(() => {
      countdown.value--;
      codeText.value = `${countdown.value}s后重新获取`;
      
      if (countdown.value <= 0) {
        clearInterval(timer);
        isCounting.value = false;
        codeText.value = '获取验证码';
      }
    }, 1000);
  } catch (error) {
    showToast('验证码发送失败');
    console.log('验证码发送失败', error);
  }
};
</script>

<style>
/* 全局样式覆盖 */
body {
  margin: 0;
  padding: 0;
  background-color: #f7f8fa;
}
</style>

<style scoped>
.login-container {
  background-color: #f7f8fa;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}

.login-navbar {
  border-bottom: 1px solid #f2f2f2;
}

:deep(.van-nav-bar) {
  background-color: #ffffff;
  margin-top: 0;
  padding-top: 0;
}

:deep(.van-nav-bar__content) {
  height: 46px;
}

:deep(.van-nav-bar__title) {
  color: #323233;
  font-weight: bold;
}

.login-content {
  padding: 0 16px;
  padding-top: 46px;
}

.welcome-text {
  text-align: center;
  margin: 24px 0;
}

.welcome-text h2 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.welcome-text p {
  font-size: 14px;
  color: #969799;
}

/* 自定义登录方式选择器 */
.login-tabs {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.tab-item {
  padding: 8px 16px;
  margin: 0 4px;
  font-size: 14px;
  color: #969799;
  cursor: pointer;
  position: relative;
}

.tab-item.active {
  color: #1989fa;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #1989fa;
}

.login-form {
  margin-top: 20px;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 12px 0;
}

.forgot-password {
  font-size: 14px;
  color: #1989fa;
}

.login-button {
  margin-top: 20px;
}

.other-login {
  margin-top: 24px;
}

.social-login {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 16px;
}

.social-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: #f2f3f5;
  display: flex;
  justify-content: center;
  align-items: center;
}

.wechat .van-icon {
  color: #07c160;
}

.qq .van-icon {
  color: #1989fa;
}

.weibo .van-icon {
  color: #ee0a24;
}

.register-entry {
  text-align: center;
  margin-top: 24px;
  margin-bottom: 24px;
  font-size: 14px;
}

.register-link {
  color: #1989fa;
  margin-left: 4px;
}
</style> 