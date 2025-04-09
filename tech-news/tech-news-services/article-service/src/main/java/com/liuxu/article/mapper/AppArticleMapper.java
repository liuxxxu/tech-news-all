package com.liuxu.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuxu.model.article.dtos.ArticleHomeDTO;
import com.liuxu.model.article.pojos.AppArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AppArticleMapper extends BaseMapper<AppArticle> {

    /**
     * 查询文章列表
     * 
     * @param dto
     * @param type 0：加载更多 1：加载最新
     * @return
     */
    List<AppArticle> loadArticleList(@Param("dto") ArticleHomeDTO dto, @Param("type") Short type);

    /**
     * 根据日期查询文章
     * 
     * @param beginDate
     * @return
     */
    List<AppArticle> selectArticleByDate(@Param("beginDate") String beginDate);
}
