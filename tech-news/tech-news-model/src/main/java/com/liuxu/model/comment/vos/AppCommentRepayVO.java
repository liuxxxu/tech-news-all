package com.liuxu.model.comment.vos;

import com.liuxu.model.comment.pojos.AppCommentRepay;
import lombok.Data;

@Data
public class AppCommentRepayVO extends AppCommentRepay {
    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}