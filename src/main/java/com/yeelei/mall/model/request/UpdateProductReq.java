package com.yeelei.mall.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 描述：更新商品请求参数
 */
@Data
public class UpdateProductReq {

    @NotNull(message = "商品id不能为空")
    private Integer id;

    private String name;

    private String image;

    private String detail;

    private Integer categoryId;

    @Min(value = 1, message = "价格不能小于1分")
    private Integer price;

    @Max(value = 10000, message = "库存不能大于10000")
    private Integer stock;

    private Integer status;
}
