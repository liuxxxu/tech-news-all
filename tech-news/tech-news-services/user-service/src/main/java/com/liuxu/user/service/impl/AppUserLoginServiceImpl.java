package com.liuxu.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.dtos.LoginDTO;
import com.liuxu.model.user.dtos.RegisterDTO;
import com.liuxu.model.user.dtos.ResetPasswordDTO;
import com.liuxu.model.user.pojos.AppUser;
import com.liuxu.model.user.vos.AppUserVO;
import com.liuxu.model.user.vos.LoginVO;
import com.liuxu.user.mapper.AppUserMapper;
import com.liuxu.user.service.AppUserLoginService;
import com.liuxu.user.service.AppUserService;
import com.liuxu.common.utils.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.UUID;

@Service
public class AppUserLoginServiceImpl extends ServiceImpl<AppUserMapper, AppUser>  implements AppUserLoginService {

    @Autowired
    private AppUserService appUserService;

    /**
     * app端登录
     * 
     * @param dto
     * @return
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 校验参数
        String phone = dto.getPhone();
        String password = dto.getPassword();
        if (StringUtils.isAnyBlank(phone, password)) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 查询用户
        LambdaQueryWrapper<AppUser> wrapper = Wrappers.<AppUser>lambdaQuery()
                .eq(AppUser::getPhone, phone);
        AppUser appUser = appUserService.getOne(wrapper);
        if (appUser == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }

        // 3. 校验密码
        String dbPassword = appUser.getPassword();
        String inputPwd = DigestUtils.md5DigestAsHex((password + appUser.getSalt()).getBytes());
        if (!dbPassword.equals(inputPwd)) {
            CustException.throwException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "用户名或密码错误");
        }

        // 4. 更新登录信息
        appUser.setLoginTime(new Date());
        appUserService.updateById(appUser);

        // 5. 返回数据（密码和盐值置空）
        LoginVO<AppUserVO> loginVO = new LoginVO<>();
        AppUserVO appUserVO = new AppUserVO();
        BeanUtils.copyProperties(appUser, appUserVO);
        loginVO.setUser(appUserVO);
        String token = AppJwtUtil.getToken(appUser.getId());
        loginVO.setToken(token);
        return loginVO;
    }

    @Override
    public boolean logout() {
        // 1. 获取当前登录用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2. 清除当前用户的登录信息
        AppThreadLocalUtils.clear();

        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordDTO dto) {
        // 1. 参数校验
        if (dto == null || StringUtils.isAnyBlank(dto.getOldPassword(), dto.getNewPassword())) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 获取当前登录用户
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 3. 查询用户信息
        AppUser appUser = appUserService.getById(userId);
        if (appUser == null) {
            CustException.throwException(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        // 4. 校验旧密码
        String oldInputPwd = DigestUtils.md5DigestAsHex((dto.getOldPassword() + appUser.getSalt()).getBytes());
        if (!appUser.getPassword().equals(oldInputPwd)) {
            CustException.throwException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "原密码错误");
        }

        // 5. 更新新密码
        String newPassword = DigestUtils.md5DigestAsHex((dto.getNewPassword() + appUser.getSalt()).getBytes());
        appUser.setPassword(newPassword);
        appUser.setUpdatedTime(new Date());

        return appUserService.updateById(appUser);
    }

    @Override
    @Transactional
    public AppUser register(RegisterDTO dto) {
        // 1. 参数校验
        String phone = dto.getPhone();
        String password = dto.getPassword();
        if (StringUtils.isAnyBlank(phone, password)) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 检查手机号是否已注册
        LambdaQueryWrapper<AppUser> wrapper = Wrappers.<AppUser>lambdaQuery()
                .eq(AppUser::getPhone, phone);
        if (appUserService.count(wrapper) > 0) {
            CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "手机号已注册");
        }

        // 3. 构建用户实体
        AppUser appUser = new AppUser();
        appUser.setPhone(phone);
        appUser.setName(phone); // 默认用手机号作为用户名
        appUser.setSalt(UUID.randomUUID().toString().replace("-", ""));
        appUser.setPassword(DigestUtils.md5DigestAsHex((password + appUser.getSalt()).getBytes()));
        appUser.setStatus(0); // 正常状态
        appUser.setFlag(0); // 普通用户
        appUser.setIdentityAuthentication(0);
        appUser.setCreatedTime(new Date());
        appUser.setUpdatedTime(new Date());
        appUser.setIsDelete(0);

        // 4. 保存用户
        appUserService.save(appUser);

        // 5. 返回用户信息（密码和盐值置空）
        appUser.setPassword(null);
        appUser.setSalt(null);
        return appUser;
    }
}
