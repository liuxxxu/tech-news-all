package com.liuxu.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.article.mapper.AppArticleConfigMapper;
import com.liuxu.article.mapper.AppArticleContentMapper;
import com.liuxu.article.mapper.AppArticleMapper;
import com.liuxu.article.mapper.AuthorMapper;
import com.liuxu.article.service.AppArticleService;
import com.liuxu.common.constants.article.ArticleConstants;
import com.liuxu.common.constants.message.NewsUpOrDownConstants;
import com.liuxu.common.exception.CustException;
import com.liuxu.feigns.AdminFeign;
import com.liuxu.feigns.WemediaFeign;
import com.liuxu.model.admin.pojo.AdChannel;
import com.liuxu.model.article.dtos.ArticleHomeDTO;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.model.article.pojos.AppArticleConfig;
import com.liuxu.model.article.pojos.AppArticleContent;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.wemedia.pojos.WmNews;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AppArticleServiceImpl extends ServiceImpl<AppArticleMapper, AppArticle> implements AppArticleService {

    @Autowired
    private WemediaFeign wemediaFeign;

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private AppArticleConfigMapper appArticleConfigMapper;

    @Autowired
    private AppArticleContentMapper appArticleContentMapper;

    @Autowired
    private AppArticleMapper appArticleMapper;

    @Value("${minio.endpoint}")
    private String endpoint;


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 保存或修改文章
     *
     * @param newsId 文章id
     * @return
     */
    @Override
    // @GlobalTransactional(rollbackFor = Exception.class, timeoutMills = 3000000)
    public void publishArticle(Long newsId) {
        // 1.根据id查询并校验自媒体文章
        WmNews wmNews = getWmNews(newsId);

        // 2. 根据wmNews封装 appArticle对象
        AppArticle appArticle = getAppArticle(wmNews);

        // 3.保存或修改 appArticle
        saveOrUpdateArticle(appArticle);

        // 4.保存关联的配置和内容信息
        saveConfigAndContent(wmNews, appArticle);

        // 5. 更新wmNews状态为9
        updateWmNews(wmNews, appArticle);

        // 6. 通过es 更新索引库
        rabbitTemplate.convertAndSend(NewsUpOrDownConstants.NEWS_UP_FOR_ES_QUEUE, appArticle.getId());
        log.info("文章发布成功，并通知搜素微服务更新索引信息 {}", appArticle.getId());
    }

    /**
     * 根据参数加载文章列表
     *
     * @param loadType 0为加载更多 1为加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<List<AppArticle>> load(Short loadType, ArticleHomeDTO dto) {
        // 1. 检查参数：（分页 时间 类型 频道）
        Integer size = dto.getSize();
        if (size == null || size <= 0) { // 页大小
            dto.setSize(10);
        }
        // 时间
        if (dto.getMaxTime() == null) {
            dto.setMaxTime(new Date());
        }

        if (dto.getMinTime() == null) {
            dto.setMinTime(new Date());
        }

        // 频道
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 类型判断
        if (!loadType.equals(ArticleConstants.LOADTYPE_LOAD_NEW)
                && !loadType.equals(ArticleConstants.LOADTYPE_LOAD_MORE)) {
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

        // 2.调用mapper查询
        List<AppArticle> articleList = appArticleMapper.loadArticleList(dto, loadType);

        // 3 返回结果
        return ResponseResult.successResult(articleList);
    }

    /**
     * 根据参数加载文章列表
     *
     * @param loadType  0为加载更多 1为加载最新
     * @param dto
     * @param firstPage 是否为首页 true 首页
     * @return
     */
    @Override
    public ResponseResult<List<AppArticle>> loadNew(Short loadType, ArticleHomeDTO dto, boolean firstPage) {
        if (firstPage) {
            // 1. 从redis缓存中 查询热点文章数据
            String articleListJSON = (String) redisTemplate.opsForValue()
                    .get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StringUtils.isNotBlank(articleListJSON)) {
                List<AppArticle> apArticles = JSON.parseArray(articleListJSON, AppArticle.class);
                ResponseResult<List<AppArticle>> result = ResponseResult.successResult(apArticles);
                return result;
            }
        }
        return load(loadType, dto);
    }

    /**
     * 给图片路径加上前缀
     *
     * @param article
     */
    private void parseArticle(AppArticle article) {
        String images = article.getCover();
        if (StringUtils.isNotBlank(images)) {
            images = Arrays.stream(images.split(","))
                    // 每一个路径添加前缀
                    .map(item -> endpoint + item)
                    // 将加了前缀的路径 拼接成字符串
                    .collect(Collectors.joining(","));
            article.setCover(images);
        }

        // 给静态页路径添加访问前缀
        // article.setStaticUrl(readPath + article.getStaticUrl());
    }

    /**
     * 6. 更新wmNews状态为9
     *
     * @param wmNews
     * @param appArticle
     */
    private void updateWmNews(WmNews wmNews, AppArticle appArticle) {
        // 将文章改为发布状态
        wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());

        // 关联articleId
        wmNews.setArticleId(appArticle.getId());

        // 远程调用
        ResponseResult responseResult = wemediaFeign.updateWmNews(wmNews);
        if (responseResult.checkCode()) {
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, responseResult.getErrorMessage());
        }
    }

    /**
     * 4.保存关联的配置和内容信息
     *
     * @param wmNews
     * @param appArticle
     */
    private void saveConfigAndContent(WmNews wmNews, AppArticle appArticle) {
        // 1. 保存配置
        AppArticleConfig config = new AppArticleConfig();
        config.setArticleId(appArticle.getId());

        // 是否允许评论
        config.setIsComment(true);
        // 是否允许转发
        config.setIsForward(true);
        // 是否 下架
        config.setIsDown(false);
        // 是否 删除
        config.setIsDelete(false);
        appArticleConfigMapper.insert(config);

        // 2. 保存文章详情
        AppArticleContent content = new AppArticleContent();
        content.setArticleId(appArticle.getId());
        content.setContent(wmNews.getContent());
        content.setSummary(wmNews.getSummary());
        appArticleContentMapper.insert(content);
    }

    /**
     * 3.保存或修改文章 appArticle
     *
     * @param appArticle
     */
    private void saveOrUpdateArticle(AppArticle appArticle) {
        // 1.判断文章id是否存在
        if (appArticle.getId() == null) {
            // 2 如果不存在 保存文章
            appArticle.setLikes(0);
            appArticle.setViews(0);
            appArticle.setComment(0);
            appArticle.setCollection(0);
            save(appArticle);
        } else {
            // 3.如果存在 修改文章
            // 3.1 查询之前的article是否存在
            AppArticle oldArticle = getById(appArticle.getId());
            if (oldArticle == null) {
                CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "关联的文章不存在");
            }

            // 3.2 修改文章
            updateById(appArticle);

            // 3.3 删除之前所关联的 config 和content
            appArticleConfigMapper.delete(
                    Wrappers.<AppArticleConfig>lambdaQuery().eq(AppArticleConfig::getArticleId, appArticle.getId()));
            appArticleContentMapper.delete(
                    Wrappers.<AppArticleContent>lambdaQuery().eq(AppArticleContent::getArticleId, appArticle.getId()));
        }

    }

    /**
     * 2.根据wmNews封装 appArticle对象
     *
     * @param wmNews
     * @return
     */
    private AppArticle getAppArticle(WmNews wmNews) {
        // 1. 创建article对象
        AppArticle appArticle = new AppArticle();

        // 2.拷贝属性 wmNews==>article
        BeanUtils.copyProperties(wmNews, appArticle);

        // 3.拷贝其他属性 id flag layout
        appArticle.setId(wmNews.getArticleId());
        appArticle.setFlag((byte) 0);
        appArticle.setLayout(wmNews.getType());

        // 4. 频道信息
        ResponseResult<AdChannel> channelResult = adminFeign.findChannelById(wmNews.getChannelId());
        if (channelResult.checkCode()) {
            log.error("远程调用查询接口失败:{}", channelResult.getErrorMessage());
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, channelResult.getErrorMessage());
        }
        AdChannel channel = channelResult.getData();
        if (channel == null) {
            log.error("远程查询频道信息为null  频道id:{}", wmNews.getChannelId());
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "频道不存在");
        }
        appArticle.setChannelName(channel.getName());

        // 5 作者信息
        AppAuthor appAuthor = authorMapper
                .selectOne(Wrappers.<AppAuthor>lambdaQuery().eq(AppAuthor::getWmUserId, wmNews.getUserId()));
        if (appAuthor == null) {
            log.error("关联的作者信息不存在 wmUserId:{}", wmNews.getUserId());
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "关联的作者信息不存在");
        }
        appArticle.setAuthorId(appAuthor.getId());
        appArticle.setAuthorName(appAuthor.getName());

        return appArticle;
    }

    /**
     * 1.根据id查询并校验自媒体文章
     *
     * @param newsId
     * @return
     */
    private WmNews getWmNews(Long newsId) {
        // 1. 根据id远程查询自媒体文章
        ResponseResult<WmNews> result = wemediaFeign.findWmNewsById(newsId);

        // 2. 检查远程调用状态
        if (result.checkCode()) {
            log.error("远程调用自媒体文章信息失败 文章id:{}", newsId);
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }

        // 3.判断news是否为null
        WmNews wmNews = result.getData();
        if (wmNews == null) {
            log.error("远程调用自媒体文章信息失败 文章id:{}", newsId);
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "对于的文章信息不存在 文章id:" + newsId);
        }

        // 4. 检查news是否为 4 或 8
        short status = wmNews.getStatus().shortValue();
        if (WmNews.Status.ADMIN_SUCCESS.getCode() != status && WmNews.Status.SUCCESS.getCode() != status) {
            log.error("对应的文章状态不为 审核通过,无法发布, 文章id:{}", newsId);
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "对应的文章状态不为 审核通过,无法发布, 文章id:" + newsId);
        }
        return wmNews;
    }
}
