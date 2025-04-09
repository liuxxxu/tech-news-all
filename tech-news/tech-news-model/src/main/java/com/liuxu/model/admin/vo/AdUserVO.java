package com.liuxu.model.admin.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AdUserVO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String image;
    private String description;
    private Date loginTime;
    private Date createdTime;
}
