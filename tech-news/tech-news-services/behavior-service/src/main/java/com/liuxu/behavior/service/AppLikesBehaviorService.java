package com.liuxu.behavior.service;

import com.liuxu.model.behavior.dtos.LikesBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;

public interface AppLikesBehaviorService {
    /**
     * 点赞或取消点赞
     * 
     * @param dto
     * @return
     */
    public ResponseResult like(LikesBehaviorDTO dto);
}
