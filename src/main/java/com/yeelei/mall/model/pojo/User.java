package com.yeelei.mall.model.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;

    private String username;

    private String password;

    private String personalizedSignature;

    private Integer role;

    private Date createTime;

    private Date updateTime;

}