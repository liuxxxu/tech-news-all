package com.liuxu.model.search.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class UserSearchDTO {

    // 设备ID
    Long equipmentId;
    /**
     * 搜索关键字
     */
    String searchWords;
    /**
     * 当前页
     */
    int pageNum;
    /**
     * 分页条数
     */
    int pageSize;

    Long entryId;
    /**
     * 最小时间
     */
    Date minBehotTime;

    public int getFromIndex() {
        if (this.pageNum < 1) return 0;
        if (this.pageSize < 1) this.pageSize = 10;
        return this.pageSize * (pageNum - 1);
    }
}