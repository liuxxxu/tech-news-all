package com.liuxu.model.behavior.dtos;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class UnLikesBehaviorDTO {
    // 设备ID
    Long equipmentId;
    // 文章ID
    @NotNull(message = "文章id不能为空")
    Long articleId;
    /**
     * 不喜欢操作方式
     * 0 不喜欢
     * 1 取消不喜欢
     */
    Short type;

}