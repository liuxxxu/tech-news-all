package com.liuxu.behavior.controller;

import com.liuxu.behavior.service.AppArticleBehaviorService;
import com.liuxu.model.behavior.dtos.ArticleBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/article/load_article_behavior")
public class AppArticleBehaviorController {

    @Autowired
    private AppArticleBehaviorService appArticleBehaviorService;

    @PostMapping
    public ResponseResult loadArticleBehavior(@RequestParam @Validated ArticleBehaviorDTO dto) {
        return appArticleBehaviorService.loadArticleBehavior(dto);
    }

}
