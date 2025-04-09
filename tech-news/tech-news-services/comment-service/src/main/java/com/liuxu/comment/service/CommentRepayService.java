package com.liuxu.comment.service;

import com.liuxu.model.comment.dtos.CommentRepayDTO;
import com.liuxu.model.comment.dtos.CommentRepayLikeDTO;
import com.liuxu.model.comment.dtos.CommentRepaySaveDTO;
import com.liuxu.common.dtos.ResponseResult;

/**
 * 评论回复 */
public interface CommentRepayService {
    /**
     * 查看更多回复内容
     * @param dto
     * @return
     */
    public ResponseResult loadCommentRepay(CommentRepayDTO dto);

    /**
     * 保存回复
     * @return
     */
    public ResponseResult saveCommentRepay(CommentRepaySaveDTO dto);

    /**
     * 点赞回复的评论
     * @param dto
     * @return
     */
    public ResponseResult saveCommentRepayLike(CommentRepayLikeDTO dto);
}
