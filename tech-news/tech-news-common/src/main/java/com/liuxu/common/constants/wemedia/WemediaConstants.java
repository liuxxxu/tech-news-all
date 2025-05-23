package com.liuxu.common.constants.wemedia;

public class WemediaConstants {
    // 是否收藏
    public static final Short COLLECT_MATERIAL = 1;//收藏
    public static final Short CANCEL_COLLECT_MATERIAL = 0;//取消收藏
    // 文章类型
    public static final String WM_NEWS_TYPE_IMAGE = "image";
    // 文章状态
    public static final Short WM_NEWS_DRAFT_STATUS = 0; //草稿
    public static final Short WM_NEWS_SUMMIT_STATUS = 1; //提交
    public static final Short WM_NEWS_AUTHED_STATUS = 8; //审核通过
    public static final Short WM_NEWS_PUBLISH_STATUS = 9; //已发布
    // 文章封面选图
    public static final Short WM_NEWS_NO_COVER = 0; //无图
    public static final Short WM_NEWS_HAVE_COVER = 1; //单图
    // 文章图片引用
    public static final Short WM_CONTENT_REFERENCE = 0;
    public static final Short WM_IMAGE_REFERENCE = 1;
    // 文章上下架状态
    public static final Short WM_NEWS_UP = 1; // 上架
    public static final Short WM_NEWS_DOWN = 0; // 下架

    public static final Short WM_NEWS_AUTH_PASS = 4;
    public static final Short WM_NEWS_AUTH_FAIL = 2;
}
