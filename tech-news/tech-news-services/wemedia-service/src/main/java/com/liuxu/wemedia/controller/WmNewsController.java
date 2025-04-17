package com.liuxu.wemedia.controller;

import com.liuxu.common.constants.wemedia.WemediaConstants;
import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.wemedia.dtos.NewsAuthDTO;
import com.liuxu.model.wemedia.dtos.WmNewsDTO;
import com.liuxu.model.wemedia.dtos.WmNewsPageReqDTO;
import com.liuxu.model.wemedia.pojos.WmNews;
import com.liuxu.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/listByUser")
    public ResponseResult findByUser(@RequestBody WmNewsPageReqDTO wmNewsPageReqDTO) {
        return wmNewsService.findByUser(wmNewsPageReqDTO);
    }

    @PostMapping("/listByStatus")
    public ResponseResult listByStatus(@RequestBody WmNewsPageReqDTO wmNewsPageReqDTO) {
        return wmNewsService.listByStatus(wmNewsPageReqDTO);
    }

    @PostMapping("/submit")
    public ResponseResult<Void> submitNews(@RequestBody WmNewsDTO dto) {
        return wmNewsService.submitNews(dto);
    }

    @GetMapping("/one/{id}")
    public ResponseResult findWmNewsById(@PathVariable("id") Long id) {
        return wmNewsService.findWmNewsById(id);
    }

    @GetMapping("/del_news/{id}")
    public ResponseResult delNews(@PathVariable("id") Long id) {
        return wmNewsService.delNews(id);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDTO dto) {
        return wmNewsService.downOrUp(dto);
    }

    /**
     * 查询文章列表
     * 
     * @param dto
     * @return
     *         1、按照创建时间降序
     *         2、后端查询的文章不应该包含草稿
     */
    @PostMapping("/list_vo")
    public PageResponseResult findList(@RequestBody NewsAuthDTO dto) {
        return wmNewsService.findList(dto);
    }

    /**
     * 查询文章详情
     * 
     * @param id
     * @return
     */
    @GetMapping("/one_vo/{id}")
    public ResponseResult findWmNewsVo(@PathVariable("id") Long id) {
        return wmNewsService.findWmNewsVo(id);
    }

    /**
     * 文章审核成功
     * 
     * @param dto
     * @return
     */
    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDTO dto) {
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_PASS, dto);
    }

    /**
     * 文章审核失败
     * 
     * @param dto
     * @return
     */
    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDTO dto) {
        return wmNewsService.updateStatus(WemediaConstants.WM_NEWS_AUTH_FAIL, dto);
    }

    /**
     * 修改文章
     * 
     * @param wmNews
     * @return
     */
    @PutMapping("/update")
    public ResponseResult<Boolean> updateWmNews(@RequestBody WmNews wmNews) {
        return ResponseResult.successResult(wmNewsService.updateById(wmNews));
    }
}
