package com.liuxu.datasync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuxu.model.article.pojos.AppArticle;

import java.util.List;

public interface ApArticleMapper extends BaseMapper<AppArticle> {
    List<AppArticle> findAllArticles();
}
