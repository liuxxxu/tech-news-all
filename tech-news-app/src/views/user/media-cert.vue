<template>
  <div class="media-cert-container">
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
        <!-- 真实姓名 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.realName"
            name="realName"
            label="真实姓名"
            placeholder="请输入真实姓名"
            :rules="[{ required: true, message: '请填写真实姓名' }]"
          />
        </van-cell-group>

        <!-- 手机号
        <van-cell-group inset>
          <van-field
            v-model="formData.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[
              { required: true, message: '请填写手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
            ]"
          />
        </van-cell-group> -->

        <!-- 身份证号 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.idCard"
            name="idCard"
            label="身份证号"
            placeholder="请输入身份证号"
            :rules="[
              { required: true, message: '请填写身份证号' },
              { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号' }
            ]"
          />
        </van-cell-group>

        <!-- 身份证正面照片 -->
        <div class="upload-section">
          <div class="upload-title">身份证正面照片</div>
          <div class="upload-area">
            <van-uploader
              :after-read="(file) => afterUpload(file, 'frontImage')"
              :max-count="1"
            >
              <div class="upload-button" v-if="!formData.frontImage">
                <van-icon name="photograph" size="24" />
                <div class="upload-text">上传正面照片</div>
              </div>
              <van-image v-else :src="formData.frontImage" width="100" height="100" />
            </van-uploader>
          </div>
        </div>

        <!-- 身份证反面照片 -->
        <div class="upload-section">
          <div class="upload-title">身份证反面照片</div>
          <div class="upload-area">
            <van-uploader
              :after-read="(file) => afterUpload(file, 'backImage')"
              :max-count="1"
            >
              <div class="upload-button" v-if="!formData.backImage">
                <van-icon name="photograph" size="24" />
                <div class="upload-text">上传反面照片</div>
              </div>
              <van-image v-else :src="formData.backImage" width="100" height="100" />
            </van-uploader>
          </div>
        </div>

        <!-- 协议 -->
        <div class="agreement-section">
          <van-checkbox v-model="formData.agreed" shape="square" icon-size="16">
            <span class="agreement-text">
              我已阅读并同意
              <span class="link" @click.stop="showAgreement('media')">《自媒体认证协议》</span>
              和
              <span class="link" @click.stop="showAgreement('platform')">《平台规范》</span>
            </span>
          </van-checkbox>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-btn">
          <van-button 
            round 
            block 
            type="primary" 
            native-type="submit"
            :disabled="!formData.agreed || !isFormValid"
          >
            提交认证
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast } from '@/utils/vant-ui'
import { submitMediaCert, uploadImage } from '@/api/user'

const router = useRouter()

// 表单数据
const formData = ref({
  realName: '',
  phone: '',
  idCard: '',
  frontImage: '',
  backImage: '',
  agreed: false
})

// 判断表单是否有效
const isFormValid = computed(() => {
  return (
    formData.value.realName &&
    formData.value.idCard &&
    formData.value.frontImage &&
    formData.value.backImage
  )
})

// 上传处理
const afterUpload = async (file, type) => {
  try {
    const imageUrl = await uploadImage(file.file, 'identityCard')
    formData.value[type] = imageUrl
    return { ...file, url: imageUrl }
  } catch (error) {
    return false
  }
}

// 显示协议
const showAgreement = (type) => {
  if (type === 'media') {
    showToast('《自媒体认证协议》：认证成为自媒体后，您需要遵守平台规定，发布高质量原创内容，平台将根据内容质量和流量给予相应收益')
  } else if (type === 'platform') {
    showToast('《平台规范》：禁止发布违法、低俗、虚假信息，尊重原创，杜绝抄袭，平台有权对违规账号进行处罚')
  }
}

// 返回上一页
const onBack = () => {
  router.back()
}

// 提交表单
const onSubmit = async () => {
  const submitData = {
    name: formData.value.realName,
    idno: formData.value.idCard,
    frontImage: formData.value.frontImage,
    backImage: formData.value.backImage
  }

  const res = await submitMediaCert(submitData)
  
  if (res.code === 0) {
    showSuccessToast('提交成功，我们将在1-3个工作日内审核')
    setTimeout(() => {
      router.back()
    }, 1500)
  }
}
</script>

<style scoped>
.media-cert-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 20px;
  padding-top: 46px;
}

.form-content {
  padding: 20px 0;
}

.upload-section {
  margin: 16px;
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
}

.upload-title {
  font-size: 14px;
  color: #323233;
  margin-bottom: 12px;
}

.upload-area {
  display: flex;
  justify-content: center;
}

.upload-button {
  width: 100px;
  height: 100px;
  background-color: #f7f8fa;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 4px;
}

.upload-text {
  font-size: 12px;
  color: #969799;
  margin-top: 8px;
}

.agreement-section {
  margin: 24px 16px 16px;
}

.agreement-text {
  font-size: 13px;
  color: #646566;
}

.link {
  color: #3478F6;
}

.submit-btn {
  padding: 16px;
  margin-top: 24px;
}

.van-cell-group {
  margin-bottom: 12px;
}
</style> 