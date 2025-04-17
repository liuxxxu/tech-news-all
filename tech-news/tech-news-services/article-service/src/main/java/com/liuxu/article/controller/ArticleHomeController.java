package com.liuxu.article.controller;

import com.liuxu.article.service.AppArticleService;
import com.liuxu.common.constants.article.ArticleConstants;
import com.liuxu.model.article.dtos.ArticleHomeDTO;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/article")
public class ArticleHomeController {

    @Autowired
    private AppArticleService articleService;

    @PostMapping("/load")
    public ResponseResult<List<AppArticle>> load(@RequestBody ArticleHomeDTO dto) {
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE, dto);
    }

    @PostMapping("/loadmore")
    public ResponseResult<List<AppArticle>> loadMore(@RequestBody ArticleHomeDTO dto) {
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE, dto);
    }

    @PostMapping("/loadnew")
    public ResponseResult<List<AppArticle>> loadNew(@RequestBody ArticleHomeDTO dto) {
        // return articleService.load(ArticleConstants.LOADTYPE_LOAD_NEW,dto);
        return articleService.loadNew(ArticleConstants.LOADTYPE_LOAD_NEW, dto, true);
    }
}
