package com.liuxu.datasync.service.impl;

import com.liuxu.common.constants.search.SearchConstants;
import com.liuxu.datasync.mapper.ApArticleMapper;
import com.liuxu.datasync.service.EsDataService;
import com.liuxu.es.service.EsService;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.vos.SearchArticleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EsDataServiceImpl implements EsDataService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private EsService<SearchArticleVO> esService;

    @Override
    public ResponseResult dataInit() {
//        1. 判断当前 指定的索引库是否存在
        boolean exist = esService.existIndex(SearchConstants.ARTICLE_INDEX_NAME);

//        2. 如果存在 直接删除对应的索引库
        if (exist) {
            esService.deleteIndex(SearchConstants.ARTICLE_INDEX_NAME);
        }

//        3. 新建索引库
        esService.createIndex(SearchConstants.ARTICLE_INDEX_MAPPING, SearchConstants.ARTICLE_INDEX_NAME);

//        4. 从数据库中 查询文章数据
        List<AppArticle> allArticles = apArticleMapper.findAllArticles();

        List<SearchArticleVO> articleVOList = allArticles.stream().map(apArticle -> {
            SearchArticleVO searchArticleVO = new SearchArticleVO();
            BeanUtils.copyProperties(apArticle, searchArticleVO);
            return searchArticleVO;
        }).collect(Collectors.toList());

//        5. 批量的保存到es索引库中
        esService.saveBatch(articleVOList, SearchConstants.ARTICLE_INDEX_NAME);

        return ResponseResult.successResult();
    }

}
