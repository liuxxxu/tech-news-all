package com.liuxu.feigns.fallback;

import com.liuxu.feigns.ArticleFeign;
import com.liuxu.model.article.pojos.AppAuthor;
import com.liuxu.common.dtos.ResponseResult;
import com.liuxu.common.enums.AppHttpCodeEnum;
import com.liuxu.model.search.vos.SearchArticleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleFeignFallback implements FallbackFactory<ArticleFeign> {

    @Override
    public ArticleFeign create(Throwable throwable) {
        return new ArticleFeign() {
            @Override
            public ResponseResult<AppAuthor> findByUserId(Long userId) {
                log.error("参数 userId : {}", userId);
                log.error("ArticleFeign findByUserId 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult save(AppAuthor appAuthor) {
                log.error("参数 appAuthor: {}", appAuthor);
                log.error("ArticleFeign save 远程调用出错 {} ", throwable.getMessage());
                return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
            }

            @Override
            public ResponseResult<SearchArticleVO> findArticle(Long id) {
                log.error(" 远程调用文章微服务  中  findArticle方法出错   ,  参数:{}      异常原因: {}", id, throwable.getCause());
                return ResponseResult.errorResult(AppHttpCodeEnum.REMOTE_SERVER_ERROR,
                        "远程调用查询Article出错," + throwable.getMessage());
            }
        };
    }
}
