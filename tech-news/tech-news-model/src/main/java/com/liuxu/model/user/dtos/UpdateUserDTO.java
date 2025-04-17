package com.liuxu.model.user.dtos;

import lombok.Data;

@Data
public class UpdateUserDTO {
    /**
     * 用户头像
     */
    private String image;

    /**
     * 用户昵称
     */
    private String name;

    /**
     * 性别，0：男，1：女，2：保密
     */
    private Integer sex;

    /**
     * 手机号
     */
    // private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 个人简介
     */
    private String description;
}