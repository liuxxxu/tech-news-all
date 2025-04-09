package com.liuxu.behavior.service;

import com.liuxu.model.behavior.dtos.ArticleBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;

public interface AppArticleBehaviorService {
    /**
     * 加载文章详情 数据回显
     * 
     * @param dto
     * @return
     */
    public ResponseResult loadArticleBehavior(ArticleBehaviorDTO dto);
}
