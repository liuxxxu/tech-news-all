package com.liuxu.model.behavior.dtos;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ReadBehaviorDTO {
    // 设备ID
    Long equipmentId;
    // 文章、动态、评论等ID
    @NotNull(message = "文章id必须传")
    Long articleId;
    /**
     * 阅读次数
     */
    Short count;
}