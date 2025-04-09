package com.liuxu.behavior.controller;

import com.liuxu.behavior.service.AppCollectionBehaviorService;
import com.liuxu.model.behavior.dtos.CollectionBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collection_behavior/")
public class AppCollectionBehaviorController {

    @Autowired
    private AppCollectionBehaviorService appCollectionBehaviorService;

    @PostMapping
    public ResponseResult collectBehavior(@RequestBody @Validated CollectionBehaviorDTO dto) {
        return appCollectionBehaviorService.collectBehavior(dto);
    }

}
