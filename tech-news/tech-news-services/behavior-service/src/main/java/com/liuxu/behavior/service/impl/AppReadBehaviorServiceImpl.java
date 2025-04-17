package com.liuxu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.behavior.service.AppReadBehaviorService;
import com.liuxu.common.constants.article.HotArticleConstants;
import com.liuxu.common.exception.CustException;
import com.liuxu.model.behavior.dtos.ReadBehaviorDTO;
import com.liuxu.model.behavior.pojos.AppReadBehavior;
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
public class AppReadBehaviorServiceImpl implements AppReadBehaviorService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppBehaviorEntryService appBehaviorEntryService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 记录阅读行为
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult readBehavior(ReadBehaviorDTO dto) {
        // 校验参数 文章id必须传
        // 根据登录用户id 或 设备id查询行为实体数据(阅读操作可不登录)
        Long userId = AppThreadLocalUtils.getUserId();

        // 查询实体数据
        AppBehaviorEntry behaviorEntry = appBehaviorEntryService.findByUserIdOrEquipmentId(userId,
                dto.getEquipmentId());
        if (behaviorEntry == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "行为实体不存在");
        }

        // 判断阅读行为是否存在
        Query query = Query
                .query(Criteria.where("entryId").is(behaviorEntry.getId()).and("articleId").is(dto.getArticleId()));
        AppReadBehavior appReadBehavior = mongoTemplate.findOne(query, AppReadBehavior.class);

        // 存在 将阅读行为的count字段加1 并修改
        if (appReadBehavior != null) {
            appReadBehavior.setCount((short) (appReadBehavior.getCount() + 1));
            mongoTemplate.save(appReadBehavior);
        } else {
            // 不存在 创建阅读行为 并初始化count字段值为 1
            appReadBehavior = new AppReadBehavior();
            appReadBehavior.setEntryId(behaviorEntry.getId());
            appReadBehavior.setArticleId(dto.getArticleId());
            appReadBehavior.setCount((short) 0);
            appReadBehavior.setCreatedTime(new Date());
            appReadBehavior.setUpdatedTime(new Date());
            mongoTemplate.save(appReadBehavior);
        }

        // 发送行为消息
        NewBehaviorDTO newBehaviorDTO = new NewBehaviorDTO();
        newBehaviorDTO.setType(NewBehaviorDTO.BehaviorType.VIEWS);
        newBehaviorDTO.setArticleId(dto.getArticleId());
        newBehaviorDTO.setAdd(1);

        rabbitTemplate.convertAndSend(HotArticleConstants.HOT_ARTICLE_SCORE_BEHAVIOR_QUEUE,
                JSON.toJSONString(newBehaviorDTO));
        log.info("发送成功 文章阅读行为消息，消息内容：{}", newBehaviorDTO);
        return ResponseResult.successResult();
    }
}
