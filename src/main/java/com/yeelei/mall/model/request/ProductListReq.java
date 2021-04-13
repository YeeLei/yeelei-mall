package com.yeelei.mall.model.request;

import lombok.Data;

/**
 * 描述：前台商品列表请求参数
 */
@Data
public class ProductListReq {
    private String keyword;
    private Integer categoryId;
    private String orderBy;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
