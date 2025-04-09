package com.liuxu.feigns;

import com.liuxu.config.FeignAutoConfiguration;
import com.liuxu.feigns.fallback.BehaviorFeignFallback;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "tech-news-behavior", fallbackFactory = BehaviorFeignFallback.class, configuration = FeignAutoConfiguration.class)
public interface BehaviorFeign {
    @GetMapping("/api/behavior_entry/one")
    ResponseResult<AppBehaviorEntry> findByUserIdOrEquipmentId(@RequestParam("userId") Long userId,
            @RequestParam("equipmentId") Long equipmentId);
}