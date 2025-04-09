package com.liuxu.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.model.user.dtos.LoginDTO;
import com.liuxu.model.user.dtos.RegisterDTO;
import com.liuxu.model.user.dtos.ResetPasswordDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.vos.LoginVO;

public interface AppUserLoginService extends IService<AppUser> {
    /**
     * app端登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * app端退出登录
     */
    boolean logout();

    /**
     * 重置密码
     * 
     * @param dto 包含旧密码和新密码的DTO
     * @return 重置是否成功
     */
    boolean resetPassword(ResetPasswordDTO dto);

    /**
     * app端用户注册
     * 
     * @param dto 注册信息
     * @return 注册成功的用户信息
     */
    AppUser register(RegisterDTO dto);
}
