package com.liuxu.article.listener;

import com.liuxu.article.service.AppArticleService;
import com.liuxu.common.constants.message.PublishArticleConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class PublishArticleListener {
    @Autowired
    private AppArticleService appArticleService;

    @RabbitListener(queuesToDeclare = {@Queue(value = PublishArticleConstants.PUBLISH_ARTICLE_QUEUE)})
    public void publishArticle(String newsId) {
        log.info("接收到发布文章通知， 待发布文章id: {} , 当前时间: {}", newsId, LocalDateTime.now());
        try {
            appArticleService.publishArticle(Long.valueOf(newsId));
            log.info("发布文章通知处理完毕  文章发布成功");
        } catch (Exception e) {
            log.error("发布文章通知处理失败，文章未能成功发布 文章id: {} , 失败原因:{}", newsId, e.getMessage());
        }
    }
}