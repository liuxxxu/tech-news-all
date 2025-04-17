package com.liuxu.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.article.mapper.AuthorMapper;
import com.liuxu.article.service.AuthorService;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorMapper, AppAuthor> implements AuthorService {
    /**
     * 根据appUserId查询关联作者信息
     * 
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<AppAuthor> findByUserId(Long userId) {
        LambdaQueryWrapper<AppAuthor> appAuthorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        appAuthorLambdaQueryWrapper.eq(AppAuthor::getUserId, userId);
        AppAuthor one = getOne(appAuthorLambdaQueryWrapper);
        return ResponseResult.successResult(one);
    }

}
