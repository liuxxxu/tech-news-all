package com.liuxu.search.service.impl;

import com.liuxu.common.constants.search.SearchConstants;
import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.es.service.EsService;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.model.search.vos.SearchArticleVO;
import com.liuxu.search.service.AppUserSearchService;
import com.liuxu.search.service.ArticleSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class ArticleSearchServiceImpl implements ArticleSearchService {
    @Autowired
    private EsService esService;

    @Autowired
    private AppUserSearchService appUserSearchService;

    /**
     * ES文章分页搜索
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult search(UserSearchDTO dto) {
        // 1.校验参数 （ 关键字 ）
        String searchWords = dto.getSearchWords();
        if (StringUtils.isBlank(searchWords)) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "搜素条件内容不能为空");
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        // 记录搜索历史记录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId != null) {
            dto.setEntryId(userId);
        }

        // 异步调用保存用户输入关键词记录
        appUserSearchService.insert(dto);

        // 2. 构建搜索请求对象 SearchSourceBuilder
        SearchSourceBuilder builder = new SearchSourceBuilder();

        // 2.1 构建查询条件 builder.query
        // 2.1.1 创建布尔条件 bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 2.1.2 创建分词查询条件 match查询title ===》 must 加入bool
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", searchWords));

        // 2.1.3 创建范围查询条件 range 查询 publishTime ==> filter 加入 bool
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime()));
        builder.query(boolQueryBuilder);

        // 2.2 设置高亮字段 builder.highlight
        HighlightBuilder highlightBuilder = new HighlightBuilder();

        // 2.2.1 哪个字段高亮
        highlightBuilder.field("title");

        // 2.2.2 前置标签
        highlightBuilder.field("<span>");
        // 2.2.2 后置标签
        highlightBuilder.field("</span>");

        builder.highlighter(highlightBuilder);

        // 2.3 构建排序条件 builder.sort 发布时间排序 （默认： score）
        builder.sort("publishTime", SortOrder.DESC);

        // 2.4 构建分页条件 builder.from(0) builder.size(pageSize)
        builder.size(dto.getPageSize());
        builder.from(dto.getFromIndex());

        // 3. 执行搜素，并封装返回结果
        PageResponseResult result = esService.search(builder, SearchArticleVO.class,
                SearchConstants.ARTICLE_INDEX_NAME);
        return result;
    }

    /**
     * 添加索引文章
     * 
     * @param article
     */
    @Override
    public void saveArticle(SearchArticleVO article) {
        esService.save(article, SearchConstants.ARTICLE_INDEX_NAME);
    }

    /**
     * 删除索引文章
     * 
     * @param articleId
     */
    @Override
    public void deleteArticle(String articleId) {
        esService.deleteById(articleId, SearchConstants.ARTICLE_INDEX_NAME);
    }
}
