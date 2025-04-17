package com.liuxu.user.service.impl;

import com.liuxu.common.constants.user.UserRelationConstants;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.dtos.UserRelationDTO;
import com.liuxu.user.service.AppUserRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class AppUserRelationServiceImpl implements AppUserRelationService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户关注/取消关注
     * 
     * @param dto
     * @return
     */
    @Override
    public boolean follow(UserRelationDTO dto) {
        // 1. 校验参数 authorApUserId 必须登录 operation 0 1
        Long userId = AppThreadLocalUtils.getUserId();
        if (dto.getAuthorAppUserId() == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }
        if (dto.getOperation() == null || (dto.getOperation() != 0 && dto.getOperation() != 1)) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "关注类型错误 类型必须为: 0关注 1取关");
        }

        // 2. 判断当前登录用户是否在关注自己
        if (userId.equals(dto.getAuthorAppUserId())) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_ALLOW, "自己不能关注自己");
        }

        // 3. 判断是否已经关注
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Double score = zSetOperations.score(UserRelationConstants.FOLLOW_LIST + userId,
                dto.getAuthorAppUserId().toString());
        if (dto.getOperation() == 0 && score != null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_ALLOW, "请勿重复关注");
        }

        try {
            // 4. 关注
            if (dto.getOperation() == 0) {
                zSetOperations.add(UserRelationConstants.FOLLOW_LIST + userId,
                        dto.getAuthorAppUserId().toString(),
                        System.currentTimeMillis());
                zSetOperations.add(UserRelationConstants.FANS_LIST + dto.getAuthorAppUserId(), userId.toString(),
                        System.currentTimeMillis());
            } else {
                // 5. 取消关注
                zSetOperations.remove(UserRelationConstants.FOLLOW_LIST + userId,
                        dto.getAuthorAppUserId().toString(),
                        System.currentTimeMillis());
                zSetOperations.remove(UserRelationConstants.FANS_LIST + dto.getAuthorAppUserId(),
                        userId.toString(),
                        System.currentTimeMillis());
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
