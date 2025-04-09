package com.liuxu.model.comment.dtos;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
public class CommentRepayDTO {
    /**
     * 评论id
     */
    @NotNull(message = "评论id不能为空")
    private String commentId;

    private Integer size;


    // 最小时间
    private Date minDate;
}