package com.liuxu.model.user.dtos;

import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

@Data
public class AuthDTO extends PageRequestDTO {
    //状态
    private Short status;
    // 认证用户ID
    private Long id;
    //驳回的原因
    private String reason;
}