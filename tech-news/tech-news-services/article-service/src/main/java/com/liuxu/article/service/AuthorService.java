package com.liuxu.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.dtos.ResponseResult;

public interface AuthorService extends IService<AppAuthor> {
    /**
     * 根据appUserId查询关联作者信息
     * 
     * @param userId
     * @return
     */
    ResponseResult<AppAuthor> findByUserId(Long userId);
}
