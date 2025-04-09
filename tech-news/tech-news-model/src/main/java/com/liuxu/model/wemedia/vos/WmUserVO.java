package com.liuxu.model.wemedia.vos;

import lombok.Data;

import java.util.Date;

@Data
public class WmUserVO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String image;
    private Date loginTime;
    private Date createdTime;
}
