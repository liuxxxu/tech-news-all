package com.liuxu.search.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.search.service.AppAssociateWordsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 联想词表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/associate")
public class AppAssociateWordsController {
    @Autowired
    private AppAssociateWordsService appAssociateWordsService;

    @PostMapping("/search")
    public ResponseResult findAssociate(@RequestBody UserSearchDTO userSearchDto) {
        return appAssociateWordsService.findAssociate(userSearchDto);
    }
}