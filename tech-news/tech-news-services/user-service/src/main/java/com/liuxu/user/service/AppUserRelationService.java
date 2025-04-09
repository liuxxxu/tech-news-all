package com.liuxu.user.service;

import com.liuxu.model.user.dtos.UserRelationDTO;

public interface AppUserRelationService {
    /**
     * 用户关注/取消关注
     * 
     * @param dto
     * @return
     */
    public boolean follow(UserRelationDTO dto);
}
