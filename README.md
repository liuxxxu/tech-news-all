# 科技新闻平台 (Tech News Platform)

## 项目简介

科技新闻平台是一个专注于科技领域的资讯聚合平台，为用户提供高质量的科技资讯内容。平台支持用户浏览、互动、创作和管理，打造了一个完整的科技资讯生态系统。

## 核心功能

### 用户端 (移动端)
- 资讯浏览：支持分类浏览、搜索、推荐等功能
- 社交互动：点赞、收藏、评论、关注等社交功能
- 个人中心：用户信息管理、收藏管理、关注管理
- 自媒体认证：支持用户申请成为自媒体作者

### 自媒体端
- 内容管理：发布、编辑、删除资讯内容
- 素材管理：上传、管理图片等素材
- 评论管理：查看、回复、管理文章评论
- 数据统计：查看文章阅读量、评论数等数据

### 管理端
- 用户管理：用户审核、权限管理
- 内容管理：资讯审核、敏感词管理
- 渠道管理：资讯分类、标签管理
- 系统监控：数据统计、系统监控

## 技术架构

### 后端架构
采用微服务架构，主要包含以下服务：

- 网关服务 (Gateway)
  - 管理端网关 (admin-gateway)
  - 移动端网关 (app-gateway)
  - 自媒体端网关 (wemedia-gateway)

- 核心服务
  - 用户服务 (user-service)
  - 文章服务 (article-service)
  - 行为服务 (behavior-service)
  - 评论服务 (comment-service)
  - 搜索服务 (search-service)
  - 数据同步服务 (data-sync-service)
  - 自媒体服务 (wemedia-service)
  - 管理服务 (admin-service)

- 公共组件
  - 通用模块 (tech-news-common)
  - 远程调用模块 (tech-news-feign)
  - 数据模型模块 (tech-news-model)
  - 启动器模块 (tech-news-starter)

### 前端架构
采用前后端分离架构，分为三个独立项目：

- 移动端 (tech-news-app)
  - 基于 Vue3 + Vant UI
  - 响应式设计，支持移动端适配

- 自媒体端 (tech-news-wemedia)
  - 基于 Vue3 + Ant Design Vue
  - 专注于内容创作和管理
  - 提供丰富的编辑功能

- 管理端 (tech-news-admin)
  - 基于 Vue3 + Ant Design Vue
  - 提供完整的后台管理功能
  - 支持数据统计和系统监控

## 技术栈

### 后端技术
- Spring Cloud Alibaba
- Spring Boot
- MyBatis Plus
- MySQL
- Redis
- Elasticsearch
- RabbitMQ
- MinIO
- XXL-JOB
- MongoDB

### 前端技术
- Vue 3
- Vite
- Pinia
- Vue Router
- Ant Design Vue
- Vant UI
- Axios

## 项目特点

1. 微服务架构：采用 Spring Cloud Alibaba 实现服务治理
2. 高可用设计：服务注册发现、负载均衡、熔断降级
3. 高性能：Redis 缓存、Elasticsearch 搜索、消息队列
4. 安全性：JWT 认证、权限控制、AI审核
5. 可扩展性：模块化设计、服务解耦、接口标准化

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8+
- Redis 6.0+
- Elasticsearch 7.6+
- RabbitMQ 3.8+

### 开发指南
1. 克隆项目
```bash
git clone https://github.com/liuxxxu/tech-news-all.git
```

2. 后端服务启动
```bash

# 启动网关服务
cd tech-news/tech-news-gateways
mvn spring-boot:run

# 启动其他微服务
cd tech-news/tech-news-services
mvn spring-boot:run
```

3. 前端项目启动
```bash
# 移动端
cd tech-news-app
pnpm install
pnpm run dev

# 自媒体端
cd tech-news-wemedia
pnpm install
pnpm run dev

# 管理端
cd tech-news-admin
pnpm install
pnpm run dev
```

## 项目结构
```
tech-news-all/
├── tech-news/                    # 后端项目
│   ├── tech-news-common/         # 通用模块
│   ├── tech-news-feign/          # 远程调用模块
│   ├── tech-news-model/          # 数据模型模块
│   ├── tech-news-starter/        # 启动器模块
│   ├── tech-news-gateways/       # 网关服务
│   └── tech-news-services/       # 微服务
├── tech-news-app/                # 移动端
├── tech-news-wemedia/            # 自媒体端
└── tech-news-admin/              # 管理端
```