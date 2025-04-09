package com.liuxu.behavior.service.impl;

import com.liuxu.behavior.service.AppBehaviorEntryService;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AppBehaviorEntryServiceImpl implements AppBehaviorEntryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询行为实体
     * 
     * @param userId      用户id
     * @param equipmentId 设备id
     * @return
     */
    @Override
    public AppBehaviorEntry findByUserIdOrEquipmentId(Long userId, Long equipmentId) {
        // 1. 判断userId是否为空 不为空 使用userId查询 如果不存在基于userId创建实体数据
        if (userId != null) {
            Query query = Query
                    .query(Criteria.where("refId").is(userId).and("type").is(AppBehaviorEntry.Type.USER.getCode()));
            AppBehaviorEntry behaviorEntry = mongoTemplate.findOne(query, AppBehaviorEntry.class);

            if (behaviorEntry == null) {
                behaviorEntry = new AppBehaviorEntry();
                behaviorEntry.setType(AppBehaviorEntry.Type.USER.getCode());
                behaviorEntry.setRefId(userId);
                behaviorEntry.setCreatedTime(new Date());
                mongoTemplate.save(behaviorEntry);
            }
            return behaviorEntry;
        }

        // 2. 判断设备id是否为空 不为空 使用设备id查询 如果不存在基于设备id创建实体数据
        if (equipmentId != null) {
            Query query = Query.query(
                    Criteria.where("refId").is(equipmentId).and("type").is(AppBehaviorEntry.Type.EQUIPMENT.getCode()));
            AppBehaviorEntry behaviorEntry = mongoTemplate.findOne(query, AppBehaviorEntry.class);

            if (behaviorEntry == null) {
                behaviorEntry = new AppBehaviorEntry();
                behaviorEntry.setType(AppBehaviorEntry.Type.EQUIPMENT.getCode());
                behaviorEntry.setRefId(equipmentId);
                behaviorEntry.setCreatedTime(new Date());
                mongoTemplate.save(behaviorEntry);
            }
            return behaviorEntry;
        }
        return null;
    }
}
