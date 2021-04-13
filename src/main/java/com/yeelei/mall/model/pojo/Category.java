package com.yeelei.mall.model.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class Category {
    private Integer id;

    private String name;

    private Integer type;

    private Integer parentId;

    private Integer orderNum;

    private Date createTime;

    private Date updateTime;

}