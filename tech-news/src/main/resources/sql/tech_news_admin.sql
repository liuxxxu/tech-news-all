/*
 Navicat Premium Dump SQL

 Source Server         : 毕设
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3307
 Source Schema         : tech_news_admin

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 09/04/2025 09:19:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ad_channel
-- ----------------------------
DROP TABLE IF EXISTS `ad_channel`;
CREATE TABLE `ad_channel`  (
  `id` bigint NOT NULL,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '频道名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '频道描述',
  `status` tinyint UNSIGNED NULL DEFAULT NULL,
  `ord` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '默认排序',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '频道信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_channel_label
-- ----------------------------
DROP TABLE IF EXISTS `ad_channel_label`;
CREATE TABLE `ad_channel_label`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `channel_id` int UNSIGNED NULL DEFAULT NULL,
  `label_id` int UNSIGNED NULL DEFAULT NULL COMMENT '标签ID',
  `ord` int UNSIGNED NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1118 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '频道标签信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_label
-- ----------------------------
DROP TABLE IF EXISTS `ad_label`;
CREATE TABLE `ad_label`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '频道名称',
  `type` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '0系统增加\r\n            1人工增加',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24237 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '标签信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_permission
-- ----------------------------
DROP TABLE IF EXISTS `ad_permission`;
CREATE TABLE `ad_permission`  (
  `id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限描述',
  `is_enable` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否有效',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_role
-- ----------------------------
DROP TABLE IF EXISTS `ad_role`;
CREATE TABLE `ad_role`  (
  `id` bigint NOT NULL,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `is_enable` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '是否有效',
  `created_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `ad_role_permission`;
CREATE TABLE `ad_role_permission`  (
  `id` bigint NOT NULL,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `created_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色与权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_user
-- ----------------------------
DROP TABLE IF EXISTS `ad_user`;
CREATE TABLE `ad_user`  (
  `id` bigint NOT NULL COMMENT '主键',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `salt` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码、通信等加密盐',
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码,md5加密',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
  `sex` tinyint(1) NULL DEFAULT 2 COMMENT '0 男\r\n            1 女\r\n            2 保密',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '简介',
  `status` tinyint UNSIGNED NULL DEFAULT NULL COMMENT '0正常\r\n            1锁定',
  `login_time` datetime(1) NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `updated_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Admin用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_user_login
-- ----------------------------
DROP TABLE IF EXISTS `ad_user_login`;
CREATE TABLE `ad_user_login`  (
  `id` int UNSIGNED NOT NULL,
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `equipment_id` int UNSIGNED NULL DEFAULT NULL COMMENT '登录设备ID',
  `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录IP',
  `address` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录地址',
  `longitude` decimal(5, 5) NULL DEFAULT NULL COMMENT '经度',
  `latitude` decimal(5, 5) NULL DEFAULT NULL COMMENT '纬度',
  `created_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员登录行为信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_user_operation
-- ----------------------------
DROP TABLE IF EXISTS `ad_user_operation`;
CREATE TABLE `ad_user_operation`  (
  `id` int UNSIGNED NOT NULL,
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `equipment_id` int UNSIGNED NULL DEFAULT NULL COMMENT '登录设备ID',
  `ip` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录IP',
  `address` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录地址',
  `type` int NULL DEFAULT NULL COMMENT '操作类型',
  `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作描述',
  `created_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员操作行为信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for ad_user_role
-- ----------------------------
DROP TABLE IF EXISTS `ad_user_role`;
CREATE TABLE `ad_user_role`  (
  `id` bigint NOT NULL,
  `role_id` int UNSIGNED NULL DEFAULT NULL COMMENT '角色ID',
  `user_id` int UNSIGNED NULL DEFAULT NULL COMMENT '用户ID',
  `created_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员角色信息表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
