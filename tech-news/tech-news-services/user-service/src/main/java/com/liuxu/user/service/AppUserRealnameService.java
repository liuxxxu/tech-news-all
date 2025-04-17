package com.liuxu.user.service;

import com.liuxu.model.user.dtos.AuthDTO;
import com.liuxu.model.user.dtos.RealNameDTO;
import com.liuxu.model.user.pojos.AppUserRealName;

import java.util.List;

/**
 * app端用户认证
 */
public interface AppUserRealnameService {

    /**
     * 提交实名认证信息
     */
    Boolean submit(RealNameDTO realNameDTO);


    /**
     * 根据状态查询用户实名信息
     *
     * @param authDTO 筛选条件
     * @return
     */
    List<AppUserRealName> loadListByStatus(AuthDTO authDTO);

    /**
     * 根据状态进行审核
     *
     * @param dto
     * @param status 2 审核失败 9 审核成功
     * @return
     */
    boolean updateStatusById(AuthDTO dto, Short status);

}
