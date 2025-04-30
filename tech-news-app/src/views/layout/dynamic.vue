<template>
    <div class="dynamic-container">
        <!-- 顶部导航栏 -->
        <van-nav-bar fixed class="custom-nav">
            <template #left>
                <div class="avatar-search">
                    <div class="avatar-wrapper" @click="goToProfile">
                        <van-image round width="32" height="32" :src="userAvatar" alt="用户头像" class="avatar" />
                    </div>
                    <van-search v-model="searchValue" placeholder="搜索动态" shape="round" class="search-input"
                        background="transparent" readonly @click="goToSearch" />
                </div>
            </template>
            <template #right>
                <div class="notification">
                    <van-icon name="plus" size="22" color="#3478F6" @click="handleNewPost" />
                </div>
            </template>
        </van-nav-bar>

        <!-- 头像滑动栏 -->
        <div class="avatar-scroll-container">
            <div class="avatar-scroll-wrapper">
                <!-- <div class="avatar-item">
          <van-image
            round
            width="50"
            height="50"
            src="https://img01.yzcdn.cn/vant/cat.jpeg"
            alt=""
          />
          <div class="avatar-name">我</div>
        </div> -->
                <div class="avatar-item">
                    <van-image round width="50" height="50"
                        src="https://bpic.588ku.com/back_list_pic/23/05/09/8eda9d7cb7fdbe825429685f98aa9822.jpg"
                        alt="科技日报" />
                    <div class="avatar-name">科技日报</div>
                </div>
                <div class="avatar-item">
                    <van-image round width="50" height="50"
                        src="https://ts1.tc.mm.bing.net/th/id/R-C.fcc7a7649912bdb4dab257256388f684?rik=4F0Kd6%2f3UDcowg&riu=http%3a%2f%2fn.sinaimg.cn%2fsinakd20111%2f762%2fw1000h562%2f20220819%2fb9a4-9a10a4a9b5f9fbddc11cea5548a84e0b.jpg&ehk=onEJRZ%2f%2f6OwdCVJ4BiLLM3anjYJ0lyBmK6%2bI0psoYvI%3d&risl=&pid=ImgRaw&r=0"
                        alt="科技周刊" />
                    <div class="avatar-name">财经周刊</div>
                </div>
                <div class="avatar-item">
                    <van-image round width="50" height="50"
                        src="https://ts1.tc.mm.bing.net/th/id/R-C.699f5e2f78822a1084872b9d4bdbf633?rik=GZaDv3GTjDyFZg&riu=http%3a%2f%2fimg95.699pic.com%2fphoto%2f50085%2f4215.jpg_wh860.jpg&ehk=6DXACoTdGA0DggtTFfDC6%2bdvohhlvpNcwiZnQn3%2fOIA%3d&risl=&pid=ImgRaw&r=0"
                        alt="体育新闻" />
                    <div class="avatar-name">体育新闻</div>
                </div>
                <div class="avatar-item">
                    <van-image round width="50" height="50"
                        src="https://img95.699pic.com/photo/40086/5739.jpg_wh860.jpg" alt="娱乐圈" />
                    <div class="avatar-name">科技娱乐圈</div>
                </div>
                <!-- <div class="avatar-item">
                    <van-image round width="50" height="50" src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
                        alt="军事观察" />
                    <div class="avatar-name">科技观察</div>
                </div> -->
            </div>
        </div>

        <!-- 关注动态列表 -->
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
            <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
                <div class="dynamic-list">
                    <!-- 动态项 -->
                    <div class="dynamic-item" v-for="(item, index) in dynamicList" :key="index">
                        <!-- 用户信息 -->
                        <div class="user-info">
                            <van-image width="42" height="42" round :src="item.avatar" alt="用户头像" />
                            <div class="user-detail">
                                <div class="username">{{ item.username }}</div>
                                <div class="timestamp">{{ item.time }}</div>
                            </div>
                            <!-- <van-button 
                round 
                size="small" 
                class="follow-btn" 
                v-if="!item.following"
                @click="handleFollow(item)"
              >
                关注
              </van-button> -->
                        </div>

                        <!-- 动态内容 -->
                        <div class="dynamic-content">
                            <div class="dynamic-text">{{ item.content }}</div>

                            <!-- 新闻卡片（可点击进入详情） -->
                            <div class="news-card" v-if="item.news" @click="goToDetail(item.news.id)">
                                <div class="news-card-content">
                                    <div class="news-title">{{ item.news.title }}</div>
                                    <div class="news-source">{{ item.news.source }}</div>
                                </div>
                                <van-image width="80" height="80" fit="cover" radius="4" :src="item.news.image"
                                    alt="新闻图片" v-if="item.news.image" />
                            </div>

                            <!-- 图片（如果有） -->
                            <div class="image-container" v-if="item.images && item.images.length > 0">
                                <van-image v-for="(img, imgIndex) in item.images" :key="imgIndex" width="33%"
                                    fit="cover" :src="img" alt="动态图片" />
                            </div>
                        </div>

                        <!-- 互动栏 -->
                        <div class="interaction-bar">
                            <div class="interaction-item" @click="handleLike(item)">
                                <van-icon :name="item.isLiked ? 'like' : 'like-o'" size="18"
                                    :class="{ 'liked': item.isLiked }" />
                                {{ item.likes }}
                            </div>
                            <div class="interaction-item" @click="handleComment(item)">
                                <van-icon name="chat-o" size="18" /> {{ item.comments }}
                            </div>
                            <div class="interaction-item" @click="handleShare(item)">
                                <van-icon name="share-o" size="18" /> {{ item.shares }}
                            </div>
                        </div>
                    </div>
                </div>
            </van-list>
        </van-pull-refresh>
    </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from '@/utils/vant-ui'
