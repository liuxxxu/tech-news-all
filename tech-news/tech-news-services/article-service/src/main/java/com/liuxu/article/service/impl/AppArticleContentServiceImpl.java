package com.liuxu.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.article.mapper.AppArticleContentMapper;
import com.liuxu.article.service.AppArticleContentService;
import com.liuxu.model.article.pojos.AppArticleContent;
import org.springframework.stereotype.Service;

@Service
public class AppArticleContentServiceImpl extends ServiceImpl<AppArticleContentMapper, AppArticleContent>
        implements AppArticleContentService {
}
