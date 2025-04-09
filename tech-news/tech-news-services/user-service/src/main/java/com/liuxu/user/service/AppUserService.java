package com.liuxu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.model.user.dtos.UpdateUserDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.vos.AppUserVO;

import java.io.InputStream;
import java.util.Map;

public interface AppUserService extends IService<AppUser> {
    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息VO
     */
    AppUserVO getUserInfo();

    /**
     * 上传图片
     *
     * @return url
     */
    String upload(String prefix, String fileName, InputStream inputStream);

    /**
     * 更新用户信息
     * 
     * @param dto 更新的用户信息
     * @return 更新是否成功
     */
    Boolean updateUserInfo(UpdateUserDTO dto);

    /**
     * 获取用户的关注数和粉丝数等信息
     * @return 关注数和粉丝数的map
     */
    Map<String, Long> getProfileCount();
}
