package com.liuxu.search.service.impl;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.search.dtos.UserSearchDTO;
import com.liuxu.model.search.pojos.AppAssociateWords;
import com.liuxu.search.service.AppAssociateWordsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppAssociateWordsServiceImpl implements AppAssociateWordsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 联想词
     * 
     * @param userSearchDto
     * @return
     */
    @Override
    public ResponseResult findAssociate(UserSearchDTO userSearchDto) {
        // 1. 参数检查
        if (StringUtils.isBlank(userSearchDto.getSearchWords())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 执行查询 模糊查询
        Query query = Query.query(Criteria.where("associateWords").regex(".*" + userSearchDto.getSearchWords() + ".*"));
        List<AppAssociateWords> associateWordsList = mongoTemplate.find(query, AppAssociateWords.class);

        return ResponseResult.successResult(associateWordsList);
    }
}
