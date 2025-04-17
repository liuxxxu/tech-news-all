package com.liuxu.behavior.controller;

import com.liuxu.behavior.service.AppUnlikeBehaviorService;
import com.liuxu.model.behavior.dtos.UnLikesBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/un_likes_behavior/")
public class AppUnlikeBehaviorController {

    @Autowired
    private AppUnlikeBehaviorService appUnlikeBehaviorService;

    @PostMapping
    public ResponseResult unlikeBehavior(@RequestBody @Validated UnLikesBehaviorDTO dto) {
        return appUnlikeBehaviorService.unlikeBehavior(dto);
    }

}
