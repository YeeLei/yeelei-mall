package com.yeelei.mall.model.vo;

import lombok.Data;

@Data
public class CartVO {
    private Integer id;
    private Integer productId;
    private Integer userId;
    private Integer quantity;
    private Integer selected;
    private Integer price;
    private Integer totalPrice;
    private String productName;
    private String productImage;
}
