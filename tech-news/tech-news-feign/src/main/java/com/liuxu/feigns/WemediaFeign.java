package com.liuxu.feigns;

import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.config.FeignAutoConfiguration;
import com.liuxu.feigns.fallback.WemediaFeignFallback;
import com.liuxu.model.wemedia.dtos.NewsAuthDTO;
import com.liuxu.model.wemedia.dtos.WmNewsDTO;
import com.liuxu.model.wemedia.pojos.WmNews;
import com.liuxu.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        value = "tech-news-wemedia", fallbackFactory = WemediaFeignFallback.class, configuration = FeignAutoConfiguration.class)
public interface WemediaFeign {
    @PostMapping("/api/user/save")
    ResponseResult<WmUser> save(@RequestBody @Validated WmUser wmUser);

    @GetMapping("/api/user/findByName/{name}")
    ResponseResult<WmUser> findByName(@PathVariable("name") String name);

    @GetMapping("/api/user/findByPhone/{phone}")
    ResponseResult<WmUser> findByPhone(@PathVariable("phone") String phone);

    @GetMapping("/api/news/one/{id}")
    ResponseResult<WmNews> findWmNewsById(@PathVariable("id") Long id);

    @PutMapping("/api/news/update")
    ResponseResult updateWmNews(@RequestBody WmNews wmNews);

    /**
     * 查询文章列表
     *
     * @param dto
     * @return 1、按照创建时间降序
     * 2、后端查询的文章不应该包含草稿
     */
    @PostMapping("/api/news/listByStatus")
    PageResponseResult listByStatus(@RequestBody NewsAuthDTO dto);


    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/api/news/one_vo/{id}")
    ResponseResult findWmNewsVo(@PathVariable("id") Long id);

    /**
     * 文章审核成功
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/news/auth_pass")
    ResponseResult authPass(@RequestBody NewsAuthDTO dto);

    /**
     * 文章审核失败
     *
     * @param dto
     * @return
     */
    @PostMapping("/api/news/auth_fail")
    ResponseResult authFail(@RequestBody NewsAuthDTO dto);

    @PostMapping("/api/news/down_or_up")
    ResponseResult downOrUp(WmNewsDTO dto);
}
