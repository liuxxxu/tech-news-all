package com.liuxu.behavior.service.impl;

import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.behavior.service.AppUnlikeBehaviorService;
import com.liuxu.common.exception.CustException;
import com.liuxu.model.behavior.dtos.UnLikesBehaviorDTO;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.model.behavior.pojos.AppUnlikesBehavior;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.pojos.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppUnlikeBehaviorServiceImpl implements AppUnlikeBehaviorService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppBehaviorEntryService appBehaviorEntryService;

    /**
     * 保存 或 取消 不喜欢
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult unlikeBehavior(UnLikesBehaviorDTO dto) {
        // 1. 校验参数 文章id不能为空
        // 用户必须登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN, "只有登录才能喜欢文章");
        }

        // 2.查询行为实体
        AppBehaviorEntry behaviorEntry = appBehaviorEntryService.findByUserIdOrEquipmentId(userId,
                dto.getEquipmentId());
        if (behaviorEntry == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "行为实体不存在");
        }

        // 3.获取 不喜欢行为是否存在
        Query query = Query
                .query(Criteria.where("entryId").is(behaviorEntry.getId()).and("articleId").is(dto.getArticleId()));
        AppUnlikesBehavior unlikesBehavior = mongoTemplate.findOne(query, AppUnlikesBehavior.class);
        if (unlikesBehavior != null && dto.getType().intValue() == 0) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "您已经设置不喜欢行为");
        }

        // 4. 添加不喜欢行为
        if (dto.getType().intValue() == 0) {
            unlikesBehavior = new AppUnlikesBehavior();
            unlikesBehavior.setEntryId(behaviorEntry.getId());
            unlikesBehavior.setArticleId(dto.getArticleId());
            unlikesBehavior.setType((short) 0);
            unlikesBehavior.setCreatedTime(new Date());
            mongoTemplate.save(unlikesBehavior);
        } else {
            // 5. 取消不喜欢行为
            mongoTemplate.remove(query, AppUnlikesBehavior.class);
        }

        return ResponseResult.successResult();
    }
}
