package com.liuxu.search.service;

import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.search.dtos.HistorySearchDTO;
import com.liuxu.model.search.dtos.UserSearchDTO;

public interface AppUserSearchService {
    /**
     * 保存用户搜索历史记录
     * 
     * @param userSearchDto
     */
    public void insert(UserSearchDTO userSearchDto);

    /**
     * 查询搜索历史
     * 
     * @param userSearchDto
     * @return
     */
    ResponseResult findUserSearch(UserSearchDTO userSearchDto);

    /**
     * 删除搜索历史
     * 
     * @param historySearchDto
     * @return
     */
    ResponseResult delUserSearch(HistorySearchDTO historySearchDto);
}
