package com.liuxu.model.wemedia.dtos;

import com.liuxu.common.dtos.PageRequestDTO;
import lombok.Data;

@Data
public class WmMaterialDTO extends PageRequestDTO {
    Short isCollection; //1 查询收藏的   0: 未收藏
}