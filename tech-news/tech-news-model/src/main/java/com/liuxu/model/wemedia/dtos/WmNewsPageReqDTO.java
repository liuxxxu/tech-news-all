package com.liuxu.model.wemedia.dtos;

import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

import java.util.Date;

@Data
public class WmNewsPageReqDTO extends PageRequestDTO {
    private Short status;//状态
    private Date beginPubDate;//开始时间
    private Date endPubDate;//结束时间
    private Long channelId;//所属频道ID
    private String keyword;//关键字
}