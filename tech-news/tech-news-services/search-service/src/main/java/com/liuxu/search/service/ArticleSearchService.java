package com.liuxu.search.service;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.model.search.vos.SearchArticleVO;

public interface ArticleSearchService {
    /**
     ES文章分页搜索
     @return
     */
    ResponseResult search(UserSearchDTO userSearchDto);

    /**
     * 添加索引文章
     * @param article
     */
    void saveArticle(SearchArticleVO article);

    /**
     * 删除索引文章
     * @param articleId
     */
    void deleteArticle(String articleId);
}
