package com.liuxu.search.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.dtos.HistorySearchDTO;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.search.service.AppUserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APP用户搜索信息表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/history")
public class AppUserSearchController {
    @Autowired
    private AppUserSearchService appUserSearchService;

    @PostMapping("/load")
    public ResponseResult findUserSearch(@RequestBody UserSearchDTO userSearchDto) {
        return appUserSearchService.findUserSearch(userSearchDto);
    }

    @PostMapping("/del")
    public ResponseResult delUserSearch(@RequestBody HistorySearchDTO dto) {
        return appUserSearchService.delUserSearch(dto);
    }
}