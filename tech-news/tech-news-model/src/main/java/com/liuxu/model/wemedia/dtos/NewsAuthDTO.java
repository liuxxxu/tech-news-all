package com.liuxu.model.wemedia.dtos;

import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

@Data
public class NewsAuthDTO extends PageRequestDTO {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 状态
     */
    private Short status;
    /**
     * 文章id
     */
    private Long id;

    /**
     * 失败原因
     */
    private String msg;
}