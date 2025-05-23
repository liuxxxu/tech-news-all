package com.liuxu.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 自媒体图文内容信息表
 * </p>
 *
 */
@Data
@TableName("wm_news")
public class WmNews implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 作者
     */
    @TableField("author_name")
    private String authorName;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图文内容
     */
    @TableField("content")
    private String content;

    /**
     * AI总结
     */
    @TableField("summary")
    private String summary;

    /**
     * 封面类型
     */
    @TableField("type")
    private Short type;

    /**
     * 封面图片
     */
    @TableField("cover")
    private String cover;

    /**
     * 频道ID
     */
    @TableField("channel_id")
    private Long channelId;

    @TableField("labels")
    private String labels;


    /**
     * 当前状态
     0 草稿
     1 提交（待审核）
     2 审核失败
     3 人工审核
     4 人工审核通过
     8 审核通过（待发布）
     9 已发布
     */
    @TableField("status")
    private Short status;

    /**
     * 定时发布时间，不定时则为空
     */
    @TableField("publish_time")
    private Date publishTime;

    /**
     * 拒绝理由
     */
    @TableField("reason")
    private String reason;

    /**
     * 发布库文章ID
     */
    @TableField("article_id")
    private Long articleId;


    @TableField("enable")
    private Short enable;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 修改时间
     */
    @TableField("updated_time")
    private Date updatedTime;

    /**
     * 删除标志
     */
    @TableField("is_delete")
    @TableLogic
    private Short isDelete;

    //状态枚举类***
    @Alias("WmNewsStatus")
    public enum Status {
        DRAFT((short) 0),
        SUBMIT((short) 1),
        FAIL((short) 2),
        ADMIN_AUTH((short) 3),
        ADMIN_SUCCESS((short) 4),
        SUCCESS((short) 8),
        PUBLISHED((short) 9),
        ;
        short code;

        Status(short code) {
            this.code = code;
        }

        public short getCode() {
            return this.code;
        }
    }

}