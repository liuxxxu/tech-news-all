package com.liuxu.article.controller;

import com.liuxu.article.service.AppArticleService;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.vos.SearchArticleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article")
public class AppArticleController {
    @Autowired
    private AppArticleService appArticleService;

    @GetMapping("{id}")
    public ResponseResult<SearchArticleVO> findArticle(@PathVariable Long id) {
        SearchArticleVO searchArticleVo = null;
        AppArticle article = appArticleService.getById(id);
        if (article != null) {
            searchArticleVo = new SearchArticleVO();
            BeanUtils.copyProperties(article, searchArticleVo);
        }
        return ResponseResult.successResult(searchArticleVo);
    }
}