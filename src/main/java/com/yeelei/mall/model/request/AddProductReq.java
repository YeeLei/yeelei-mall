package com.yeelei.mall.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 描述：添加商品请求参数
 */
@Data
public class AddProductReq {
    @NotNull(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "商品图片不能为空")
    private String image;

    private String detail;

    @NotNull(message = "商品分类不能为空")
    private Integer categoryId;

    @NotNull(message = "商品价格不能为空")
    @Min(value = 1, message = "价格不能小于1分")
    private Integer price;

    @NotNull(message = "商品库存不能为空")
    @Max(value = 10000, message = "库存不能大于10000")
    private Integer stock;

    private Integer status;
}
