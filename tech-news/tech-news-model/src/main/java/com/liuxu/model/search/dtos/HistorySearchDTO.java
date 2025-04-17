package com.liuxu.model.search.dtos;

import lombok.Data;

@Data
public class HistorySearchDTO {

    // 设备ID
    Long equipmentId;
    /**
     * 接收搜索历史记录id
     */
    String id;
}