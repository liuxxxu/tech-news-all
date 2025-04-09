package com.liuxu.model.admin.dtos;

import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

/**
 * 敏感词数据传输对象DTO */
@Data
public class SensitiveDTO extends PageRequestDTO {
    /**
     * 敏感词名称
     */
    private String name;
}
