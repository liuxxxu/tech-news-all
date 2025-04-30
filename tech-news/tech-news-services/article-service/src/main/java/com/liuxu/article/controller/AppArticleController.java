package com.liuxu.article.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liuxu.article.service.AppArticleContentService;
import com.liuxu.article.service.AppArticleService;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.model.article.pojos.AppArticleContent;
import com.liuxu.model.search.vos.ArticleVO;
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
    @Autowired
    private AppArticleContentService appArticleContentService;

    @GetMapping("{id}")
    public ResponseResult<ArticleVO> findArticle(@PathVariable Long id) {
        ArticleVO articleVO = null;
        AppArticle article = appArticleService.getById(id);
        if (article != null) {
            articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);
            LambdaQueryWrapper<AppArticleContent> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AppArticleContent::getArticleId, id);
            AppArticleContent articleContent = appArticleContentService.getOne(wrapper);
            if (articleContent != null) {
                articleVO.setContent(articleContent.getContent());
                articleVO.setSummary(articleContent.getSummary());
            }
        }
        return ResponseResult.successResult(articleVO);
    }
}