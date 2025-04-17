package com.liuxu.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.wemedia.dtos.NewsAuthDTO;
import com.liuxu.model.wemedia.dtos.WmNewsDTO;
import com.liuxu.model.wemedia.dtos.WmNewsPageReqDTO;
import com.liuxu.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {
    /**
     * 查询用户自媒体文章
     * @return
     */
    ResponseResult findByUser(WmNewsPageReqDTO dto);

    ResponseResult listByStatus(WmNewsPageReqDTO wmNewsPageReqDTO);

    /**
     * 自媒体文章发布
     * @param dto
     * @return
     */
    ResponseResult<Void> submitNews(WmNewsDTO dto);

    /**
     * 根据文章id查询文章
     * @return
     */
    ResponseResult findWmNewsById(Long id);

    /**
     * 删除文章
     * @return
     */
    ResponseResult delNews(Long id);

    /**
     * 上下架
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsDTO dto);

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    PageResponseResult findList(NewsAuthDTO dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    ResponseResult findWmNewsVo(Long id) ;

    /**
     *  自媒体文章人工审核
     * @param status  2  审核失败  4 审核成功
     * @param dto
     * @return
     */
    ResponseResult updateStatus(Short status, NewsAuthDTO dto);


}
