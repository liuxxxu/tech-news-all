package com.liuxu.comment.service;

import com.liuxu.model.comment.dtos.CommentDTO;
import com.liuxu.model.comment.dtos.CommentLikeDTO;
import com.liuxu.model.comment.dtos.CommentSaveDTO;
import com.liuxu.common.dtos.ResponseResult;

public interface CommentService {
    /**
     * 保存评论
     * @return
     */
    public ResponseResult saveComment(CommentSaveDTO dto);

    /**
     * 点赞评论
     * @param dto
     * @return
     */
    public ResponseResult like(CommentLikeDTO dto);

    /**
     * 根据文章id查询评论列表
     * @return
     */
    public ResponseResult findByArticleId(CommentDTO dto);
}
