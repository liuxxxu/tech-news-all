package com.liuxu.behavior.service;

import com.liuxu.model.behavior.dtos.CollectionBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;

public interface AppCollectionBehaviorService {
    /**
     * 收藏 取消收藏
     * 
     * @param dto
     * @return
     */
    ResponseResult collectBehavior(CollectionBehaviorDTO dto);
}
