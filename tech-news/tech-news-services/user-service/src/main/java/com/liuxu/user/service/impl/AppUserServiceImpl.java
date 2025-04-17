package com.liuxu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.file.service.impl.MinIOFileStorageService;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.dtos.UpdateUserDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.pojos.AppUserFan;
import com.liuxu.model.user.pojos.AppUserFollow;
import com.liuxu.model.user.vos.AppUserVO;
import com.liuxu.user.mapper.AppUserMapper;
import com.liuxu.user.service.AppUserFanService;
import com.liuxu.user.service.AppUserFollowService;
import com.liuxu.user.service.AppUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements AppUserService {

    @Autowired
    private AppUserFollowService appUserFollowService;

    @Autowired
    private AppUserFanService appUserFanService;

    @Autowired
    private MinIOFileStorageService minIOFileStorageService;

    @Override
    public AppUserVO getUserInfo() {
        // 1. 获取当前登录用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2. 获取用户基本信息
        AppUser appUser = getById(userId);
        if (appUser == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }

        // 3. 创建VO对象并复制基本信息
        AppUserVO userVO = new AppUserVO();
        BeanUtils.copyProperties(appUser, userVO);

        return userVO;
    }

    @Override
    public String upload(String prefix, String fileName, InputStream inputStream) {
        return minIOFileStorageService.uploadImage(prefix, fileName, inputStream);
    }

    @Override
    @Transactional
    public Boolean updateUserInfo(UpdateUserDTO dto) {
        // 1. 获取当前登录用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2. 更新用户信息
        AppUser updateUser = new AppUser();
        updateUser.setId(userId);
        BeanUtils.copyProperties(dto, updateUser);
        updateById(updateUser);

        // 远程更新自媒体用户信息


        // 远程更新文章作者信息




        return true;
    }

    @Override
    public Map<String, Long> getProfileCount() {
        // 1. 获取当前登录用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2. 获取关注数
        LambdaQueryWrapper<AppUserFollow> followWrapper = new LambdaQueryWrapper<>();
        followWrapper.eq(AppUserFollow::getUserId, userId);
        long followCount = appUserFollowService.count(followWrapper);

        // 3. 获取粉丝数
        LambdaQueryWrapper<AppUserFan> fanWrapper = new LambdaQueryWrapper<>();
        fanWrapper.eq(AppUserFan::getUserId, userId);
        long fansCount = appUserFanService.count(fanWrapper);

        // 4. 返回结果
        Map<String, Long> result = new HashMap<>();
        result.put("followCount", followCount);
        result.put("fansCount", fansCount);
        return result;
    }
}
