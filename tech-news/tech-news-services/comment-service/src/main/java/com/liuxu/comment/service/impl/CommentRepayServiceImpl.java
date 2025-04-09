package com.liuxu.comment.service.impl;

import com.liuxu.aliyun.scan.GreenScan;
import com.liuxu.aliyun.scan.ScanResult;
import com.liuxu.comment.service.CommentRepayService;
import com.liuxu.common.exception.CustException;
import com.liuxu.feigns.UserFeign;
import com.liuxu.model.comment.dtos.CommentRepayDTO;
import com.liuxu.model.comment.dtos.CommentRepayLikeDTO;
import com.liuxu.model.comment.dtos.CommentRepaySaveDTO;
import com.liuxu.model.comment.pojos.AppComment;
import com.liuxu.model.comment.pojos.AppCommentRepay;
import com.liuxu.model.comment.pojos.AppCommentRepayLike;
import com.liuxu.model.comment.vos.AppCommentRepayVO;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.common.threadlocal.AppThreadLocalUtils;
import com.liuxu.model.user.pojos.AppUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentRepayServiceImpl implements CommentRepayService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private GreenScan greenScan;

    /**
     * 查看更多回复内容
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadCommentRepay(CommentRepayDTO dto) {
        // 1. 校验参数
        if (dto.getSize() == null || dto.getSize() <= 0) {
            dto.setSize(10);
        }

        if (dto.getMinDate() == null) {
            dto.setMinDate(new Date());
        }

        // 2. 查询评论回复列表 commentId = ? and createdTime < minDate 排序 createdTime desc limit
        // size
        Query query = Query.query(Criteria.where("commentId").is(dto.getCommentId())
                .and("createdTime").lt(dto.getMinDate()))
                .limit(dto.getSize())
                .with(Sort.by(Sort.Direction.DESC, "createdTime"));
        List<AppCommentRepay> appCommentRepays = mongoTemplate.find(query, AppCommentRepay.class);

        // 3.是否登录
        Long userId = AppThreadLocalUtils.getUserId();

        // 3.1 如果未登录 或者 回复的集合是空的 那么直接返回集合
        if (userId == null || appCommentRepays == null || appCommentRepays.size() == 0) {
            return ResponseResult.successResult(appCommentRepays);
        }

        // 3.2 如果登录了 并且集合不为空 判断当前回复列表中 哪些回复过点赞
        List<String> collectRepayIds = appCommentRepays.stream().map(AppCommentRepay::getId)
                .collect(Collectors.toList());

        // 用户id = user.getId commentRepayId in 集合
        Query likedCommentRepay = Query
                .query(Criteria.where("authorId").is(userId).and("commentRepayId").in(collectRepayIds));

        // 得到当前回复列表中，点赞的集合
        List<AppCommentRepayLike> appCommentRepayLikes = mongoTemplate.find(likedCommentRepay,
                AppCommentRepayLike.class);

        if (appCommentRepayLikes != null && appCommentRepayLikes.size() > 0) {
            List<String> likedCommentRepayIds = appCommentRepayLikes.stream()
                    .map(AppCommentRepayLike::getCommentRepayId).collect(Collectors.toList());

            // 说明有点过赞的回复
            ArrayList<AppCommentRepayVO> repayVOArrayList = new ArrayList<>();
            appCommentRepays.forEach(appCommentRepay -> {
                AppCommentRepayVO appCommentRepayVO = new AppCommentRepayVO();
                BeanUtils.copyProperties(appCommentRepay, appCommentRepayVO);
                if (likedCommentRepayIds.contains(appCommentRepay.getId())) {
                    appCommentRepayVO.setOperation((short) 0);
                }
                repayVOArrayList.add(appCommentRepayVO);
            });
            return ResponseResult.successResult(repayVOArrayList);
        }

        return ResponseResult.successResult(appCommentRepays);
    }

    /**
     * 保存回复
     * 
     * @return
     */
    @Override
    public ResponseResult saveCommentRepay(CommentRepaySaveDTO dto) {
        // 1.检查参数
        // 2.判断是否登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            return ResponseResult.successResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 3.安全过滤
        try {
            ScanResult scanResult = greenScan.greenTextScan(dto.getContent());
            switch (scanResult.getSuggestion()) {
                case "block":
                    // 失败
                    CustException.throwException(AppHttpCodeEnum.PARAM_INVALID, "回复非法");
                    break;
                case "review":
                    // 人工

                    break;
                case "pass":
                    // 成功
                    break;
                default:
                    // 人工
                    break;
            }
        } catch (Exception e) {
            CustException.throwException(AppHttpCodeEnum.SERVER_ERROR, "阿里云安全服务错误");
        }

        // 4. 保存评论
        ResponseResult<AppUser> userResult = userFeign.findUserById(userId);
        if (userResult.getCode() != 0) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN, userResult.getErrorMessage());
        }
        AppUser user = userResult.getData();
        if (user == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN, "当前登录信息错误");
        }

        AppCommentRepay appCommentRepay = new AppCommentRepay();
        appCommentRepay.setAuthorId(user.getId());
        appCommentRepay.setAuthorName(user.getName());
        appCommentRepay.setContent(dto.getContent());
        appCommentRepay.setCommentId(dto.getCommentId());
        appCommentRepay.setCreatedTime(new Date());
        appCommentRepay.setUpdatedTime(new Date());
        appCommentRepay.setLikes(0);
        mongoTemplate.insert(appCommentRepay);

        // 更新评论的回复数量
        AppComment appComment = mongoTemplate.findById(dto.getCommentId(), AppComment.class);
        appComment.setReply(appComment.getReply() + 1);
        mongoTemplate.save(appComment);

        return ResponseResult.successResult();
    }

    /**
     * 点赞回复的评论
     * 
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveCommentRepayLike(CommentRepayLikeDTO dto) {
        // 1. 检查参数
        // 2. 判断是否登录
        Long userId = AppThreadLocalUtils.getUserId();
        if (userId == null) {
            CustException.throwException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 3.点赞操作
        AppCommentRepay appCommentRepay = mongoTemplate.findById(dto.getCommentRepayId(), AppCommentRepay.class);
        if (appCommentRepay == null) {
            CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "评论不存在");
        }

        // 查询当前用户是否点过赞
        Query query = Query
                .query(Criteria.where("commentRepayId").is(dto.getCommentRepayId()).and("authorId").is(userId));
        AppCommentRepayLike repayLike = mongoTemplate.findOne(query, AppCommentRepayLike.class);

        // 点赞
        if (dto.getOperation() == 0) {
            if (repayLike != null) {
                CustException.throwException(AppHttpCodeEnum.DATA_EXIST, "请勿重复点赞");
            }

            // 更新评论的点赞数量
            appCommentRepay.setLikes(appCommentRepay.getLikes() + 1);
            mongoTemplate.save(appCommentRepay);

            // 保存APP评论信息点赞
            AppCommentRepayLike appCommentRepayLike = new AppCommentRepayLike();
            appCommentRepayLike.setAuthorId(userId);
            appCommentRepayLike.setCommentRepayId(appCommentRepay.getId());
            appCommentRepayLike.setOperation(dto.getOperation());
            mongoTemplate.save(appCommentRepayLike);
        } else {
            // 4. 取消点赞
            if (repayLike == null) {
                CustException.throwException(AppHttpCodeEnum.DATA_NOT_EXIST, "您还没有点赞");
            }

            // 更新评论的点赞数量
            appCommentRepay.setLikes(appCommentRepay.getLikes() < 0 ? 0 : appCommentRepay.getLikes() - 1);
            mongoTemplate.save(appCommentRepay);

            // 更新APP评论信息点赞
            mongoTemplate.remove(query, AppCommentRepayLike.class);
        }

        // 5.数据返回
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("likes", appCommentRepay.getLikes());
        return ResponseResult.successResult(resultMap);
    }
}