import { useUserStore } from '@/stores'

const router = useRouter()
const userStore = useUserStore()

// 用户头像
const userAvatar = computed(() => {
  return userStore.userInfo.image || '/src/assets/portrait.png'
})

// 搜索值
const searchValue = ref('')

// 数据列表
const dynamicList = ref([])

// 分页相关
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = ref(10)

// 下拉刷新
const refreshing = ref(false)

// 获取动态列表
const fetchDynamicList = async (isLoadMore = false) => {
  if (loading.value || finished.value) return
  
  loading.value = true
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 模拟新的动态数据
    const mockMoreData = [
      {
        id: dynamicList.value.length + 1,
        username: '创业观察',
        avatar: 'https://www.ahchanye.com/wp-content/uploads/2022/03/2022031817395743.png',
        time: '5小时前',
        following: true,
        content: '最新研究显示，90后、00后已成为创业主力军，他们更倾向于在新兴领域寻找机会。',
        news: {
          id: 4,
          title: '新一代年轻人创业图鉴：机遇与挑战并存',
          source: '创业周刊',
            image: 'https://www.ahchanye.com/wp-content/uploads/2022/03/2022031817395743.png'
        },
        images: [],
        likes: 156,
        comments: 28,
        shares: 15,
        isLiked: false
      },
      {
        id: dynamicList.value.length + 2,
        username: '科技达人',
          avatar: 'https://www.ncsti.gov.cn/images/kjrd_rgzn_active.png',
        time: '6小时前',
        following: true,
        content: '发现了一家隐藏在巷子里的米其林餐厅，每道菜都是艺术品！',
        news: null,
        images: [
          'https://pic3.zhimg.com/v2-c900780a9c1f91e247ddd5d7ef4b1609_1200x500.jpg',
          'https://x0.ifengimg.com/ucms/2020_43/13232FA8FBD20C0F070E565D9BD3685F91595630_w1080_h720.jpg'
        ],
        likes: 428,
        comments: 56,
        shares: 32,
        isLiked: false
      }
    ]

    // 模拟API响应
    const res = {
      status: 200,
      data: {
        list: isLoadMore ? mockMoreData : mockMoreData,
        total: 20 // 总数据条数
      }
    }

    if (res.status === 200) {
      const { list, total } = res.data
      
      if (isLoadMore) {
        dynamicList.value.push(...list)
      } else {
        dynamicList.value = list
      }

      // 判断是否加载完所有数据
      if (dynamicList.value.length >= total) {
        finished.value = true
      }
      
      page.value += 1
    }
  } catch (error) {
    console.error('获取动态列表失败', error)
    showToast('加载失败，请重试')
  } finally {
    loading.value = false
  }
}

