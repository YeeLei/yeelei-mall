package com.yeelei.mall.model.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Cart {
    private Integer id;

    private Integer productId;

    private Integer userId;

    private Integer quantity;

    private Integer selected;

    private Date createTime;

    private Date updateTime;
}