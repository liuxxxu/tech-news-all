package com.liuxu.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.model.article.dtos.ArticleHomeDTO;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.common.dtos.ResponseResult;

import java.util.List;

public interface AppArticleService extends IService<AppArticle> {
    /**
     * 保存或修改文章
     *
     * @param newsId 文章id
     * @return
     */
    void publishArticle(Long newsId);

    /**
     * 根据参数加载文章列表
     *
     * @param loadType 1为加载更多 2为加载最新
     * @param dto
     * @return
     */
    ResponseResult<List<AppArticle>> load(Short loadType, ArticleHomeDTO dto);

    /**
     * 加载文章列表
     *
     * @param dto
     * @param loadType  1 加载更多 2 加载最新
     * @param firstPage true 是首页 false 非首页
     * @return
     */
    ResponseResult<List<AppArticle>> loadNew(Short loadType, ArticleHomeDTO dto, boolean firstPage);
}
