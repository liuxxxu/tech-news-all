package com.liuxu.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * APP用户粉丝信息表
 * @TableName app_user_fan
 */
@TableName(value ="app_user_fan")
@Data
public class AppUserFan {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 粉丝ID
     */
    private Long fansId;

    /**
     * 粉丝昵称
     */
    private String fansName;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 是否可见我动态
     */
    private Integer isDisplay;

    /**
     * 是否屏蔽私信
     */
    private Integer isShieldLetter;

    /**
     * 是否屏蔽评论
     */
    private Integer isShieldComment;
}