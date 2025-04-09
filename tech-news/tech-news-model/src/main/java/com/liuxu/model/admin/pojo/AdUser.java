package com.liuxu.model.admin.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Admin用户表
 */
@Data
@TableName("ad_user")
public class AdUser implements Serializable {
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
     * 0正常
     * 1锁定
     */
    @TableField("status")
    private Integer status;

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
     * 最后登录时间
     */
    @TableField("login_time")
    private Date loginTime;

}