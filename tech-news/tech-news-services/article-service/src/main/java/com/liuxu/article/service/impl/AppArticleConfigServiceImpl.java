package com.liuxu.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.article.mapper.AppArticleConfigMapper;
import com.liuxu.article.service.AppArticleConfigService;
import com.liuxu.model.article.pojos.AppArticleConfig;
import org.springframework.stereotype.Service;

@Service
public class AppArticleConfigServiceImpl extends ServiceImpl<AppArticleConfigMapper, AppArticleConfig>
        implements AppArticleConfigService {
}
