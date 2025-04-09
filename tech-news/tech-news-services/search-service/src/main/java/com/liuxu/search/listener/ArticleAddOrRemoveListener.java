package com.liuxu.search.listener;

import com.liuxu.common.constants.message.NewsUpOrDownConstants;
import com.liuxu.common.exception.CustException;
import com.liuxu.feigns.ArticleFeign;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.search.vos.SearchArticleVO;
import com.liuxu.search.service.ArticleSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleAddOrRemoveListener {

    @Autowired
    private ArticleSearchService articleSearchService;

    @Autowired
    private ArticleFeign articleFeign;

    @RabbitListener(queuesToDeclare = @Queue(value = NewsUpOrDownConstants.NEWS_UP_FOR_ES_QUEUE))
    public void listenNewsUpMsg(String articleId) {
        log.info("接收到文章上架消息，消息内容：{}", articleId);

//        根据articleId查询文章信息，将文章信息 添加到es
        ResponseResult<SearchArticleVO> result = articleFeign.findArticle(Long.valueOf(articleId));
        if (result.checkCode()) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "未查询到对应的文章数据");
        }

        articleSearchService.saveArticle(result.getData());
    }

    @RabbitListener(queuesToDeclare = @Queue(value = NewsUpOrDownConstants.NEWS_DOWN_FOR_ES_QUEUE))
    public void listenNewsDownMsg(String articleId) {
        log.info("收到文章下架消息，消息内容：{}", articleId);

        articleSearchService.deleteArticle(articleId);
    }
}
