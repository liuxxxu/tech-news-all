package com.liuxu.wemedia.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.user.vos.LoginVO;
import com.liuxu.model.wemedia.dtos.WmUserDTO;
import com.liuxu.model.wemedia.vos.WmUserVO;
import com.liuxu.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {
    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/in")
    public ResponseResult<LoginVO<WmUserVO>> login(@RequestBody WmUserDTO dto){
        LoginVO<WmUserVO> loginVO = wmUserService.login(dto);
        return ResponseResult.successResult(loginVO);
    }

    @PostMapping("/out")
    public ResponseResult<Boolean> logout() {
        boolean logout = wmUserService.logout();
        return ResponseResult.successResult(logout);
    }
}
