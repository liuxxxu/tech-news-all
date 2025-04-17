package com.liuxu.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 自媒体图文引用素材信息表
 */
@Data
@TableName("wm_news_material")
public class WmNewsMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 素材ID
     */
    @TableField("material_id")
    private Long materialId;

    /**
     * 图文ID
     */
    @TableField("news_id")
    private Long newsId;

    /**
     * 引用类型
     0 内容引用
     1 封面引用
     */
    @TableField("type")
    private Short type;

}