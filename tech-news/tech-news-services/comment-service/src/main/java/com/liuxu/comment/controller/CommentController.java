package com.liuxu.comment.controller;

import com.liuxu.comment.service.CommentService;
import com.liuxu.model.comment.dtos.CommentDTO;
import com.liuxu.model.comment.dtos.CommentLikeDTO;
import com.liuxu.model.comment.dtos.CommentSaveDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 保存评论
     * @return
     */
    @PostMapping("/save")
    public ResponseResult saveComment(@RequestBody @Validated CommentSaveDTO dto) {
        return commentService.saveComment(dto);
    }

    @PostMapping("/like")
    public ResponseResult like(@RequestBody @Validated CommentLikeDTO dto){
        return commentService.like(dto);
    }

    @PostMapping("/load")
    public ResponseResult findByArticleId(@RequestBody @Validated CommentDTO dto){
        return commentService.findByArticleId(dto);
    }
}
