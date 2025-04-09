package com.liuxu.user.controller;

import com.liuxu.common.constants.admin.AdminConstants;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.user.dtos.AuthDTO;
import com.liuxu.model.user.dtos.RealNameDTO;
import com.liuxu.model.user.pojos.AppUserRealName;
import com.liuxu.user.service.AppUserRealnameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AppUserAuthController {
    @Autowired
    private AppUserRealnameService appUserRealnameService;

    @PostMapping("/submit")
    public ResponseResult<Boolean> submit(@RequestBody RealNameDTO realNameDTO) {
        Boolean result = appUserRealnameService.submit(realNameDTO);
        return ResponseResult.successResult(result);
    }

    @PostMapping("/list")
    public ResponseResult<List<AppUserRealName>> loadListByStatus(@RequestBody AuthDTO authDTO) {
        List<AppUserRealName> list = appUserRealnameService.loadListByStatus(authDTO);
        return ResponseResult.successResult(list);
    }

    @PutMapping("/authPass")
    public ResponseResult<Boolean> authPass(@RequestBody AuthDTO dto) {
        boolean result = appUserRealnameService.updateStatusById(dto, AdminConstants.PASS_AUTH);
        return ResponseResult.successResult(result);
    }

    @PutMapping("/authFail")
    public ResponseResult<Boolean> authFail(@RequestBody AuthDTO dto) {
        boolean result = appUserRealnameService.updateStatusById(dto, AdminConstants.FAIL_AUTH);
        return ResponseResult.successResult(result);
    }
}
