package com.liuxu.model.admin.dtos;


import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

@Data
public class ChannelDTO extends PageRequestDTO {
    /**
     * 频道名称
     */
    private String name ;
    /**
     * 状态
     */
    private Integer status;
}
