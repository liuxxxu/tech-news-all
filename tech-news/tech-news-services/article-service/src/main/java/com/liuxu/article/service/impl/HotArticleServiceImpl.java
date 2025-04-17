package com.liuxu.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuxu.article.mapper.AppArticleMapper;
import com.liuxu.article.service.HotArticleService;
import com.liuxu.common.constants.article.ArticleConstants;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.utils.DateUtils;
import com.liuxu.feigns.AdminFeign;
import com.liuxu.model.admin.pojo.AdChannel;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.model.article.vos.HotArticleVo;
import com.liuxu.model.message.app.AggBehaviorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private AppArticleMapper appArticleMapper;

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 计算热文章
     */
    @Override
    public void computeHotArticle() {
        // 1.查询近5天的文章
        // 1.1 计算5天前的时间
        String param = LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd 00:00:00"));

        // 1.2 使用articleMapper查询文章数据
        List<AppArticle> articleList = appArticleMapper.selectArticleByDate(param);
        if (CollectionUtils.isEmpty(articleList)) {
            log.info("当前头条太冷清，近5天没人发布文章了");
            return;
        }

        // 2. 计算每一篇文章热度得分
        List<HotArticleVo> articleVoList = getHotArticleVoList(articleList);

        // 3. 按照频道 每个频道缓存 热度最高的30条文章
        cacheToRedisByTag(articleVoList);
    }

    /**
     * 更新文章行为数量
     * 
     * @param aggBehavior
     */
    @Override
    public void updateAppArticle(AggBehaviorDTO aggBehavior) {
        // 1. 根据id 查询文章数据
        AppArticle appArticle = appArticleMapper.selectById(aggBehavior.getArticleId());
        if (appArticle == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "对应的文章数据不存在");
        }

        // 2. 将聚合文章行为文章数据 和 文章表中的统计数据 累加到一起
        Integer views = appArticle.getViews() == null ? 0 : appArticle.getViews();
        appArticle.setViews((int) (views + aggBehavior.getView()));

        Integer likes = appArticle.getLikes() == null ? 0 : appArticle.getLikes();
        appArticle.setLikes((int) (likes + aggBehavior.getLike()));

        Integer collections = appArticle.getCollection() == null ? 0 : appArticle.getCollection();
        appArticle.setCollection((int) (collections + aggBehavior.getCollect()));

        Integer comments = appArticle.getComment() == null ? 0 : appArticle.getComment();
        appArticle.setComment((int) (comments + aggBehavior.getComment()));

        appArticleMapper.updateById(appArticle);

        // 3. 重新计算文章热度得分
        Integer score = computeScore(appArticle);

        // 4.如果文章是今天发布的 热度*3
        // 当前时间
        String now = DateUtils.dateToString(new Date());
        // 发布时间
        String publishTime = DateUtils.dateToString(appArticle.getPublishTime());
        if (publishTime.equals(now)) {
            score = score * 3;
        }

        // 5. 更新当前文章所在频道的缓存
        updateAppArticleCache(appArticle, score, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + appArticle.getChannelId());

        // 6. 更新推进频道的缓存
        updateAppArticleCache(appArticle, score,
                ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 更新文章缓存
     * 
     * @param article
     * @param score
     * @param cacheKey
     */
    private void updateAppArticleCache(AppArticle article, Integer score, String cacheKey) {
        // 1. 从redis中查询出对应的热点文章列表
        String hotArticleJSON = redisTemplate.opsForValue().get(cacheKey);
        List<HotArticleVo> articleVoList = JSON.parseArray(hotArticleJSON, HotArticleVo.class);

        // 2. 判断当前文章是否存在热点文章列表中
        boolean isHas = false;
        for (HotArticleVo hotArticleVo : articleVoList) {
            if (hotArticleVo.getId().equals(article.getId())) {
                // 3. 如果存在 直接更新文章score热度值
                hotArticleVo.setScore(score);
                isHas = true;
                break;
            }
        }

        // 4. 如果不存在 直接将当前文章加入到热点文章列表
        if (!isHas) {
            HotArticleVo articleVo = new HotArticleVo();
            BeanUtils.copyProperties(article, articleVo);
            articleVo.setScore(score);
            articleVoList.add(articleVo);
        }

        // 5. 重新将热点文章列表按照热度降序排序 截取前30条文章
        articleVoList = articleVoList.stream()
                .sorted(Comparator.comparing(HotArticleVo::getScore).reversed())
                .limit(30)
                .collect(Collectors.toList());

        // 6. 将文章集合重新存入redis中
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(articleVoList));
    }

    /**
     * 按照频道缓存热度最高的30条文章
     * 
     * @param articleVoList
     */
    private void cacheToRedisByTag(List<HotArticleVo> articleVoList) {
        // 1. 远程查询频道列表
        ResponseResult<List<AdChannel>> result = adminFeign.selectChannels();
        if (result.checkCode()) {
            CustException.throwException(AppHttpCodeEnum.REMOTE_SERVER_ERROR, result.getErrorMessage());
        }
        List<AdChannel> channelList = result.getData();

        // 2. 遍历频道列表，从文章列表中筛选每个频道对应的文章
        for (AdChannel channel : channelList) {
            List<HotArticleVo> hotArticleVoList = articleVoList.stream()
                    .filter(articleVo -> articleVo.getChannelId().equals(channel.getId()))
                    .collect(Collectors.toList());
            sortAndCache(hotArticleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + channel.getId());
        }

        // 3. 缓存推荐频道的文章
        sortAndCache(articleVoList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并缓存文章
     * 
     * @param hotArticleVoList
     * @param cacheKey
     */
    private void sortAndCache(List<HotArticleVo> hotArticleVoList, String cacheKey) {
        // 1. 按照热度降序排序 截取前30条文章
        hotArticleVoList = hotArticleVoList.stream()
                .sorted(Comparator.comparing(HotArticleVo::getScore).reversed())
                .limit(30)
                .collect(Collectors.toList());

        // 2. 缓存到redis中
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(hotArticleVoList), 24, TimeUnit.HOURS);
    }

    /**
     * 计算文章列表的热度得分
     * 
     * @param articleList
     * @return
     */
    private List<HotArticleVo> getHotArticleVoList(List<AppArticle> articleList) {
        return articleList.stream().map(article -> {
            HotArticleVo hot = new HotArticleVo();
            BeanUtils.copyProperties(article, hot);
            Integer score = computeScore(article);
            hot.setScore(score);
            return hot;
        }).collect(Collectors.toList());
    }

    /**
     * 计算文章分值
     * 
     * @param article
     * @return
     */
    private Integer computeScore(AppArticle article) {
        Integer score = 0;
        if (article.getLikes() != null) {
            score += article.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if (article.getViews() != null) {
            score += article.getViews();
        }
        if (article.getComment() != null) {
            score += article.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if (article.getCollection() != null) {
            score += article.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        return score;
    }
}
