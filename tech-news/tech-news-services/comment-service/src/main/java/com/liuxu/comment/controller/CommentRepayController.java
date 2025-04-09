package com.liuxu.comment.controller;

import com.liuxu.comment.service.CommentRepayService;
import com.liuxu.model.comment.dtos.CommentRepayDTO;
import com.liuxu.model.comment.dtos.CommentRepayLikeDTO;
import com.liuxu.model.comment.dtos.CommentRepaySaveDTO;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment_repay")
public class CommentRepayController {

    @Autowired
    private CommentRepayService commentRepayService;

    @PostMapping("/load")
    public ResponseResult loadCommentRepay(@RequestBody @Validated CommentRepayDTO dto){
        return commentRepayService.loadCommentRepay(dto);
    }

    @PostMapping("/save")
    public ResponseResult saveCommentRepay(@RequestBody @Validated CommentRepaySaveDTO dto){
        return commentRepayService.saveCommentRepay(dto);
    }

    @PostMapping("/like")
    public ResponseResult saveCommentRepayLike(@RequestBody @Validated CommentRepayLikeDTO dto){
        return commentRepayService.saveCommentRepayLike(dto);
    }
}