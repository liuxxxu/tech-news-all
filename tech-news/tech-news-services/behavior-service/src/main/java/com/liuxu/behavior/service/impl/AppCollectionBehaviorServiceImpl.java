package com.liuxu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.behavior.service.AppCollectionBehaviorService;
import com.liuxu.common.constants.article.HotArticleConstants;
import com.liuxu.common.exception.CustException;
import com.liuxu.model.behavior.dtos.CollectionBehaviorDTO;
import com.liuxu.model.behavior.pojos.AppCollection;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.message.app.NewBehaviorDTO;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.pojos.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class AppCollectionBehaviorServiceImpl implements AppCollectionBehaviorService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppBehaviorEntryService appBehaviorEntryService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 收藏 取消收藏
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collectBehavior(CollectionBehaviorDTO dto) {
        // 1. 参数校验 用户必须登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2. 获取实体
        AppBehaviorEntry behaviorEntry = appBehaviorEntryService.findByUserIdOrEquipmentId(userId,
                dto.getEquipmentId());
        if (behaviorEntry == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "实体不存在");
        }

        // 3. 判断用户是否收藏
        Query query = Query
                .query(Criteria.where("entryId").is(behaviorEntry.getId()).and("articleId").is(dto.getArticleId()));
        AppCollection collection = mongoTemplate.findOne(query, AppCollection.class);
        if (collection != null && dto.getType().intValue() == 0) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "用户以及收藏");
        }

        // 4. 创建收藏
        if (dto.getType().intValue() == 0) {
            collection = new AppCollection();
            collection.setEntryId(behaviorEntry.getId());
            collection.setArticleId(dto.getArticleId());
            collection.setType((short) 0);
            collection.setCollectionTime(new Date());
            mongoTemplate.save(collection);
        } else {
            // 5. 删除收藏
            mongoTemplate.remove(query, AppCollection.class);
        }

        // 发送行为消息
        NewBehaviorDTO newBehaviorDTO = new NewBehaviorDTO();
        newBehaviorDTO.setType(NewBehaviorDTO.BehaviorType.COLLECTION);
        newBehaviorDTO.setArticleId(dto.getArticleId());
        newBehaviorDTO.setAdd(dto.getOperation().intValue() == 0 ? 1 : -1);

        rabbitTemplate.convertAndSend(HotArticleConstants.HOT_ARTICLE_SCORE_BEHAVIOR_QUEUE,
                JSON.toJSONString(newBehaviorDTO));
        log.info("发送成功 文章收藏行为消息，消息内容：{}", newBehaviorDTO);

        return ResponseResult.successResult();
    }
}
