package com.liuxu.model.comment.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * APP评论信息点赞
 */
@Data
@Document("ap_comment_like")
public class AppCommentLike {

    /**
     * id
     */
    private String id;

    /**
     * 用户ID
     */
    private Long authorId;

    /**
     * 评论id
     */
    private String commentId;

    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}