<template>
  <div class="follows-container">
    <!-- 顶部导航栏 -->
    <van-nav-bar
      left-arrow
      @click-left="onBack"
      fixed
      left-arrow-color="#3478F6"
    />

    <!-- Tab切换 -->
    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="关注">
        <van-pull-refresh v-model="followsRefreshing" @refresh="onRefresh">
          <van-list
            v-model:loading="followsLoading"
            :finished="followsFinished"
            finished-text="没有更多了"
            @load="onFollowsLoad"
          >
            <div class="user-list">
              <div class="user-item" v-for="(user, index) in followsList" :key="index">
                <div class="user-info">
                  <van-image
                    round
                    width="50"
                    height="50"
                    :src="user.avatar"
                    alt="用户头像"
                  />
                  <div class="user-detail">
                    <div class="username">{{ user.username }}</div>
                    <div class="bio">{{ user.bio }}</div>
                  </div>
                </div>
                <van-button 
                  round 
                  size="small" 
                  :type="user.isFollowing ? 'default' : 'primary'"
                  @click="handleFollow(user)"
                >
                  {{ user.isFollowing ? (user.isMutual ? '互相关注' : '已关注') : '关注' }}
                </van-button>
              </div>
            </div>
          </van-list>
        </van-pull-refresh>
      </van-tab>

      <van-tab title="粉丝">
        <van-pull-refresh v-model="fansRefreshing" @refresh="onRefresh">
          <van-list
            v-model:loading="fansLoading"
            :finished="fansFinished"
            finished-text="没有更多了"
            @load="onFansLoad"
          >
            <div class="user-list">
              <div class="user-item" v-for="(user, index) in fansList" :key="index">
                <div class="user-info">
                  <van-image
                    round
                    width="50"
                    height="50"
                    :src="user.avatar"
                    alt="用户头像"
                  />
                  <div class="user-detail">
                    <div class="username">{{ user.username }}</div>
                    <div class="bio">{{ user.bio }}</div>
                  </div>
                </div>
                <van-button 
                  round 
                  size="small" 
                  :type="user.isFollowing ? 'default' : 'primary'"
                  @click="handleFollow(user)"
                >
                  {{ user.isFollowing ? (user.isMutual ? '互相关注' : '已关注') : '关注' }}
                </van-button>
              </div>
            </div>
          </van-list>
        </van-pull-refresh>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showSuccessToast } from '@/utils/vant-ui'

const router = useRouter()
const route = useRoute()
const activeTab = ref(0)

// 关注列表相关
const followsLoading = ref(false)
const followsFinished = ref(false)
const followsRefreshing = ref(false)
const followsList = ref([
  {
    id: 1,
    username: '科技前沿',
    avatar: 'https://img01.yzcdn.cn/vant/cat.jpeg',
    bio: '关注科技发展，分享前沿资讯',
    isFollowing: true,
    isMutual: true
  },
  {
    id: 2,
    username: '财经观察',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    bio: '专注财经领域，解读市场动向',
    isFollowing: true,
    isMutual: false
  }
])

// 粉丝列表相关
const fansLoading = ref(false)
const fansFinished = ref(false)
const fansRefreshing = ref(false)
const fansList = ref([
  {
    id: 3,
    username: '创业者联盟',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-1.jpeg',
    bio: '分享创业经验，交流创业心得',
    isFollowing: true,
    isMutual: true
  },
  {
    id: 4,
    username: '科技达人',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-2.jpeg',
    bio: '热爱科技，分享生活',
    isFollowing: false,
    isMutual: false
  }
])

onMounted(() => {
  // 根据路由参数设置初始tab
  const tab = Number(route.query.tab) || 0
  activeTab.value = tab
})

// 返回上一页
const onBack = () => {
  router.back()
}

// 处理关注/取消关注
const handleFollow = (user) => {
  if (user.isFollowing) {
    // 取消关注
    user.isFollowing = false
    user.isMutual = false
    showToast('已取消关注')
  } else {
    // 关注
    user.isFollowing = true
    // 如果在粉丝列表中，则设置为互相关注
    if (activeTab.value === 1) {
      user.isMutual = true
      showSuccessToast('已互相关注')
    } else {
      showSuccessToast('关注成功')
    }
  }
}

// 加载关注列表
const onFollowsLoad = async () => {
  if (followsLoading.value) return
  followsLoading.value = true
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    // 模拟加载更多数据
    const moreData = [
      {
        id: 5,
        username: '新媒体运营',
        avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-3.jpeg',
        bio: '专注新媒体运营与内容创作',
        isFollowing: true,
        isMutual: false
      }
    ]
    followsList.value.push(...moreData)
    followsFinished.value = true
  } catch (error) {
    showToast('加载失败')
  } finally {
    followsLoading.value = false
  }
}

// 加载粉丝列表
const onFansLoad = async () => {
  if (fansLoading.value) return
  fansLoading.value = true
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    // 模拟加载更多数据
    const moreData = [
      {
        id: 6,
        username: '互联网爱好者',
        avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/apple-4.jpeg',
        bio: '关注互联网发展，分享行业见解',
        isFollowing: false,
        isMutual: false
      }
    ]
    fansList.value.push(...moreData)
    fansFinished.value = true
  } catch (error) {
    showToast('加载失败')
  } finally {
    fansLoading.value = false
  }
}

// 刷新
const onRefresh = async () => {
  try {
    if (activeTab.value === 0) {
      followsFinished.value = false
      await onFollowsLoad()
      followsRefreshing.value = false
    } else {
      fansFinished.value = false
      await onFansLoad()
      fansRefreshing.value = false
    }
    showToast('刷新成功')
  } catch (error) {
    showToast('刷新失败')
    followsRefreshing.value = false
    fansRefreshing.value = false
  }
}
</script>

<style scoped>
.follows-container {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
}

:deep(.van-tabs__wrap) {
  padding: 0;
}

:deep(.van-tabs__nav) {
  display: flex;
  justify-content: space-between;
  padding: 0 25%;
}

:deep(.van-tab) {
  flex: none !important;
  padding: 0 !important;
  width: 110px;
}

:deep(.van-tabs__line) {
  width: 24px !important;
  background-color: #3478F6;
}

.user-list {
  padding: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
  margin-right: 12px;
}

.user-detail {
  margin-left: 12px;
  flex: 1;
}

.username {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.bio {
  font-size: 13px;
  color: #999;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}

:deep(.van-button) {
  width: 72px;
  height: 32px;
}

:deep(.van-button--default) {
  color: #666;
  background: #f5f5f5;
  border: none;
}
</style> 