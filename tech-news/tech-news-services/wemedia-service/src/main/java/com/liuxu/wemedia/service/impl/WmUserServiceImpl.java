package com.liuxu.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.common.utils.AppJwtUtil;
import com.liuxu.model.user.vos.LoginVO;
import com.liuxu.model.wemedia.dtos.WmUserDTO;
import com.liuxu.model.wemedia.pojos.WmUser;
import com.liuxu.model.wemedia.vos.WmUserVO;
import com.liuxu.wemedia.mapper.WmUserMapper;
import com.liuxu.wemedia.service.WmUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;

@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    /**
     * 根据名称查询自媒体用户信息
     *
     * @param name
     * @return
     */
    @Override
    public ResponseResult<WmUser> findByName(String name) {
        WmUser wmUser = getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, name));
        if (wmUser != null) {
            return ResponseResult.successResult(wmUser);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }
    }

    @Override
    public ResponseResult<WmUser> findByPhone(String phone) {
        WmUser wmUser = getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getPhone, phone));
        return ResponseResult.successResult(wmUser);
    }

    /**
     * 登录
     *
     * @param dto
     * @return
     */
    @Override
    public LoginVO<WmUserVO> login(WmUserDTO dto) {
        // 1. 校验参数 phone password
        String phone = dto.getPhone();
        String password = dto.getPassword();

        if (StringUtils.isAnyBlank(phone, password)) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 2.查询用户
        LambdaQueryWrapper<WmUser> wrapper = Wrappers.<WmUser>lambdaQuery().eq(WmUser::getPhone, phone);
        WmUser wmUser = getOne(wrapper);
        if (wmUser == null) {
            CustException.throwException(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        // 3. 判断输入的密码和数据库中的密码是否一致
        String inputPWD = DigestUtils.md5DigestAsHex((dto.getPassword() + wmUser.getSalt()).getBytes());
        if (!inputPWD.equals(wmUser.getPassword())) {
            CustException.throwException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "用户名或密码错误");
        }

        // 4. 判断 user 状态 9正常
        Integer status = wmUser.getStatus();
        if (status != 9) {
            CustException.throwException(AppHttpCodeEnum.LOGIN_STATUS_ERROR);
        }

        // 5. 修改最近登录时间
        wmUser.setLoginTime(new Date());
        updateById(wmUser);

        // 6. 颁发凭证
        String token = AppJwtUtil.getToken(wmUser.getId());

        // 7.封装返回结果
        WmUserVO wmUserVO = new WmUserVO();
        BeanUtils.copyProperties(wmUser, wmUserVO);

        LoginVO<WmUserVO> loginVO = new LoginVO<>();
        loginVO.setUser(wmUserVO);
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
