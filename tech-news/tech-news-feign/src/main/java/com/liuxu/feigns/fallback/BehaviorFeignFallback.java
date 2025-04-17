package com.liuxu.feigns.fallback;

import com.liuxu.feigns.BehaviorFeign;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BehaviorFeignFallback implements FallbackFactory<BehaviorFeign> {
    @Override
    public BehaviorFeign create(Throwable throwable) {
        throwable.printStackTrace();
        return new BehaviorFeign() {
            @Override
            public ResponseResult<AppBehaviorEntry> findByUserIdOrEquipmentId(Long userId, Long equipmentId) {
                log.error("Feign服务降级触发 远程调用:BehaviorFeign  findByUserIdOrEquipmentId 失败,参数:userId={} equipmentId={}",
                        userId, equipmentId);
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "远程服务调用出现异常");
            }
        };
    }
}
