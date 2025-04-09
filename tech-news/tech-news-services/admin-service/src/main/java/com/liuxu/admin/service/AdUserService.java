package com.liuxu.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.model.admin.dtos.AdUserDTO;
import com.liuxu.model.admin.pojo.AdUser;
import com.liuxu.model.admin.vo.AdUserVO;
import com.liuxu.model.user.vos.LoginVO;

/**
 * 管理员用户信息 */
public interface AdUserService extends IService<AdUser> {
    /**
     * 登录功能
     *
     * @param adUserDTO
     * @return
     */
    LoginVO<AdUserVO> login(AdUserDTO adUserDTO);

    boolean logout();
}
