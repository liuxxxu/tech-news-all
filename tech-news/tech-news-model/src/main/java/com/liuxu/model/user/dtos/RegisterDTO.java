package com.liuxu.model.user.dtos;

import lombok.Data;

@Data
public class RegisterDTO {
    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;
}