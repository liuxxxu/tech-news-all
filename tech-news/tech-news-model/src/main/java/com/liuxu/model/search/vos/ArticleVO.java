package com.liuxu.model.search.vos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.liuxu.common.anno.EsId;
import lombok.Data;

import java.util.Date;

@Data
public class ArticleVO {
    // 文章id
    @EsId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    // 文章标题
    private String title;
    // 文章发布时间
    private Date publishTime;
    // 封面
    private String cover;
    // 作者
    private Long authorId;
    // 作者名字
    private String authorName;
    // 作者头像
    private String authorAvatar;
    // 文章内容
    private String content;
    // AI 总结
    private String summary;
}