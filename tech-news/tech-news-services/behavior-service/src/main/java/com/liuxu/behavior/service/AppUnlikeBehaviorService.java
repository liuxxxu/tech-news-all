package com.liuxu.behavior.service;

import com.liuxu.model.behavior.dtos.UnLikesBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;

public interface AppUnlikeBehaviorService {
    /**
     * 保存 或 取消 不喜欢
     * 
     * @param dto
     * @return
     */
    ResponseResult unlikeBehavior(UnLikesBehaviorDTO dto);
}
