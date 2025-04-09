package com.liuxu.datasync.controller;

import com.liuxu.datasync.service.EsDataService;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("esdata")
public class DataSyncController {
    @Autowired
    private EsDataService esDataService;

    @GetMapping("init")
    public ResponseResult esDataInit(){
        return esDataService.dataInit();
    }
}