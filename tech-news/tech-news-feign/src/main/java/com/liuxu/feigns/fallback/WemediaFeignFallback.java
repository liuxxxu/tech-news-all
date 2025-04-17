package com.liuxu.feigns.fallback;

import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.feigns.WemediaFeign;
import com.liuxu.model.wemedia.dtos.NewsAuthDTO;
import com.liuxu.model.wemedia.dtos.WmNewsDTO;
import com.liuxu.model.wemedia.pojos.WmNews;
import com.liuxu.model.wemedia.pojos.WmUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 服务降级
 */
@Component
@Slf4j
public class WemediaFeignFallback implements FallbackFactory<WemediaFeign> {
    @Override
    public WemediaFeign create(Throwable throwable) {
        return new WemediaFeign() {
            @Override
            public ResponseResult<WmUser> save(WmUser wmUser) {
                log.error("参数：{}", wmUser);
                log.error("自媒体 save 远程调用出错 {}", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<WmUser> findByName(String name) {
                log.error("参数：{}", name);
                log.error("自媒体 findByName 远程调用出错 {}", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<WmUser> findByPhone(String phone) {
                log.error("参数：{}", phone);
                log.error("自媒体 findByPhone 远程调用出错 {}", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<WmNews> findWmNewsById(Long id) {
                log.error("参数: {}", id);
                log.error("自媒体 findWmNewsById 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult<Boolean> updateWmNews(WmNews wmNews) {
                log.error("参数: {}", wmNews);
                log.error("自媒体 updateWmNews 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public PageResponseResult listByStatus(NewsAuthDTO dto) {
                log.error("参数: {}", dto);
                log.error("自媒体 findList 远程调用出错 {} ", throwable.getMessage());
                return new PageResponseResult();
            }

            @Override
            public ResponseResult findWmNewsVo(Long id) {
                log.error("参数: {}", id);
                log.error("自媒体 findWmNewsVo 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult authPass(NewsAuthDTO dto) {
                log.error("参数: {}", dto);
                log.error("自媒体 authPass 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult authFail(NewsAuthDTO dto) {
                log.error("参数: {}", dto);
                log.error("自媒体 authFail 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }

            @Override
            public ResponseResult downOrUp(WmNewsDTO dto) {
                log.error("参数: {}", dto);
                log.error("自媒体 downOrUp 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR);
            }
        };
    }
}
