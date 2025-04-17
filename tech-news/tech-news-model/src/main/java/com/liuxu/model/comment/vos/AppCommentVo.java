package com.liuxu.model.comment.vos;

import com.liuxu.model.comment.pojos.AppComment;
import lombok.Data;

@Data
public class AppCommentVo extends AppComment {
    /**
     * 0：点赞
     */
    private Short operation;
}