package com.liuxu.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.behavior.service.AppLikesBehaviorService;
import com.liuxu.common.constants.article.HotArticleConstants;
import com.liuxu.common.exception.CustException;
import com.liuxu.model.behavior.dtos.LikesBehaviorDTO;
import com.liuxu.model.behavior.pojos.AppLikesBehavior;
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
public class AppLikesBehaviorServiceImpl implements AppLikesBehaviorService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppBehaviorEntryService appBehaviorEntryService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 点赞或取消点赞
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(LikesBehaviorDTO dto) {
        // 1. 校验参数
        // 点赞需要登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN, "点赞需要登录");
        }
        // 校验文章id不能为空 使用注解校验
        // 校验点赞方式 只能是0 或 1 使用注解校验

        // 2. 根据当前登录用户id查询行为实体对象
        AppBehaviorEntry apBehaviorEntry = appBehaviorEntryService.findByUserIdOrEquipmentId(userId,
                dto.getEquipmentId());
        if (apBehaviorEntry == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "获取行为实体信息失败");
        }

        // 3. 如果是点赞操作 判断是否已经点过赞
        Query query = Query
                .query(Criteria.where("entryId").is(apBehaviorEntry.getId()).and("articleId").is(dto.getArticleId()));
        AppLikesBehavior likesBehavior = mongoTemplate.findOne(query, AppLikesBehavior.class);
        if (dto.getOperation().intValue() == 0 && likesBehavior != null) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "请勿重复点赞");
        }

        // 4. 没有点过赞则 像mongo点赞集合中 添加点赞数据
        if (dto.getOperation().intValue() == 0) {
            AppLikesBehavior appLikesBehavior = new AppLikesBehavior();
            appLikesBehavior.setCreatedTime(new Date());
            appLikesBehavior.setArticleId(dto.getArticleId());
            appLikesBehavior.setEntryId(apBehaviorEntry.getId());
            appLikesBehavior.setType((short) 0);
            appLikesBehavior.setOperation((short) 0);
            mongoTemplate.save(appLikesBehavior);
        } else {
            // 5. 如果是取消点赞操作 在mongo点赞集合中 删除对应点赞数据
            mongoTemplate.remove(query, AppLikesBehavior.class);
        }

        // 发送行为消息
        NewBehaviorDTO newBehaviorDTO = new NewBehaviorDTO();
        newBehaviorDTO.setType(NewBehaviorDTO.BehaviorType.LIKES);
        newBehaviorDTO.setArticleId(dto.getArticleId());
        newBehaviorDTO.setAdd(dto.getOperation().intValue() == 0 ? 1 : -1);

        rabbitTemplate.convertAndSend(HotArticleConstants.HOT_ARTICLE_SCORE_BEHAVIOR_QUEUE,
                JSON.toJSON(newBehaviorDTO));
        log.info("发送成功 文章点赞行为消息，消息内容：{}", newBehaviorDTO);
        return ResponseResult.successResult();
    }
}
