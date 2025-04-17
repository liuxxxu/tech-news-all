package com.liuxu.admin.controller;

import com.liuxu.admin.service.AdUserService;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.admin.dtos.AdUserDTO;
import com.liuxu.model.admin.vo.AdUserVO;
import com.liuxu.model.user.vos.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/login")
@RestController
public class LoginController {

    @Autowired
    private AdUserService adUserService;


    @PostMapping("/in")
    public ResponseResult<LoginVO<AdUserVO>> login(@RequestBody AdUserDTO adUserDTO) {
        return ResponseResult.successResult(adUserService.login(adUserDTO));
    }

    @PostMapping("/out")
    public ResponseResult<Boolean> logout() {
        boolean logout = adUserService.logout();
        return ResponseResult.successResult(logout);
    }

}
