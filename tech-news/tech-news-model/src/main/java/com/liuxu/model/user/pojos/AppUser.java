package com.liuxu.model.user.pojos;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * APP用户信息表
 * </p>
 */
@Data
@TableName("app_user")
public class AppUser implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 加密盐
     */
    @TableField("salt")
    private String salt;
    /**
     * 用户名
     */
    @TableField("name")
    private String name;

    /**
     * 密码,md5加密
     */
    @TableField("password")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 头像
     */
    @TableField("image")
    private String image;

    /**
     * 0 男
     * 1 女
     * 2 保密
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 简介
     */
    @TableField("description")
    private Integer description;

    /**
     * 是否身份认证 0 未认证 1 已认证
     */
    @TableField("is_identity_authentication")
    private Integer identityAuthentication;

    /**
     * 0正常
     * 1锁定
     */
    @TableField("status")
    private Integer status;

    /**
     * 0 普通用户
     * 1 自媒体人
     * 2 大V
     */
    @TableField("flag")
    private Integer flag;

    /**
     * 最后登录时间
     */
    @TableField("login_time")
    private Date loginTime;

    /**
     * 注册时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private Date updatedTime;

    /**
     * 删除标志
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

}