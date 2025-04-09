package com.liuxu.article.controller;

import com.liuxu.article.service.AuthorService;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/author")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping("/findByUserId/{userId}")
    public ResponseResult<AppAuthor> findByUserId(@PathVariable("userId") Long userId) {
        return authorService.findByUserId(userId);
    }

    @PostMapping("/save")
    public ResponseResult<Boolean> save(@RequestBody AppAuthor appAuthor) {
        boolean save = authorService.save(appAuthor);
        return ResponseResult.successResult(save);
    }

}
