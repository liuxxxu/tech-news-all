package com.liuxu.model.user.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * APP实名认证信息表
 */
@Data
@TableName("app_user_realname")
public class AppUserRealName implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 账号ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 用户名称
     */
    @TableField("name")
    private String name;
    /**
     * 身份证号
     */
    @TableField("idno")
    private String idno;
    /**
     * 正面照片
     */
    @TableField("front_image")
    private String frontImage;
    /**
     * 背面照片
     */
    @TableField("back_image")
    private String backImage;

    /**
     * 状态
     * 0 创建中
     * 1 待审核
     * 2 审核失败
     * 9 审核通过
     */
    @TableField("status")
    private Short status;
    /**
     * 拒绝原因
     */
    @TableField("reason")
    private String reason;
    /**
     * 创建时间
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
    @TableField("is_delete")
    @TableLogic
    private Integer isDelete;

}