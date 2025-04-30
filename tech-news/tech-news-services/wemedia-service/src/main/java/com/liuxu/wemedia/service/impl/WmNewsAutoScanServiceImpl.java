package com.liuxu.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liuxu.common.constants.message.PublishArticleConstants;
import com.liuxu.common.constants.wemedia.WemediaConstants;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.feigns.AdminFeign;
import com.liuxu.model.wemedia.pojos.WmNews;
import com.liuxu.wemedia.mapper.WmNewsMapper;
import com.liuxu.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RefreshScope
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${coze.access-key}")
    private String accessKey;

    @Value("${coze.bot-id}")
    private String botId;

    @Value("${coze.base-url}")
    private String baseUrl;

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 自媒体文章审核
     *
     * @param id 自媒体文章id
     */
    @Override
    public void autoScanWmNews(Long id) {
        log.info("文章自动审核触发，待审核的文章id：{}", id);
        // 1. 判断文章id是否为空
        if (id == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_REQUIRE, "待审核的文章id为空");
        }

        // 2. 根据id查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }

        // 3.判断文章的状态 必须是1（待审核） 避免重复消费
        Short status = wmNews.getStatus();
        if (!WemediaConstants.WM_NEWS_SUMMIT_STATUS.equals(status)) {
            log.info("当前文章状态为：{} 不是待审核状态，无需审核", status);
            return;
        }

        // 6. AI审核
        boolean pass = handleAIScan(wmNews);
        if (!pass) {
            // 返回false代表审核不成功 结束处理
            log.info("文章AI审核未通过, id：{}", id);
            return;
        }

        // 7. 根据文章的发布时间，发送延迟消息，用于定时发布文章
        // 获取发布时间
        Date publishTime = wmNews.getPublishTime();
        long nowTime = System.currentTimeMillis();
        long publishTimeValue;
        // 没设置定时发布时间，默认发布时间为当前时间
        if (publishTime == null) {
            wmNews.setPublishTime(new Date());
            publishTimeValue = nowTime;
        } else {
            publishTimeValue = publishTime.getTime();
        }
        // 发布时间 - 当前时间 = 距离发布的延迟时间
        long remainTime = publishTimeValue - nowTime;

        // 使用rabbitmq发送延迟消息
        rabbitTemplate.convertAndSend(
                PublishArticleConstants.DELAY_DIRECT_EXCHANGE,
                PublishArticleConstants.PUBLISH_ARTICLE_ROUTE_KEY,
                wmNews.getId(),
                new MessagePostProcessor() {
                    @Override
                    public Message postProcessMessage(Message message) throws AmqpException {
                        MessageProperties messageProperties = message.getMessageProperties();
                        // 设置消息头
                        messageProperties.setHeader("x-delay", remainTime <= 0 ? 0 : remainTime);
                        return message;
                    }
                });
        log.info("发送文章发布消息，文章id:{}，当前时间:{}，定时时间:{}", wmNews.getId(), LocalDateTime.now(), publishTime);
    }

    /**
     * 6. AI文本审核
     *
     * @param wmNews
     * @return
     */
    private boolean handleAIScan(WmNews wmNews) {
        boolean flag = false;
        JSONObject scanResult = null;
        try {
            scanResult = aiScan(wmNews.getContent());
            // AI建议: No 违规，Help 进一步人工审核，Yes 通过
            String suggestion = scanResult.getString("pass");
            switch (suggestion) {
                case "No":
                    // 失败
                    updateWmNews(WmNews.Status.FAIL.getCode(), scanResult.getString("reason"), wmNews);
                    break;
                case "Help":
                    // 人工
                    updateWmNews(WmNews.Status.ADMIN_AUTH.getCode(), "AI审核建议进一步人工审核", wmNews);
                    break;
                case "Yes":
                    // 成功
                    flag = true;
                    String summary = scanResult.getString("summary");
                    // 进行编码
                    summary = new String(summary.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    updateWmNews(WmNews.Status.SUCCESS.getCode(), "AI审核通过", summary, wmNews);
                    break;
                default:
                    // 人工
                    updateWmNews(WmNews.Status.ADMIN_AUTH.getCode(), "AI审核失败，转人工审核", wmNews);
                    break;
            }
        } catch (Exception e) {
            log.error("AI审核异常, 文章id:{}", wmNews.getId(), e);
            // 人工
            updateWmNews(WmNews.Status.ADMIN_AUTH.getCode(), "AI审核失败，转人工审核", wmNews);
        }
        return flag;
    }

    private JSONObject aiScan(String content) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessKey);

        Map<String, Object> additionalMessage = new HashMap<>();
        additionalMessage.put("content", content);
        additionalMessage.put("content_type", "text");
        additionalMessage.put("role", "user");
        additionalMessage.put("type", "question");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("bot_id", botId);
        requestBody.put("user_id", "tech-news");
        requestBody.put("stream", true);
        requestBody.put("additional_messages", List.of(additionalMessage));
        requestBody.put("auto_save_history", true);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl, requestEntity, String.class);
        String response = responseEntity.getBody();
        // 解析事件流响应
        JSONObject answerJson = null;
        if (response != null) {
            String[] events = response.split("\\n\\n");
            // 从后往前遍历
            for (int i = events.length - 1; i >= 0; i--) {
                String event = events[i];
                if (event.startsWith("event:conversation.message.completed")) {
                    String data = event.substring(event.indexOf("data:"));
                    JSONObject jsonData = JSON.parseObject(data.substring(5));
                    if ("answer".equals(jsonData.getString("type"))) {
                        String answerContent = jsonData.getString("content");
                        answerJson = JSON.parseObject(answerContent);
                        break;
                    }
                }
            }
        }
        return answerJson;
    }

    /**
     * 修改文章状态
     *
     * @param status
     * @param reason
     * @param wmNews
     */
    private void updateWmNews(Short status, String reason, String summary, WmNews wmNews) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNews.setSummary(summary);
        wmNewsMapper.updateById(wmNews);
    }

    private void updateWmNews(Short status, String reason, WmNews wmNews) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }
}
