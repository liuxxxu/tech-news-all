<template>
  <div class="data-panel">
    <a-row :gutter="16">
      <a-col :span="6">
        <div class="stat-card blue">
          <div class="icon-wrapper">
            <FileTextOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">326</div>
            <div class="stat-title">发布文章</div>
          </div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="stat-card green">
          <div class="icon-wrapper">
            <EyeOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">45,639</div>
            <div class="stat-title">阅读量</div>
          </div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="stat-card purple">
          <div class="icon-wrapper">
            <CommentOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">2,562</div>
            <div class="stat-title">评论量</div>
          </div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="stat-card yellow">
          <div class="icon-wrapper">
            <LikeOutlined />
          </div>
          <div class="stat-content">
            <div class="stat-value">5,429</div>
            <div class="stat-title">点赞量</div>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { FileTextOutlined, EyeOutlined, CommentOutlined, LikeOutlined } from '@ant-design/icons-vue'
</script>

<style lang="scss" scoped>
.data-panel {
  margin-bottom: 24px;

  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    .icon-wrapper {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 48px;
      height: 48px;
      border-radius: 50%;
      margin-right: 16px;
      font-size: 24px;
    }

    .stat-content {
      .stat-value {
        font-size: 24px;
        font-weight: 600;
        line-height: 1.2;
        margin-bottom: 4px;
      }

      .stat-title {
        font-size: 14px;
        color: rgba(0, 0, 0, 0.45);
      }
    }

    &.blue {
      .icon-wrapper {
        background-color: rgba(24, 144, 255, 0.1);
        color: #1890ff;
      }
      .stat-value {
        color: #1890ff;
      }
    }

    &.green {
      .icon-wrapper {
        background-color: rgba(82, 196, 26, 0.1);
        color: #52c41a;
      }
      .stat-value {
        color: #52c41a;
      }
    }

    &.yellow {
      .icon-wrapper {
        background-color: rgba(250, 173, 20, 0.1);
        color: #faad14;
      }
      .stat-value {
        color: #faad14;
      }
    }

    &.purple {
      .icon-wrapper {
        background-color: rgba(114, 46, 209, 0.1);
        color: #722ed1;
      }
      .stat-value {
        color: #722ed1;
      }
    }
  }
}
</style> 