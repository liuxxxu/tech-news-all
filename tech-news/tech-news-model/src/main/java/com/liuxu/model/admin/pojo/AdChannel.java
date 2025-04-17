package com.liuxu.model.admin.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.liuxu.common.validator.ValidatorAdd;
import com.liuxu.common.validator.ValidatorUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;
/**
 * <p>
 * 频道信息表
 * </p>
 */
@Data
@TableName("ad_channel")
public class AdChannel implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id")
    @NotNull(message = "id不能为空",groups = {ValidatorUpdate.class})
    private Long id;
    /**
     * 频道名称
     */
    @TableField("name")
    @NotBlank(message = "频道名称不能为空",groups = {ValidatorAdd.class})
    @Length(max = 10, message = "频道名称长度不能大于10", groups = {ValidatorAdd.class, ValidatorUpdate.class})
    private String name;
    /**
     * 频道描述
     */
    @TableField("description")
    private String description;

    @TableField("status")
    private Boolean status;
    /**
     * 默认排序
     */
    @TableField("ord")
    private Integer ord;
    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
}