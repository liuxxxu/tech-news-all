package com.liuxu.wemedia.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.wemedia.pojos.WmUser;
import com.liuxu.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class WmUserController {
    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/save")
    public ResponseResult<WmUser> save(@RequestBody @Validated WmUser wmUser) {
        wmUserService.save(wmUser);
        return ResponseResult.successResult(wmUser);
    }

    @GetMapping("/findByName/{name}")
    public ResponseResult<WmUser> findByName(@PathVariable("name") String name) {
        return wmUserService.findByName(name);
    }

    @GetMapping("/findByPhone/{phone}")
    public ResponseResult<WmUser> findByPhone(@PathVariable("phone") String phone) {
        return wmUserService.findByPhone(phone);
    }

}
