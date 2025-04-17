package com.liuxu.model.wemedia.vos;

import com.liuxu.model.wemedia.pojos.WmNews;
import lombok.Data;

@Data
public class WmNewsVO  extends WmNews {
    /**
     * 作者名称
     */
    private String authorName;
}