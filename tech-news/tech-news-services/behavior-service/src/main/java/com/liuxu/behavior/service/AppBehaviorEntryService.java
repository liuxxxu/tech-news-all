package com.liuxu.behavior.service;

import com.liuxu.model.behavior.pojos.AppBehaviorEntry;

public interface AppBehaviorEntryService {
    /**
     * 查询行为实体
     * 
     * @param userId      用户id
     * @param equipmentId 设备id
     * @return
     */
    AppBehaviorEntry findByUserIdOrEquipmentId(Long userId, Long equipmentId);
}
