package com.liuxu.search.service.impl;

import com.liuxu.common.exception.CustException;
import com.liuxu.feigns.BehaviorFeign;
import com.liuxu.model.behavior.pojos.AppBehaviorEntry;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.search.dtos.HistorySearchDTO;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.model.search.pojos.AppUserSearch;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.search.service.AppUserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AppUserSearchServiceImpl implements AppUserSearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private BehaviorFeign behaviorFeign;

    /**
     * 保存用户搜索历史记录
     * 
     * @param dto
     */
    @Async("taskExecutor")
    @Override
    public void insert(UserSearchDTO dto) {
        String searchWords = dto.getSearchWords();

        // 1. 根据用户id 或 设备id 查询对应的行为实体
        ResponseResult<AppBehaviorEntry> result = behaviorFeign.findByUserIdOrEquipmentId(dto.getEntryId(),
                dto.getEquipmentId());
        if (result.checkCode()) {
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
        AppBehaviorEntry behaviorEntry = result.getData();

        // 2. 根据行为实体id 及 关键词查询是否存在
        Query query = Query.query(Criteria.where("entryId").is(behaviorEntry.getId()).and("keyword").is(searchWords));
        AppUserSearch userSearch = mongoTemplate.findOne(query, AppUserSearch.class);

        // 3. 如果存在该历史记录，修改创建时间
        if (userSearch != null) {
            userSearch.setCreatedTime(new Date());
            mongoTemplate.save(userSearch);
            return;
        }

        // 4. 如果不存在 则新增记录
        userSearch = new AppUserSearch();
        userSearch.setEntryId(behaviorEntry.getId());
        userSearch.setKeyword(searchWords);
        userSearch.setCreatedTime(new Date());
        mongoTemplate.insert(userSearch);
    }

    /**
     * 查询用户搜索历史记录 显示10条
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findUserSearch(UserSearchDTO dto) {
        // 1. 查询用户的登录信息
        Long userId = AppThreadLocalUtils.getUserId();

        // 2. 根据用户id或设备id 查询行为数据
        ResponseResult<AppBehaviorEntry> result = behaviorFeign
                .findByUserIdOrEquipmentId(userId, dto.getEquipmentId());
        if (result.checkCode()) {
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
        AppBehaviorEntry behaviorEntry = result.getData();

        // 3. 根据行为实体id查询 历史记录
        // 默认查询10条历史记录，并且按照时间降序排序
        Query query = Query.query(Criteria.where("entryId").is(behaviorEntry.getId()))
                .with(Sort.by(Sort.Direction.DESC, "createdTime"))
                .limit(10);
        List<AppUserSearch> appUserSearchList = mongoTemplate.find(query, AppUserSearch.class);

        return ResponseResult.successResult(appUserSearchList);
    }

    /**
     * 删除搜索历史
     * 
     * @param historySearchDto
     * @return
     */
    @Override
    public ResponseResult delUserSearch(HistorySearchDTO historySearchDto) {
        // 1. 校验参数
        if (historySearchDto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 判断实体数据是否存在
        Long userId = AppThreadLocalUtils.getUserId();
        ResponseResult<AppBehaviorEntry> result = behaviorFeign
                .findByUserIdOrEquipmentId(userId, historySearchDto.getEquipmentId());
        if (result.checkCode()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR, "远程调用行为服务失败");
        }

        AppBehaviorEntry behaviorEntry = result.getData();
        if (behaviorEntry == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "行为实体数据不存在");
        }

        // 3. 删除实体数据
        Query query = Query
                .query(Criteria.where("_id").is(historySearchDto.getId()).and("entryId").is(behaviorEntry.getId()));
        mongoTemplate.remove(query, AppUserSearch.class);

        return ResponseResult.successResult();
    }

}
