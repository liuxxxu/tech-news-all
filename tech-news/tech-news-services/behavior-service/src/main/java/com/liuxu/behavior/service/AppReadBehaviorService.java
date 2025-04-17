package com.liuxu.behavior.service;

import com.liuxu.model.behavior.dtos.ReadBehaviorDTO;
import com.liuxu.common.dtos.ResponseResult;

public interface AppReadBehaviorService {
    /**
     * 记录阅读行为
     * 
     * @param dto
     * @return
     */
    ResponseResult readBehavior(ReadBehaviorDTO dto);
}
