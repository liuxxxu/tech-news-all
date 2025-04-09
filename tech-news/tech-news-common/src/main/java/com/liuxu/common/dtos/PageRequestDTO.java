package com.liuxu.common.dtos;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 分页查询参数
 */
@Data
@Slf4j
public class PageRequestDTO {
    protected Integer size;
    protected Integer page;


    /**
     * 检查参数
     */
    public void checkParam() {
        if (this.page == null || this.page <= 0) {
            setPage(1);
        }
        if (this.size == null || this.size <= 0 || this.size > 100) {
            setSize(10);
        }
    }
}
