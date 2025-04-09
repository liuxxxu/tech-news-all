package com.liuxu.feigns;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.config.FeignAutoConfiguration;
import com.liuxu.feigns.fallback.AdminFeignFallback;
import com.liuxu.model.admin.dtos.ChannelDTO;
import com.liuxu.model.admin.pojo.AdChannel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        value = "tech-news-admin",
        fallbackFactory = AdminFeignFallback.class,
        configuration = FeignAutoConfiguration.class
)
public interface AdminFeign {
    @PostMapping("/api/sensitive/sensitives")
    ResponseResult sensitives();

    @GetMapping("/api/channel/one/{id}")
    ResponseResult<AdChannel> findChannelById(@PathVariable Long id);

    /**
     * 频道列表
     * @return
     */
    @GetMapping("/api/channel/channels")
    ResponseResult<List<AdChannel>> selectChannels();

    /**
     * 频道列表
     * @return
     */
    @PostMapping("/api/channel/list")
    ResponseResult listChannels(@RequestBody ChannelDTO dto);
}
