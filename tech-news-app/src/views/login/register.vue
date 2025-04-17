<template>
  <div class="register-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-arrow
      @click-left="onClickLeft"
      fixed
      class="register-navbar"
      :placeholder="false"
    />

    <div class="register-content">
      <!-- 欢迎语 -->
      <div class="welcome-text">
        <h2>创建新账号</h2>
        <p>请填写以下信息完成注册</p>
      </div>

      <!-- 注册表单 -->
      <van-form @submit="onSubmit" class="register-form">
        <!-- 手机号输入 -->
        <van-field
          v-model="registerForm.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请填写手机号' }]"
          right-icon="phone-o"
        />

        <!-- 验证码 -->
        <!-- <van-field
          v-model="registerForm.code"
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
        </van-field> -->

        <!-- 密码输入 -->
        <van-field
          v-model="registerForm.password"
          :type="showPassword ? 'text' : 'password'"
          name="password"
          label="设置密码"
          placeholder="请设置密码"
          :rules="[{ required: true, message: '请设置密码' }]"
          :right-icon="showPassword ? 'eye-o' : 'closed-eye'"
          @click-right-icon="showPassword = !showPassword"
        />

        <!-- 密码强度指示器 -->
        <div class="password-strength">
          <div class="strength-bar">
            <div class="bar-item" :class="{ active: passwordStrength >= 1 }"></div>
            <div class="bar-item" :class="{ active: passwordStrength >= 2 }"></div>
            <div class="bar-item" :class="{ active: passwordStrength >= 3 }"></div>
          </div>
          <span class="strength-text">密码强度：{{ strengthText }}</span>
        </div>

        <!-- 确认密码 -->
        <van-field
          v-model="registerForm.confirmPassword"
          :type="showConfirmPassword ? 'text' : 'password'"
          name="confirmPassword"
          label="确认密码"
          placeholder="请再次输入密码"
          :rules="[
            { required: true, message: '请确认密码' },
            { validator: validateConfirmPassword, message: '两次输入的密码不一致' }
          ]"
          :right-icon="showConfirmPassword ? 'eye-o' : 'closed-eye'"
          @click-right-icon="showConfirmPassword = !showConfirmPassword"
        />

        <!-- 用户协议 -->
        <div class="agreement">
          <van-checkbox v-model="registerForm.agreement" shape="square" icon-size="14px">
            我已阅读并同意
            <span class="agreement-link" @click.stop="showUserAgreement">《用户协议》</span>
            和
            <span class="agreement-link" @click.stop="showPrivacyPolicy">《隐私政策》</span>
          </van-checkbox>
        </div>

        <!-- 注册按钮 -->
        <div class="register-button">
          <van-button round block type="primary" native-type="submit" :disabled="!registerForm.agreement">
            注册
          </van-button>
        </div>
      </van-form>

      <!-- 其他注册方式 -->
      <div class="other-register">
        <van-divider>其他注册方式</van-divider>
        <div class="social-register">
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

      <!-- 登录入口 -->
      <div class="login-entry">
        <span>已有账号？</span>
        <span class="login-link" @click="goToLogin">立即登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '@/api/user';
import { encryptPassword } from '@/utils/crypto';

const router = useRouter();
const showPassword = ref(false);
const showConfirmPassword = ref(false);

const registerForm = ref({
  phone: '',
  password: '',
  confirmPassword: '',
  agreement: false
});

// 密码强度计算
const passwordStrength = computed(() => {
  const password = registerForm.value.password;
  if (!password) return 0;
  
  let strength = 0;
  // 长度大于8
  if (password.length >= 8) strength++;
  // 包含数字和字母
  if (/[a-zA-Z]/.test(password) && /[0-9]/.test(password)) strength++;
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(password)) strength++;
  
  return strength;
});

const strengthText = computed(() => {
  const strength = passwordStrength.value;
  return strength === 0 ? '弱' : strength === 1 ? '弱' : strength === 2 ? '中' : '强';
});

// 验证确认密码
const validateConfirmPassword = () => {
  return registerForm.value.password === registerForm.value.confirmPassword;
};

const onSubmit = async (values) => {
  try {
    // 验证密码强度
    if (passwordStrength.value < 2) {
      throw new Error('密码强度不足，请设置更复杂的密码')
    }
    
    // 调用注册接口
    const res = await register({
      phone: values.phone,
      password: encryptPassword(values.password)
    });
    
    if (res.code === 0) {
      showSuccessToast('注册成功');
      router.push('/login');
    }
  } catch (error) {
    console.error('注册失败:', error);
  }
};

const onClickLeft = () => {
  router.back();
};

const showUserAgreement = () => {
  showToast({
    message: '用户协议：\n1. 禁止发布违法、违规内容\n2. 禁止发布垃圾广告信息\n3. 尊重知识产权，避免侵权行为\n4. 遵守社区规范，维护良好秩序',
    duration: 5000
  })
};

const showPrivacyPolicy = () => {
  showToast({
    message: '隐私政策：\n1. 我们会保护您的个人信息安全\n2. 未经授权不会向第三方分享信息\n3. 您有权管理自己的个人信息\n4. 您可以随时联系我们进行咨询',
    duration: 5000
  })
};

const goToLogin = () => {
  router.push('/login');
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
.register-container {
  background-color: #f7f8fa;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}

.register-navbar {
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

.register-content {
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

.register-form {
  margin-top: 20px;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
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

.agreement {
  margin: 16px 0;
}

.agreement-link {
  color: #1989fa;
}

.register-button {
  margin-top: 20px;
}

.other-register {
  margin-top: 24px;
}

.social-register {
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

.login-entry {
  text-align: center;
  margin-top: 24px;
  margin-bottom: 24px;
  font-size: 14px;
}

.login-link {
  color: #1989fa;
  margin-left: 4px;
}
</style> 