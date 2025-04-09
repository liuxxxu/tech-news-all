package com.liuxu.feigns;

import com.liuxu.config.FeignAutoConfiguration;
import com.liuxu.feigns.fallback.ArticleFeignFallback;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.vos.SearchArticleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "tech-news-article", fallbackFactory = ArticleFeignFallback.class, configuration = FeignAutoConfiguration.class)
public interface ArticleFeign {
    @GetMapping("/api/author/findByUserId/{userId}")
    ResponseResult<AppAuthor> findByUserId(@PathVariable("userId") Long userId);

    @PostMapping("/api/author/save")
    ResponseResult<Boolean> save(@RequestBody AppAuthor appAuthor);

    @GetMapping("/api/article/{id}")
    ResponseResult<SearchArticleVO> findArticle(@PathVariable("id") Long id);
}
