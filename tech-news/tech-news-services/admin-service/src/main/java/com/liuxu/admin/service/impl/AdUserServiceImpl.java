package com.liuxu.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.admin.mapper.AdUserMapper;
import com.liuxu.admin.service.AdUserService;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.common.utils.AppJwtUtil;
import com.liuxu.model.admin.dtos.AdUserDTO;
import com.liuxu.model.admin.pojo.AdUser;
import com.liuxu.model.admin.vo.AdUserVO;
import com.liuxu.model.user.vos.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * 管理员用户信息
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    /**
     * 登录功能
     *
     * @param dto
     * @return
     */
    @Override
    public LoginVO<AdUserVO> login(AdUserDTO dto) {
        // 1. 校验参数
        String name = dto.getName();
        String password = dto.getPassword();
        if (StringUtils.isAnyBlank(name, password)) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2. 查询用户
        LambdaQueryWrapper<AdUser> wrapper = Wrappers.<AdUser>lambdaQuery()
                .eq(AdUser::getName, name);
        AdUser adUser = getOne(wrapper);
        if (adUser == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }

        // 3. 校验密码
        String dbPassword = adUser.getPassword();
        String inputPwd = DigestUtils.md5DigestAsHex((password + adUser.getSalt()).getBytes());
        if (!dbPassword.equals(inputPwd)) {
            CustException.throwException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "用户名或密码错误");
        }

        // 4. 更新登录信息
        adUser.setLoginTime(new Date());
        updateById(adUser);

        // 5. 返回数据（密码和盐值置空）
        LoginVO<AdUserVO> loginVO = new LoginVO<>();
        AdUserVO AdUserVO = new AdUserVO();
        BeanUtils.copyProperties(adUser, AdUserVO);
        loginVO.setUser(AdUserVO);
        String token = AppJwtUtil.getToken(adUser.getId());
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

}
