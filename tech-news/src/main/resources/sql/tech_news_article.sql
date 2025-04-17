/*
 Navicat Premium Dump SQL

 Source Server         : 毕设
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3307
 Source Schema         : tech_news_article

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 09/04/2025 09:19:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for app_article
-- ----------------------------
DROP TABLE IF EXISTS `app_article`;
CREATE TABLE `app_article`  (
  `id` bigint NOT NULL,
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `author_id` bigint NULL DEFAULT NULL COMMENT '文章作者的ID',
  `author_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作者昵称',
  `channel_id` bigint NULL DEFAULT NULL COMMENT '文章所属频道ID',
  `channel_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '频道名称',
  `layout` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '封面类型 0 无 1 有',
  `cover` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图片',
  `flag` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '文章标记\n            0 普通文章 1 置顶文章',
  `labels` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文章标签最多3个 逗号分隔',
  `likes` int UNSIGNED NULL DEFAULT NULL COMMENT '点赞数量',
  `collection` int UNSIGNED NULL DEFAULT NULL COMMENT '收藏数量',
  `comment` int UNSIGNED NULL DEFAULT NULL COMMENT '评论数量',
  `views` int UNSIGNED NULL DEFAULT NULL COMMENT '阅读数量',
  `province_id` int UNSIGNED NULL DEFAULT NULL COMMENT '省市',
  `city_id` int UNSIGNED NULL DEFAULT NULL COMMENT '市区',
  `county_id` int UNSIGNED NULL DEFAULT NULL COMMENT '区县',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `sync_status` tinyint(1) NULL DEFAULT 0 COMMENT '同步状态',
  `origin` tinyint UNSIGNED NULL DEFAULT 0 COMMENT '来源',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文章信息表，存储已发布的文章' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for app_article_config
-- ----------------------------
DROP TABLE IF EXISTS `app_article_config`;
CREATE TABLE `app_article_config`  (
  `id` bigint NOT NULL COMMENT '主键',
  `article_id` bigint NULL DEFAULT NULL COMMENT '文章ID',
  `is_comment` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否可评论',
  `is_forward` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否转发',
  `is_down` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否下架',
  `is_delete` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP已发布文章配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for app_article_content
-- ----------------------------
DROP TABLE IF EXISTS `app_article_content`;
CREATE TABLE `app_article_content`  (
  `id` bigint NOT NULL COMMENT '主键',
  `article_id` bigint NULL DEFAULT NULL COMMENT '文章ID',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '文章内容',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_id`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP已发布文章内容表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for app_author
-- ----------------------------
DROP TABLE IF EXISTS `app_author`;
CREATE TABLE `app_author`  (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '作者名称',
  `type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '0 爬取数据\r\n            1 签约合作商\r\n            2 平台自媒体人\r\n            ',
  `user_id` bigint NULL DEFAULT NULL COMMENT '社交账号ID',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `wm_user_id` bigint NULL DEFAULT NULL COMMENT '自媒体账号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_type_name`(`type` ASC, `name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = 'APP文章作者信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for app_collection
-- ----------------------------
DROP TABLE IF EXISTS `app_collection`;
CREATE TABLE `app_collection`  (
  `id` bigint NOT NULL,
  `article_id` bigint UNSIGNED NULL DEFAULT NULL COMMENT '文章ID',
  `type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '点赞内容类型\r\n            0文章\r\n            1动态',
  `collection_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `published_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_type`(`article_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'APP收藏信息表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
