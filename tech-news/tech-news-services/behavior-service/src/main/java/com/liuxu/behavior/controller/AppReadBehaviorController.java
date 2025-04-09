package com.liuxu.behavior.controller;

import com.liuxu.behavior.service.AppReadBehaviorService;
import com.liuxu.model.behavior.dtos.ReadBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/read_behavior")
public class AppReadBehaviorController {

    @Autowired
    private AppReadBehaviorService appReadBehaviorService;

    @PostMapping
    public ResponseResult readBehavior(@RequestBody @Validated ReadBehaviorDTO dto) {
        return appReadBehaviorService.readBehavior(dto);
    }

}
