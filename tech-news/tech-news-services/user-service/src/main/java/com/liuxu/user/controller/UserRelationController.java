package com.liuxu.user.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.user.dtos.UserRelationDTO;
import com.liuxu.user.service.AppUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserRelationController {
    @Autowired
    private AppUserRelationService appUserRelationService;

    @PostMapping("/user_follow")
    public ResponseResult<Boolean> follow(@RequestBody UserRelationDTO dto) {
        boolean result = appUserRelationService.follow(dto);
        return ResponseResult.successResult(result);
    }
}
