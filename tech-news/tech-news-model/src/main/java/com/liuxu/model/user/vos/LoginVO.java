package com.liuxu.model.user.vos;

import lombok.Data;

@Data
public class LoginVO<T> {
    /**
     * 登录令牌
     */
    private String token;

    /**
     * 用户信息
     */
    private T user;
}