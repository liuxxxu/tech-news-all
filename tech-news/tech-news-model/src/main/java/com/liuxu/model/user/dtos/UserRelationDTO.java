package com.liuxu.model.user.dtos;

import lombok.Data;

@Data
public class UserRelationDTO {
    /**
     * 文章作者ID
     */
    private Long authorAppUserId;

    /**
     * 操作方式
     * 0 关注
     * 1 取消关注
     */
    private Integer operation;
}