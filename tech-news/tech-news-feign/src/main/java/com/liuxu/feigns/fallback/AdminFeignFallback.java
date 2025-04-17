package com.liuxu.feigns.fallback;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.feigns.AdminFeign;
import com.liuxu.model.admin.dtos.ChannelDTO;
import com.liuxu.model.admin.pojo.AdChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AdminFeignFallback implements FallbackFactory<AdminFeign> {
    @Override
    public AdminFeign create(Throwable throwable) {
        return new AdminFeign() {
            @Override
            public ResponseResult sensitives() {
                log.error("AdminFeign sensitives 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult<AdChannel> findChannelById(Long id) {
                log.info("参数: {}", id);
                log.error("AdminFeign findOne 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<List<AdChannel>> selectChannels() {
                log.error("AdminFeign selectChannels 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult listChannels(ChannelDTO dto) {
                log.error("AdminFeign listChannels 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }
        };
    }
}
