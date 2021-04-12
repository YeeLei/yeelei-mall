package com.yeelei.mall.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddCategoryReq {
    @Size(min = 2, max = 5)
    @NotNull(message = "name不能为空")
    private String name;

    @NotNull(message = "type不能为空")
    @Min(1) //分类最小层级数
    @Max(3) //分类最大层级数
    private Integer type;

    @NotNull(message = "parentId不能为空")
    private Integer parentId;

    @NotNull(message = "orderNum不能为空")
    private Integer orderNum;
}
