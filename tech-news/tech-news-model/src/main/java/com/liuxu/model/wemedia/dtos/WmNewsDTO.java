package com.liuxu.model.wemedia.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class WmNewsDTO {
    // 文章ID
    private Long id;
    /**
     * 作者
     */

    /**
     * 标题
     */
    private String title;
    /**
     * 频道id
     */
    private Long channelId;
    /**
     * 标签
     */
    private String labels;
    /**
     * 定时发布时间，为null表示立即发布
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 文章封面类型  0 无 1 有
     */
    private Short type;
    /**
     * 封面图片
     */
    private String cover;
    /**
     * 是否上架  0 下架  1 上架
     */
    private Short enable;

    /**
     * 原始状态 提交为1  草稿为0
     */
    private Short statusOld;

    /**
     * 状态 提交为1  草稿为0
     */
    private Short status;
    /**
     * 拒绝理由
     */
    private String reason;
}