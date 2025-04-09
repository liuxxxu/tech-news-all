package com.liuxu.article.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuxu.article.service.AppArticleConfigService;
import com.liuxu.common.constants.message.NewsUpOrDownConstants;
import com.liuxu.model.article.pojos.AppArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleUpOrDownListener {

    @Autowired
    private AppArticleConfigService AppArticleConfigService;

    @RabbitListener(queuesToDeclare = {@Queue(NewsUpOrDownConstants.NEWS_UP_FOR_ARTICLE_CONFIG_QUEUE)})
    public void handleNewsUpMsg(String articleId) {
        log.info("接收到文章 上架消息 文章id:{}", articleId);
        AppArticleConfigService.update(
                Wrappers.<AppArticleConfig>lambdaUpdate()
                        .set(AppArticleConfig::getIsDown, false)
                        .eq(AppArticleConfig::getArticleId, articleId)
        );
    }

    @RabbitListener(queuesToDeclare = {@Queue(NewsUpOrDownConstants.NEWS_DOWN_FOR_ARTICLE_CONFIG_QUEUE)})
    public void handleNewsDownMsg(String articleId) {
        log.info("接收到文章 下架消息 文章id:{}", articleId);
        AppArticleConfigService.update(
                Wrappers.<AppArticleConfig>lambdaUpdate()
                        .set(AppArticleConfig::getIsDown, true)
                        .eq(AppArticleConfig::getArticleId, articleId)
        );
    }
}
