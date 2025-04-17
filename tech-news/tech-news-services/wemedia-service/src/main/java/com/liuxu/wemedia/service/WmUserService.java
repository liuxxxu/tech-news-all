package com.liuxu.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.model.user.vos.LoginVO;
import com.liuxu.model.wemedia.dtos.WmUserDTO;
import com.liuxu.model.wemedia.pojos.WmUser;
import com.liuxu.model.wemedia.vos.WmUserVO;

public interface WmUserService extends IService<WmUser> {
    /**
     * 根据名称查询自媒体用户信息
     *
     * @param name
     * @return
     */
    ResponseResult<WmUser> findByName(String name);

    ResponseResult<WmUser> findByPhone(String phone);

    /**
     * 登录
     *
     * @param dto
     * @return
     */
    LoginVO<WmUserVO> login(WmUserDTO dto);


    boolean logout();
}
