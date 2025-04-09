package com.liuxu.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuxu.comment.service.CommentService;
import com.liuxu.common.constants.article.HotArticleConstants;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.exception.CustException;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.feigns.UserFeign;
import com.liuxu.model.article.pojos.AppArticle;
import com.liuxu.model.comment.dtos.CommentDTO;
import com.liuxu.model.comment.dtos.CommentLikeDTO;
import com.liuxu.model.comment.dtos.CommentSaveDTO;
import com.liuxu.model.comment.pojos.AppComment;
import com.liuxu.model.comment.pojos.AppCommentLike;
import com.liuxu.model.comment.vos.AppCommentVo;
import com.liuxu.model.message.app.NewBehaviorDTO;
import com.liuxu.model.user.pojos.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 保存评论
     * 
     * @return
     */
    @Override
    public ResponseResult saveComment(CommentSaveDTO dto) {
        // 1.校验参数
        // 校验是否登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 2.校验文章id 校验内容不为空 校验内容长度不能大于140个字符 (validated校验)
        dto.getArticleId();

        // 阿里云校验评论内容是否违规(简单: 此步不做 普通: 阿里云检测有问题提示 高级: 有问题不报异常，替换为**)
        // try {
        //     ScanResult scanResult = greenScan.greenTextScan(dto.getContent());
        //     switch (scanResult.getSuggestion()) {
        //         case "block":
        //             // 失败
        //             CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "评论非法");
        //             break;
        //         case "review":
        //             // 人工
        //
        //             break;
        //         case "pass":
        //             // 成功
        //             break;
        //         default:
        //             // 人工
        //             break;
        //     }
        // } catch (Exception e) {
        //     CustException.throwException(AppHttpCodeEnum.SERVER_ERROR, "远程调用阿里云内容服务失败");
        // }

        // 3. 远程查询当前登陆用户信息
        AppUser appUser = userFeign.findUserById(userId).getData();
        if (appUser == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "当前用户信息不正确");
        }

        // 4. 创建评论信息，并保存到mongo
        AppComment appComment = new AppComment();
        appComment.setAuthorId(appUser.getId());
        appComment.setAuthorName(appUser.getName());
        appComment.setArticleId(dto.getArticleId());
        appComment.setType((short) 0);
        appComment.setChannelId(0L);
        appComment.setContent(dto.getContent());
        appComment.setImage(appComment.getImage());
        appComment.setLikes(0);
        appComment.setReply(0);
        appComment.setFlag((short) 0);
        appComment.setLongitude(new BigDecimal("0"));
        appComment.setLatitude(new BigDecimal("0"));
        appComment.setAddress("");
        appComment.setOrd(0);
        appComment.setCreatedTime(new Date());
        appComment.setUpdatedTime(new Date());

        mongoTemplate.save(appComment);

        // 发送行为消息
        NewBehaviorDTO newBehaviorDTO = new NewBehaviorDTO();
        newBehaviorDTO.setType(NewBehaviorDTO.BehaviorType.COMMENT);
        newBehaviorDTO.setArticleId(dto.getArticleId());
        newBehaviorDTO.setAdd(1);

        rabbitTemplate.convertAndSend(HotArticleConstants.HOT_ARTICLE_SCORE_BEHAVIOR_QUEUE,
                JSON.toJSONString(newBehaviorDTO));
        log.info("发送成功 文章评论行为消息，消息内容：{}", newBehaviorDTO);

        return ResponseResult.successResult();
    }

    /**
     * 点赞评论
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(CommentLikeDTO dto) {
        // 1. 参数校验 评论id不能为空 operation必须为 0 或 1
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN, "登录才能点赞");
        }

        // 2. 根据评论id查询评论数据， 为null返回错误信息
        Query queryComment = Query.query(Criteria.where("id").is(dto.getCommentId()));
        AppComment comment = mongoTemplate.findOne(queryComment, AppComment.class);
        if (comment == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "评论数据不存在");
        }

        // 3. 如果是点赞操作 判断是否已经点赞
        RLock lock = redissonClient.getLock("likes-lock");
        lock.lock();
        try {
            Query queryCommentLike = Query.query(Criteria.where("commentId").is(dto.getCommentId())
                    .and("authorId").is(userId));
            if (dto.getOperation() == 0) { // 点赞
                AppCommentLike commentLike = mongoTemplate.findOne(queryCommentLike, AppCommentLike.class);

                // 点过赞提示 请勿重复点赞
                if (commentLike != null) {
                    CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "请勿重复点赞");
                }

                // 未点过赞 保存点赞信息到mongo
                commentLike = new AppCommentLike();
                commentLike.setAuthorId(userId);
                commentLike.setCommentId(dto.getCommentId());
                commentLike.setOperation(dto.getOperation());
                mongoTemplate.save(commentLike);

                // 并修改评论信息的点赞数量( + 1)
                comment.setLikes(comment.getLikes() + 1);
                mongoTemplate.save(comment);
            } else { // 取消点赞
                // 4. 如果是取消点赞操作
                // 删除点赞信息
                mongoTemplate.remove(queryCommentLike);

                // 并修改评论信息的点赞数量( - 1) , 要判断下别减成负数
                if (comment.getLikes() > 0) {
                    comment.setLikes(comment.getLikes() - 1);
                    mongoTemplate.save(comment);
                }
            }

        } finally {
            lock.unlock();
        }

        // 返回结果时，需要返回点赞点赞数量 返回的key要求必须是: likes
        HashMap<String, Object> map = new HashMap<>();
        map.put("likes", comment.getLikes());

        return ResponseResult.successResult(map);
    }

    /**
     * 根据文章id查询评论列表
     * 
     * @return
     */
    @Override
    public ResponseResult findByArticleId(CommentDTO dto) {
        // 1. 参数校验
        // 校验文章id
        Query queryArticle = Query.query(Criteria.where("id").is(dto.getArticleId()));
        AppArticle article = mongoTemplate.findOne(queryArticle, AppArticle.class);
        if (article == null) {
            CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "文章不存在");
        }

        // 校验size
        Integer size = dto.getSize();
        if (size == null || size <= 0) {
            size = 10;
        }

        // 2. 查询Mongo文章所有评论列表
        List<AppComment> appCommentList = null;

        // 文章评论的首页
        if (dto.getIndex().intValue() == 1) {
            Query query = Query.query(
                    Criteria.where("articleId").is(dto.getArticleId())
                            .and("flag").is(1))
                    .with(Sort.by(Sort.Direction.DESC, "likes"));
            appCommentList = mongoTemplate.find(query, AppComment.class);

            // 热点集合不为空
            if (CollectionUtils.isEmpty(appCommentList)) {
                appCommentList = mongoTemplate.find(getQuery(dto, size), AppComment.class);
            } else {
                size = size - appCommentList.size();
                List<AppComment> normalComments = mongoTemplate.find(getQuery(dto, size), AppComment.class);
                appCommentList.addAll(normalComments);
            }
        }

        // 3. 封装查询结果
        Long userId = AppThreadLocalUtils.getUserId();
        // 3.1 用户未登录 直接放回评论列表
        if (userId == null) {
            return ResponseResult.successResult(appCommentList);
        }

        // 3.2 用户登录 需要加载当前用户对评论点赞的列表
        // 用户文章对应的所有评论ID列表
        List<String> commonIds = appCommentList.stream().map(AppComment::getId).collect(Collectors.toList());

        // 查询 点赞批量列表 按照评论id筛选
        List<AppCommentLike> appCommentLikes = mongoTemplate.find(Query.query(
                Criteria.where("commentId").in(commonIds)
                        .and("authorId").is(userId)),
                AppCommentLike.class);

        // 遍历当前用户点赞列表和当前评论列表
        if (CollectionUtils.isEmpty(appCommentList) || CollectionUtils.isEmpty(appCommentLikes)) {
            return ResponseResult.successResult(appCommentList);
        }

        // 获取点赞过的评论id
        List<String> commentIds = appCommentLikes.stream().map(AppCommentLike::getCommentId)
                .collect(Collectors.toList());

        // 遍历评论列表，将comment转为commentVo
        return ResponseResult.successResult(appCommentList.stream().map(comm -> parseCommentVo(comm, commentIds)));
    }

    /**
     * 构建查询条件
     * 
     * @param dto
     * @param size
     * @return
     */
    private Query getQuery(CommentDTO dto, Integer size) {
        return Query.query(Criteria.where("articleId").is(dto.getArticleId())
                .and("flag").is(0)
                .and("createdTime").lt(dto.getMinDate()))
                .limit(size)
                .with(Sort.by(Sort.Direction.DESC, "createdTime"));
    }

    /**
     * 将comment 转为 vo对象 根据likes情况判断是否点过赞
     */
    private AppCommentVo parseCommentVo(AppComment appComment, List<String> commentIds) {
        AppCommentVo appCommentVo = new AppCommentVo();
        BeanUtils.copyProperties(appCommentVo, appCommentVo);

        // 遍历当前用户点赞列表
        if (commentIds.contains(appCommentVo.getId())) {
            appCommentVo.setOperation((short) 0);
        }
        return appCommentVo;
    }
}
