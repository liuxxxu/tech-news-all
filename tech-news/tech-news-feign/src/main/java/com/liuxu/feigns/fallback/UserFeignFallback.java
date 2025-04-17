package com.liuxu.feigns.fallback;

import com.liuxu.feigns.UserFeign;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.user.dtos.AuthDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.pojos.AppUserRealName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserFeignFallback implements FallbackFactory<UserFeign> {
    @Override
    public UserFeign create(Throwable throwable) {
        return new UserFeign() {
            @Override
            public ResponseResult<AppUser> findUserById(Long id) {
                log.error("Feign服务降级触发 远程调用:UserFeign  findUserById 失败,参数:{}", id);
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<List<AppUserRealName>> loadListByStatus(AuthDTO authDTO) {
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<Boolean> authPass(AuthDTO authDTO) {
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<Boolean> authFail(AuthDTO authDTO) {
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }
        };
    }
}
