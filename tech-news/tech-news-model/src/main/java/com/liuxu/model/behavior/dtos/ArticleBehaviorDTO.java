package com.liuxu.model.behavior.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class ArticleBehaviorDTO {
    // 设备ID
    Long equipmentId;
    // 文章ID
    @NotNull(message = "文章Id不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    Long articleId;
    // 作者ID
    Long authorId;
    // 作者对应的apuserid
    Long authorAppUserId;
}