package com.liuxu.article.service;

import com.liuxu.model.message.app.AggBehaviorDTO;

/**
 * <p>
 * 热文章表 服务类
 * </p>
 *
 */
public interface HotArticleService {
    /**
     * 计算热文章
     */
    public void computeHotArticle();

    /**
     * 更新文章行为数量
     * 
     * @param aggBehavior
     */
    public void updateAppArticle(AggBehaviorDTO aggBehavior);
}