package com.liuxu.model.user.vos;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * APP用户信息VO
 */
@Data
public class AppUserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String image;

    /**
     * 简介
     */
    private String description;

    /**
     * 性别
     * 0 男
     * 1 女
     * 2 保密
     */
    private Integer sex;

    /**
     * 是否身份认证
     */
    private Boolean identityAuthentication;

    /**
     * 0 普通用户
     * 1 自媒体人
     * 2 大V
     */
    private Integer flag;

    /**
     * 创建时间
     */
    private Date createdTime;
}