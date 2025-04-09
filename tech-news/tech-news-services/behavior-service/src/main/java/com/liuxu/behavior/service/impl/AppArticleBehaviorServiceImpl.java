package com.liuxu.behavior.service.impl;

import com.liuxu.behavior.service.AppArticleBehaviorService;
import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.common.constants.user.UserRelationConstants;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.behavior.dtos.ArticleBehaviorDTO;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.model.behavior.pojos.AppCollection;
import com.liuxu.model.behavior.pojos.AppLikesBehavior;
import com.liuxu.model.behavior.pojos.AppUnlikesBehavior;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AppArticleBehaviorServiceImpl implements AppArticleBehaviorService {

    @Autowired
    private AppBehaviorEntryService appBehaviorEntryService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 加载文章详情 数据回显
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadArticleBehavior(ArticleBehaviorDTO dto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isfollow", false);
        map.put("islike", false);
        map.put("isunlike", false);
        map.put("iscollection", false);

        // 1. 参数校验
        // 判断用户是否登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            // 未登录 返回默认数据
            return ResponseResult.successResult(map);
        }

        // 已登录 查询实体
        AppBehaviorEntry behaviorEntry = appBehaviorEntryService.findByUserIdOrEquipmentId(userId,
                dto.getEquipmentId());
        if (behaviorEntry == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "实体不存在");
        }

        Query query = Query.query(Criteria.where("entryId").is(userId).and("articleId").is(dto.getArticleId()));

        // 查询文章喜欢行为
        AppLikesBehavior likesBehavior = mongoTemplate.findOne(query, AppLikesBehavior.class);
        if (likesBehavior != null) {
            map.remove("islike");
            map.put("islike", true);
        }

        // 查询文章不喜欢行为
        AppUnlikesBehavior unlikesBehavior = mongoTemplate.findOne(query, AppUnlikesBehavior.class);
        if (unlikesBehavior != null) {
            map.remove("isunlike");
            map.put("isunlike", true);
        }

        // 查询文章不喜欢行为
        AppCollection collection = mongoTemplate.findOne(query, AppCollection.class);
        if (collection != null) {
            map.remove("iscollection");
            map.put("iscollection", true);
        }

        // 是否关注
        Double score = redisTemplate.opsForZSet().score(UserRelationConstants.FOLLOW_LIST + userId,
                dto.getAuthorAppUserId().toString());
        if (score != null) {
            map.remove("isfollow");
            map.put("isfollow", true);
        }

        return ResponseResult.successResult(map);
    }
}
