package com.liuxu.model.article.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleHomeDTO {
    // 最大时间
    Date maxTime;
    // 最小时间
    Date minTime;
    // 分页size
    Integer size;
    // 频道ID
    String tag;
}