// 刷新所有数据
const refreshAllData = async () => {
  try {
    // 重置分页
    page.value = 1
    finished.value = false
    await fetchDynamicList()
  } catch (error) {
    console.error('刷新数据失败', error)
    showToast('刷新失败，请重试')
  }
}

// 下拉刷新
const onRefresh = async () => {
  try {
    await refreshAllData()
    showToast('刷新成功')
  } catch (error) {
    console.error('刷新失败', error)
    showToast('刷新失败，请重试')
  } finally {
    refreshing.value = false
  }
}

// 加载更多
const onLoad = () => {
  fetchDynamicList(true)
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/article/${id}`)
}

// 关注用户
const handleFollow = (item) => {
  // TODO: 实现关注功能
  showToast('关注成功')
}

// 点赞
const handleLike = (item) => {
  // TODO: 实现点赞功能
  item.isLiked = !item.isLiked
  item.likes += item.isLiked ? 1 : -1
}

// 评论
const handleComment = (item) => {
  // TODO: 实现评论功能
  showToast('评论功能开发中')
}

// 分享
const handleShare = (item) => {
  // TODO: 实现分享功能
  showToast('分享功能开发中')
}

// 跳转到搜索页
const goToSearch = () => {
  router.push('/search')
}

// 处理新建动态
const handleNewPost = () => {
  router.push('/post')
}

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 页面加载时获取数据
onMounted(async () => {
  await fetchDynamicList()
})
</script>

<style scoped>
.dynamic-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 56px;
  padding-bottom: 50px;
}

.custom-nav {
  border-bottom: 1px solid #f0f0f0;
}

.custom-nav :deep(.van-nav-bar__content) {
  height: 56px;
  padding: 0 4px;
}

.custom-nav :deep(.van-nav-bar__left) {
  flex: 1;
  padding-left: 8px;
  padding-right: 0;
  width: calc(100% - 46px);
}

.custom-nav :deep(.van-nav-bar__right) {
  padding-right: 8px;
  min-width: 40px;
}

.avatar {
  flex-shrink: 0;
}

.avatar-wrapper {
  cursor: pointer;
  flex-shrink: 0;
}

.avatar-search {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 8px;
}

.search-input {
  flex: 1;
  min-width: 160px;
  width: 100%;
}

.search-input :deep(.van-search) {
  padding: 0;
  width: 100%;
}

.search-input :deep(.van-search__content) {
  background-color: #f5f5f5;
}

.search-input :deep(.van-field__left-icon) {
  margin-right: 4px;
}

.notification {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.dynamic-list {
  padding: 12px;
}

.dynamic-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.user-detail {
  flex: 1;
  margin-left: 12px;
}

.username {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.timestamp {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.follow-btn {
  font-size: 12px;
  height: 28px;
  padding: 0 12px;
  color: #fff;
  background-color: #3478F6;
  border: none;
}

.dynamic-content {
  margin-bottom: 16px;
}

.dynamic-text {
  font-size: 15px;
  line-height: 1.5;
  color: #333;
  margin-bottom: 12px;
}

.news-card {
  display: flex;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
  align-items: center;
}

.news-card-content {
  flex: 1;
  margin-right: 12px;
}

.news-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 6px;
  font-weight: 500;
  line-height: 1.4;
}

.news-source {
  font-size: 12px;
  color: #999;
}

.image-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 12px;
}

.interaction-bar {
  display: flex;
  border-top: 1px solid #eee;
  padding-top: 12px;
}

.interaction-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.interaction-item .van-icon {
  transition: all 0.3s;
}

.interaction-item .van-icon.liked {
  color: #ee0a24;
}

.van-loading {
  text-align: center;
  padding: 16px 0;
}

.van-divider {
  margin: 16px 0;
}

.avatar-scroll-container {
  background-color: #fff;
  padding: 12px 0;
  margin-bottom: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.avatar-scroll-wrapper {
  display: flex;
  overflow-x: auto;
  padding: 0 12px;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none; /* Firefox */
}

.avatar-scroll-wrapper::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Edge */
}

.avatar-item {
  flex: 0 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-right: 20px;
  width: 60px;
}

.avatar-name {
  margin-top: 8px;
  font-size: 12px;
  color: #333;
  text-align: center;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
