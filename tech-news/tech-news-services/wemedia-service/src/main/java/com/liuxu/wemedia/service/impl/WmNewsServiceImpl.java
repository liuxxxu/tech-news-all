package com.liuxu.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxu.common.constants.message.NewsAutoScanConstants;
import com.liuxu.common.constants.message.NewsUpOrDownConstants;
import com.liuxu.common.constants.message.PublishArticleConstants;
import com.liuxu.common.constants.wemedia.WemediaConstants;
import com.liuxu.common.dtos.PageResponseResult;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.wemedia.dtos.NewsAuthDTO;
import com.liuxu.model.wemedia.dtos.WmNewsDTO;
import com.liuxu.model.wemedia.dtos.WmNewsPageReqDTO;
import com.liuxu.model.wemedia.pojos.WmNews;
import com.liuxu.model.wemedia.pojos.WmNewsMaterial;
import com.liuxu.model.wemedia.pojos.WmUser;
import com.liuxu.model.wemedia.vos.WmNewsVO;
import com.liuxu.wemedia.mapper.WmMaterialMapper;
import com.liuxu.wemedia.mapper.WmNewsMapper;
import com.liuxu.wemedia.mapper.WmNewsMaterialMapper;
import com.liuxu.wemedia.mapper.WmUserMapper;
import com.liuxu.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    /**
     * 查询所有自媒体文章
     * 
     * @return
     */
    @Override
    public ResponseResult findByUser(WmNewsPageReqDTO dto) {
        // 1. 参数检查
        if (dto == null) {
            return ResponseResult.successResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        dto.checkParam();

        // 2. 条件封装执行查询
        LambdaQueryWrapper<WmNews> query = getWmNewsLambdaQueryWrapper(dto);

        // 按照登录用户ID去查询
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }
        query.eq(WmNews::getUserId, userId);

        // 分页查询，返回结果
        Page<WmNews> wmNewsPage = new Page<>(dto.getPage(), dto.getSize());
        // 3.执行查询
        IPage<WmNews> page = page(wmNewsPage, query);
        // 4.封装查询结果
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), page.getTotal(),
                page.getRecords());
        return pageResponseResult;
    }

    @Override
    public ResponseResult listByStatus(WmNewsPageReqDTO dto) {
        // 1. 参数检查
        if (dto == null) {
            return ResponseResult.successResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        dto.checkParam();

        // 2. 条件封装执行查询
        LambdaQueryWrapper<WmNews> query = getWmNewsLambdaQueryWrapper(dto);
        query.ne(WmNews::getStatus, 0);
        // 分页查询，返回结果
        Page<WmNews> wmNewsPage = new Page<>(dto.getPage(), dto.getSize());
        // 3.执行查询
        IPage<WmNews> page = page(wmNewsPage, query);
        // 4.封装查询结果
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), page.getTotal(),
                page.getRecords());
        return pageResponseResult;
    }

    @NotNull
    private static LambdaQueryWrapper<WmNews> getWmNewsLambdaQueryWrapper(WmNewsPageReqDTO dto) {
        LambdaQueryWrapper<WmNews> query = Wrappers.lambdaQuery();
        // 如果有文章标题，按照文章标题模糊查询
        query.like(StringUtils.isNotBlank(dto.getKeyword()), WmNews::getTitle, dto.getKeyword());
        // 如果有频道信息，按照频道ID查询
        query.eq(dto.getChannelId() != null, WmNews::getChannelId, dto.getChannelId());
        // 如果有文章状态，按照状态信息进行查询
        query.eq(dto.getStatus() != null, WmNews::getStatus, dto.getStatus());
        // 如果开始时间，结束时间不为空按照时间区间查询
        // 发布时间 >= 开始时间
        query.ge(dto.getBeginPubDate() != null, WmNews::getPublishTime, dto.getBeginPubDate());
        // 发布时间 <= 开始时间
        query.le(dto.getEndPubDate() != null, WmNews::getPublishTime, dto.getEndPubDate());
        query.orderByDesc(WmNews::getCreatedTime);
        return query;
    }

    /**
     * 自媒体文章发布
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<Void> submitNews(WmNewsDTO dto) {
        // 1. 参数校验
        if (StringUtils.isBlank(dto.getContent())) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 检查当前用户是否登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }
        // 获取用户信息
        WmUser wmUser = wmUserMapper.selectById(userId);
        if (wmUser == null) {
            CustException.throwException(AppHttpCodeEnum.USER_NOT_EXIST);
        }

        // 2. 保存或者修改文章
        WmNews wmNews = new WmNews();
        // 将Dto参数里面的值设置到WmNews
        BeanUtils.copyProperties(dto, wmNews);
        // 设置用户ID
        wmNews.setUserId(userId);
        wmNews.setAuthorName(wmUser.getName());

        // 3. 保存
        saveWmNews(wmNews);

        // 如果是提交，发送待审核消息
        rabbitTemplate.convertAndSend(NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_QUEUE, wmNews.getId());
        log.info("成功发送待审核消息,待审核的文章id为:{}", wmNews.getId());
        return ResponseResult.successResult();


        // // 3.1 抽取文章中关联的图片路径
        // List<String> materials = parseContentImages(dto.getContent());
        //
        // // 3.2 关联文章内容中的图片和素材关系
        // if (!CollectionUtils.isEmpty(materials)) {
        //     saveRelativeInfo(materials, wmNews.getId(), WemediaConstants.WM_CONTENT_REFERENCE);
        // }
        //
        // // 3.3 关联文章封面中的图片和素材关系 封面可能是选择自动或者是无图
        // saveRelativeInfoForCover(dto, materials, wmNews);

        // 3.4 发送待审核消息
        // rabbitTemplate.convertAndSend(NewsAutoScanConstants.WM_NEWS_AUTO_SCAN_QUEUE, wmNews.getId());
        // log.info("成功发送待审核消息,待审核的文章id为:{}", wmNews.getId());
        // return ResponseResult.successResult();
    }

    /**
     * 根据文章id查询文章
     * 
     * @return
     */
    @Override
    public ResponseResult findWmNewsById(Long id) {
        // 1. 参数检查
        if (id == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 执行查询
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "当前新闻不存在");
        }

        // 3. 返回结果
        ResponseResult responseResult = ResponseResult.successResult(wmNews);
        return responseResult;
    }

    /**
     * 删除文章
     * 
     * @return
     */
    @Override
    public ResponseResult delNews(Long id) {
        // 1. 检查参数
        if (id == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "文章ID不可缺少");
        }

        // 2. 获取数据
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "当前文章不存在");
        }

        // 3.判断当前文章的状态 status==9 enable == 1
        if (wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())
                && wmNews.getEnable().equals(WemediaConstants.WM_NEWS_UP)) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "文章已发布，不能删除");
        }

        // 4. 去除素材与文章的关系
        LambdaQueryWrapper<WmNewsMaterial> query = Wrappers.<WmNewsMaterial>lambdaQuery();
        query.eq(WmNewsMaterial::getNewsId, wmNews.getId());
        wmNewsMaterialMapper.delete(query);

        // 5. 删除文章
        removeById(wmNews.getId());
        return ResponseResult.successResult();
    }

    /**
     * 上下架
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult downOrUp(WmNewsDTO dto) {
        // 1. 检查参数
        if (dto == null || dto.getId() == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID);
        }
        Short enable = dto.getEnable();
        if (enable == null ||
                (!WemediaConstants.WM_NEWS_UP.equals(enable)
                        &&
                        !WemediaConstants.WM_NEWS_DOWN.equals(enable))) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "上下架状态错误");
        }

        // 2. 查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "当前文章不存在");
        }

        // 3. 判断文章是否发布
        if (!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "当前文章不是发布状态,不能上下架");
        }

        // 4.修改文章状态 同步到APP端
        update(Wrappers.<WmNews>lambdaUpdate().eq(WmNews::getId, wmNews.getId())
                .set(WmNews::getEnable, dto.getEnable()));

        // 5. 上下架发送消息通知 用于同步article 及 elasticsearch
        if (WemediaConstants.WM_NEWS_UP.equals(dto.getEnable())) {
            // 上架
            rabbitTemplate.convertAndSend(
                    NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE,
                    NewsUpOrDownConstants.NEWS_UP_ROUTE_KEY,
                    wmNews.getArticleId());
            log.info("成功发送文章 上架消息 文章id:{}", wmNews.getArticleId());
        } else {
            // 下架
            rabbitTemplate.convertAndSend(
                    NewsUpOrDownConstants.NEWS_UP_OR_DOWN_EXCHANGE,
                    NewsUpOrDownConstants.NEWS_DOWN_ROUTE_KEY,
                    wmNews.getArticleId());
            log.info("成功发送文章 下架消息 文章id:{}", wmNews.getArticleId());
        }

        return ResponseResult.successResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 查询文章列表
     * 
     * @param dto
     * @return
     */
    @Override
    public PageResponseResult findList(NewsAuthDTO dto) {
        // 1. 检查参数
        dto.checkParam();

        // 2. 记录当前页
        Integer currentPage = dto.getPage();

        // 3. 设置起始页 （当前页码-1）* 每页条数
        dto.setPage((dto.getPage() - 1) * dto.getSize());
        if (StringUtils.isNotBlank(dto.getTitle())) {
            dto.setTitle("%" + dto.getTitle() + "%");
        }

        // 4. 分页查询
        List<WmNewsVO> wmNewsVOList = wmNewsMapper.findListAndPage(dto);
        // 统计总共有多少条数据
        long count = wmNewsMapper.findListCount(dto);

        // 5.结果返回
        PageResponseResult result = new PageResponseResult(currentPage, dto.getSize(), count, wmNewsVOList);
        return result;
    }

    /**
     * 查询文章详情
     * 
     * @param id
     * @return
     */
    @Override
    public ResponseResult findWmNewsVo(Long id) {
        // 1. 参数检查
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 查询文章信息
        WmNews wmNews = getById(id);
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章信息不存在");
        }

        // 3. 查询作者
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章作者不存在");
        }

        // 4. 封装vo信息返回
        WmNewsVO wmNewsVO = new WmNewsVO();
        BeanUtils.copyProperties(wmNews, wmNewsVO);
        if (wmUser != null) {
            wmNewsVO.setAuthorName(wmUser.getName());
        }

        ResponseResult responseResult = ResponseResult.successResult(wmNewsVO);
        return responseResult;
    }

    /**
     * 自媒体文章人工审核
     * 
     * @param status 2 审核失败 4 审核成功
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateStatus(Short status, NewsAuthDTO dto) {
        // 1. 参数检查
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 查询文章
        WmNews wmNews = getById(dto.getId());
        if (wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 检查文章状态 不能为9 已发布
        wmNews.setStatus(status);
        if (StringUtils.isNotBlank(dto.getMsg())) {
            wmNews.setReason(dto.getMsg());
        }
        updateById(wmNews);

        // 发布文章
        if (status == WmNews.Status.ADMIN_SUCCESS.getCode()) {
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
            log.info("发送文章发布消息  文章id:{}  当前时间:{}", wmNews.getId(), LocalDateTime.now());
        }

        return ResponseResult.successResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 【3.3】 关联文章封面中的图片和素材关系
     * 
     * @param dto       前端用户选择封面信息数据
     * @param materials 从内容中解析的图片列表
     * @param wmNews    文章ID
     */
    // private void saveRelativeInfoForCover(WmNewsDTO dto, List<String> materials, WmNews wmNews) {
    //     // 获取前端用户选择的图片
    //     List<String> images = dto.getCover();
    //
    //     // 自动获取封面
    //     if (WemediaConstants.WM_NEWS_TYPE_AUTO.equals(dto.getType())) {
    //         int materialSize = materials.size();
    //
    //         // 单图
    //         if (materialSize > 0 && materialSize <= 2) {
    //             images = materials.stream().limit(1).collect(Collectors.toList());
    //             wmNews.setType(WemediaConstants.WM_NEWS_HAVE_COVER);
    //         } else if (materialSize > 2) {
    //             // 多图
    //             images = materials.stream().limit(3).collect(Collectors.toList());
    //             wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
    //         } else {
    //             // 无图
    //             wmNews.setType(WemediaConstants.WM_NEWS_NO_COVER);
    //         }
    //
    //         if (images != null && images.size() > 0) {
    //             // 将图片集合 转换成字符串
    //             wmNews.setImages(imageListToStr(images, endpoint));
    //         }
    //
    //         // 更新
    //         updateById(wmNews);
    //     }
    //
    //     // 保存图片列表和素材的关系
    //     if (images != null && images.size() > 0) {
    //         images = images.stream().map(
    //                 img -> img.replace(endpoint, "").replace(" ", ""))
    //                 .collect(Collectors.toList());
    //         saveRelativeInfo(images, wmNews.getId(), WemediaConstants.WM_IMAGE_REFERENCE);
    //     }
    // }

    /**
     * 3.2 关联文章内容中的图片和素材关系
     * 
     * @param urls   素材列表
     * @param newsId 文章ID
     * @param type   类型 0：内容素材 1：封面素材
     */
    private void saveRelativeInfo(List<String> urls, Long newsId, Short type) {
        // 1. 查询文章内容中的图片对于的素材ID
        Long userId = AppThreadLocalUtils.getUserId();
        List<Integer> relationsIds = wmMaterialMapper.selectRelationsIds(urls, userId);

        // 2. 判断素材是否缺失
        if (CollectionUtils.isEmpty(relationsIds) || relationsIds.size() < urls.size()) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "相关素材缺失,保存文章失败");
        }

        // 3. 保存素材关系
        wmNewsMaterialMapper.saveRelations(relationsIds, newsId, type);
    }

    /**
     * 3.1 抽取文章中关联的图片路径
     * 
     * @param content 文章内容
     * @return
     */
    private List<String> parseContentImages(String content) {
        // 将JSON字符串转换成LIST
        List<Map> contents = JSON.parseArray(content, Map.class);

        // 遍历文章内容 将所有 type为image的 value取出来 去除前缀路径
        return contents.stream()
                // 过滤 type==image的所有集合
                .filter(map -> map.get("type").equals(WemediaConstants.WM_NEWS_TYPE_IMAGE))
                // 获取到image下的value 图片URL
                .map(url -> (String) url.get("value"))
                // 图片URL去除前缀
                .map(url -> url.replace(endpoint, "").replace(" ", ""))
                // 去除重复路径
                .distinct()
                // 转换成list集合
                .collect(Collectors.toList());
    }

    /**
     * 保存或修改文章
     * @param wmNews
     */
    private void saveWmNews(WmNews wmNews) {
        // 上架
        wmNews.setEnable(WemediaConstants.WM_NEWS_UP);
        if (wmNews.getId() == null) {
            wmNews.setCreatedTime(new Date());
            // 保存
            save(wmNews);
        } else {
            // 修改操作
            wmNews.setUpdatedTime(new Date());
            // 先删除当前文章和素材关系表中的数据
            // LambdaQueryWrapper<WmNewsMaterial> wrapper = Wrappers.<WmNewsMaterial>lambdaQuery();
            // wrapper.eq(WmNewsMaterial::getNewsId, wmNews.getId());
            // wmNewsMaterialMapper.delete(wrapper);
            updateById(wmNews);
        }
    }

}
