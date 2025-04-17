package com.liuxu.model.user.dtos;

import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

@Data
public class RealNameDTO extends PageRequestDTO {
    /**
     * 用户实名
     */
    private String name;
    /**
     * 身份证号
     */
    private String idno;
    /**
     * 正面照片
     */
    private String fontImage;
    /**
     * 背面照片
     */
    private String backImage;
}