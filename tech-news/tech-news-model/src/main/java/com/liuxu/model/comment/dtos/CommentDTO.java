package com.liuxu.model.comment.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {
    @NotNull(message = "文章id不能为空")
    private Long articleId;
    // 最小时间
    private Date minDate;
    //是否是首页
    private Short index;
    // 每页条数
    private Integer size;
}