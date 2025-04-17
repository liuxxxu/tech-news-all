package com.liuxu.search.service;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.dtos.UserSearchDTO;

/**
 * <p>
 * 联想词表 服务类
 * </p>
 *
 */
public interface AppAssociateWordsService {

    /**
     * 联想词
     * 
     * @param userSearchDto
     * @return
     */
    ResponseResult findAssociate(UserSearchDTO userSearchDto);

}