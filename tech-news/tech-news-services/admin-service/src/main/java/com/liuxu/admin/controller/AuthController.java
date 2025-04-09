package com.liuxu.admin.controller;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.feigns.UserFeign;
import com.liuxu.model.user.dtos.AuthDTO;
import com.liuxu.model.user.pojos.AppUserRealName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserFeign userFeign;

    @PostMapping("/list")
    public ResponseResult<List<AppUserRealName>> list(@RequestBody AuthDTO dto) {
        return userFeign.loadListByStatus(dto);
    }

    @PutMapping("/authPass")
    public ResponseResult<Boolean> pass(@RequestBody AuthDTO dto) {
        return userFeign.authPass(dto);
    }

    @PutMapping("/authFail")
    public ResponseResult<Boolean> fail(@RequestBody AuthDTO dto) {
        return userFeign.authFail(dto);
    }
}
