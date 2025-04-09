package com.liuxu.model.user.dtos;

import lombok.Data;

@Data
public class ResetPasswordDTO {
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}