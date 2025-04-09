package com.liuxu.user.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.user.dtos.UpdateUserDTO;
import com.liuxu.model.user.vos.AppUserVO;
import com.liuxu.user.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class AppUserController {
    @Autowired
    private AppUserService appUserService;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ResponseResult<AppUserVO> getUserInfo() {
        return ResponseResult.successResult(appUserService.getUserInfo());
    }

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public ResponseResult<String> upload(@RequestParam("prefix") String prefix,
                                         @RequestParam("fileName") String fileName,
                                         @RequestPart("file") MultipartFile file) throws IOException {
        return ResponseResult.successResult(appUserService.upload(prefix, fileName, file.getInputStream()));
    }

    /**
     * 修改用户信息
     */
    @PutMapping("/info")
    public ResponseResult<Boolean> updateUserInfo(@RequestBody UpdateUserDTO dto) {
        return ResponseResult.successResult(appUserService.updateUserInfo(dto));
    }

    /**
     * 获取用户数据信息
     */
    @GetMapping("/count")
    public ResponseResult<Map<String, Long>> getProfileCount() {
        return ResponseResult.successResult(appUserService.getProfileCount());
    }
}
