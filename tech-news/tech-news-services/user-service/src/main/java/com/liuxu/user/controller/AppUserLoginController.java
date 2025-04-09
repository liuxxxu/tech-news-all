package com.liuxu.user.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.user.dtos.LoginDTO;
import com.liuxu.model.user.dtos.RegisterDTO;
import com.liuxu.model.user.dtos.ResetPasswordDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.vos.LoginVO;
import com.liuxu.user.service.AppUserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class AppUserLoginController {

    @Autowired
    private AppUserLoginService appUserLoginService;

    @PostMapping("/in")
    public ResponseResult<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO loginVO = appUserLoginService.login(dto);
        return ResponseResult.successResult(loginVO);
    }

    @PostMapping("/out")
    public ResponseResult<Boolean> logout() {
        boolean logout = appUserLoginService.logout();
        return ResponseResult.successResult(logout);
    }

    @PostMapping("/reset-password")
    public ResponseResult<Boolean> resetPassword(@RequestBody ResetPasswordDTO dto) {
        return ResponseResult.successResult(appUserLoginService.resetPassword(dto));
    }

    @PostMapping("/register")
    public ResponseResult<AppUser> register(@RequestBody RegisterDTO dto) {
        AppUser appUser = appUserLoginService.register(dto);
        return ResponseResult.successResult(appUser);
    }
}