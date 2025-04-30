package com.liuxu.search.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.search.service.ArticleSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class ArticleSearchController{

    @Autowired
    private ArticleSearchService articleSearchService;

    @PostMapping("/article")
    public ResponseResult search(@RequestBody UserSearchDTO userSearchDto) {
        // 文章搜索接口
        return articleSearchService.search(userSearchDto);
    }
}