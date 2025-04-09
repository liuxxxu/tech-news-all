package com.liuxu.model.article.vos;

import com.liuxu.model.article.pojos.AppArticle;
import lombok.Data;

@Data
public class HotArticleVo extends AppArticle {
    /**
     * 分值
     */
    private Integer score;
}