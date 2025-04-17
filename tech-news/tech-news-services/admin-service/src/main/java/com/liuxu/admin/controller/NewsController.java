package com.liuxu.admin.controller;

import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.feigns.WemediaFeign;
import com.liuxu.model.wemedia.dtos.NewsAuthDTO;
import com.liuxu.model.wemedia.dtos.WmNewsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    private WemediaFeign wemediaFeign;

    /**
     * 查询文章审核列表
     *
     * @param dto
     * @return 1、按照创建时间降序
     *         2、后端查询的文章不应该包含草稿
     */
    @PostMapping("/listByStatus")
    PageResponseResult listByStatus(@RequestBody NewsAuthDTO dto) {
        return wemediaFeign.listByStatus(dto);
    }

    /**
     * 查询文章详情
     *
     * @param id
     * @return
     */
    @GetMapping("/auth_one/{id}")
    ResponseResult findWmNewsVo(@PathVariable("id") Long id) {
        return wemediaFeign.findWmNewsVo(id);
    }

    /**
     * 文章审核成功
     *
     * @param dto
     * @return
     */
    @PostMapping("/auth_pass")
    ResponseResult authPass(@RequestBody NewsAuthDTO dto) {
        return wemediaFeign.authPass(dto);
    }

    /**
     * 文章审核失败
     *
     * @param dto
     * @return
     */
    @PostMapping("/auth_fail")
    ResponseResult authFail(@RequestBody NewsAuthDTO dto) {
        return wemediaFeign.authFail(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDTO dto) {
        return wemediaFeign.downOrUp(dto);
    }
}
