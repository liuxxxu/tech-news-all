package com.liuxu.comment.service;

import com.liuxu.model.comment.dtos.CommentDTO;
import com.liuxu.model.comment.pojos.AppComment;
import com.liuxu.common.dtos.ResponseResult;

public interface CommentHotService {

    /**
     * 查找热点评论
     */
    public void hotCommentExecutor(AppComment apComment);

    /**
     * 根据 文章id 查询评论列表
     * @param dto
     * @return
     */
    public ResponseResult findByArticleId(CommentDTO dto);
}
