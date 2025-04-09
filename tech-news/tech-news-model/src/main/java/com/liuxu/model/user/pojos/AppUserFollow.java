package com.liuxu.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * APP用户关注信息表
 * @TableName app_user_follow
 */
@TableName(value ="app_user_follow")
@Data
public class AppUserFollow {
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
     * 关注作者ID
     */
    private Long followId;

    /**
     * 粉丝昵称
     */
    private String followName;

    /**
     * 关注度
            0 偶尔感兴趣
            1 一般
            2 经常
            3 高度
     */
    private Integer level;

    /**
     * 是否动态通知
     */
    private Integer isNotice;

    /**
     * 创建时间
     */
    private Date createdTime;
}