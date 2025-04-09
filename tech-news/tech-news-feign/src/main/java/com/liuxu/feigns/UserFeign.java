package com.liuxu.feigns;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.config.FeignAutoConfiguration;
import com.liuxu.feigns.fallback.UserFeignFallback;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.dtos.AuthDTO;
import com.liuxu.model.user.pojos.AppUserRealName;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "tech-news-user", fallbackFactory = UserFeignFallback.class, configuration = FeignAutoConfiguration.class)
public interface UserFeign {
    @GetMapping("/api/user/{id}")
    ResponseResult<AppUser> findUserById(@PathVariable("id") Long id);

    @PostMapping("/api/auth/list")
    ResponseResult<List<AppUserRealName>> loadListByStatus(@RequestBody AuthDTO authDTO);

    @PutMapping("/api/auth/authPass")
    ResponseResult<Boolean> authPass(@RequestBody AuthDTO authDTO);

    @PutMapping("/api/auth/authFail")
    ResponseResult<Boolean> authFail(@RequestBody AuthDTO authDTO);
}
