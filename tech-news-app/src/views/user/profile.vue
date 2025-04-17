<template>
  <div class="user-profile-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-arrow
      @click-left="onBack"
      fixed
      left-arrow-color="#3478F6"
      title="编辑资料"
    />

    <!-- 表单内容 -->
    <div class="form-content">
      <van-form @submit="onSubmit">
        <!-- 头像 -->
        <div class="avatar-upload">
          <van-image
            round
            width="80"
            height="80"
            :src="previewImage || avatar"
            alt="用户头像"
          />
          <div class="upload-btn" @click="handleUploadClick">更换头像</div>
          <input
            type="file"
            ref="fileInput"
            accept="image/*"
            style="display: none"
            @change="handleFileChange"
          />
        </div>

        <!-- 昵称 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.nickname"
            name="nickname"
            label="昵称"
            placeholder="请输入昵称"
            :rules="[{ required: true, message: '请填写昵称' }]"
          />
        </van-cell-group>

                <!-- 手机号 -->
        <van-cell-group inset>
          <van-field
            disabled
            v-model="formData.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            :rules="[{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }]"
          />
        </van-cell-group>

        <!-- 性别 -->
        <van-cell-group inset>
          <van-field
            name="gender"
            label="性别"
          >
            <template #input>
              <van-radio-group v-model="formData.gender" direction="horizontal">
                <van-radio name="male">男</van-radio>
                <van-radio name="female">女</van-radio>
                <van-radio name="secret">保密</van-radio>
              </van-radio-group>
            </template>
          </van-field>
        </van-cell-group>


        <!-- 邮箱 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.email"
            name="email"
            label="邮箱"
            placeholder="请输入邮箱（选填）"
          />
        </van-cell-group>

        <!-- 简介 -->
        <van-cell-group inset>
          <van-field
            v-model="formData.bio"
            name="bio"
            label="简介"
            type="textarea"
            rows="3"
            autosize
            placeholder="请输入个人简介（选填）"
          />
        </van-cell-group>

        <!-- 提交按钮 -->
        <div class="submit-btn">
          <van-button round block type="primary" native-type="submit">
            保存
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showSuccessToast, showToast, showLoadingToast, closeToast } from '@/utils/vant-ui'
import { useUserStore } from '@/stores'
import { updateUserInfo, uploadImage } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const fileInput = ref(null)
const previewImage = ref('')

// 表单数据
const formData = ref({
  nickname: userStore.userInfo.name || '',
  gender: userStore.userInfo.sex === 0 ? 'male' : (userStore.userInfo.sex === 1 ? 'female' : 'secret'),
  phone: userStore.userInfo.phone || '',
  email: userStore.userInfo.email || '',
  bio: userStore.userInfo.description || ''
})

// 头像
const avatar = computed(() => userStore.userInfo.image || '/src/assets/portrait.png')

// 点击更换头像按钮
const handleUploadClick = () => {
  fileInput.value.click()
}

// 文件选择改变
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    // 验证文件类型
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png']
    if (!allowedTypes.includes(file.type)) {
      showToast('只支持jpg、jpeg和png格式的图片')
      return
    }

    // 验证文件大小（限制为2MB）
    if (file.size > 2 * 1024 * 1024) {
      showToast('图片大小不能超过2MB')
      return
    }

    // 创建预览
    const reader = new FileReader()
    reader.onload = (e) => {
      previewImage.value = e.target.result
    }
    reader.readAsDataURL(file)

    // 保存文件对象以供后续上传
    selectedFile.value = file
  } catch (error) {
    console.error('文件处理失败:', error)
    showToast('文件处理失败')
    // 校验失败时保持原图片
    previewImage.value = userStore.userInfo.image || ''
  } finally {
    // 清空input，以便可以重复选择同一文件
    event.target.value = ''
  }
}

// 返回上一页
const onBack = () => {
  router.back()
}

// 提交表单
const onSubmit = async () => {
  try {
    showLoadingToast({
      message: '保存中...',
      forbidClick: true,
      duration: 0
    })

    let imageUrl = userStore.userInfo.image

    // 如果选择了新头像，先上传头像
    if (selectedFile.value) {
      try {
        imageUrl = await uploadImage(selectedFile.value, 'avatar')
      } catch (error) {
        showToast(error.message)
        return
      }
    }

    // 更新用户信息
    const updateData = {
      image: imageUrl || '',
      name: formData.value.nickname,
      sex: getGenderValue(formData.value.gender),
      phone: formData.value.phone || '',
      email: formData.value.email || '',
      description: formData.value.bio || ''
    }

    const res = await updateUserInfo(updateData)
    
    if (res.code === 0) {
      showSuccessToast('保存成功')
      // 更新本地用户信息
      userStore.setUserInfo({
        ...userStore.userInfo,
        ...updateData
      })
      setTimeout(() => {
        router.back()
      }, 1000)
    }
  } catch (error) {
    console.error('保存失败:', error)
    showToast('保存失败')
  } finally {
    closeToast()
  }
}

// 添加文件选择ref
const selectedFile = ref(null)

// 获取性别数值
const getGenderValue = (gender) => {
  switch (gender) {
    case 'male':
      return 0
    case 'female':
      return 1
    default:
      return 2
  }
}
</script>

<style scoped>
.user-profile-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 20px;
  padding-top: 46px;
}

.form-content {
  padding: 20px 0;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 24px 0;
}

.upload-btn {
  margin-top: 12px;
  color: #3478F6;
  font-size: 14px;
  cursor: pointer;
}

.submit-btn {
  padding: 20px 16px;
}

.van-cell-group {
  margin-bottom: 12px;
}
</style> 