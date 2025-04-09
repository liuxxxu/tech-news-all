package com.liuxu.behavior.controller;

import com.liuxu.behavior.service.AppLikesBehaviorService;
import com.liuxu.model.behavior.dtos.LikesBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/likes_behavior")
public class AppLikesBehaviorController {
    @Autowired
    private AppLikesBehaviorService appLikesBehaviorService;

    /**
     * 点赞或取消点赞
     * 
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult like(@RequestBody @Validated LikesBehaviorDTO dto) {
        return appLikesBehaviorService.like(dto);
    }
}
