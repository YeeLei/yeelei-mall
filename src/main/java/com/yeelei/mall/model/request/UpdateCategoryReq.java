package com.yeelei.mall.model.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdateCategoryReq {
    @NotNull(message = "分类id不能为空")
    private Integer id;

    @Size(min = 2,max = 5)
    private String name;

    @Min(1) //分类最小层级数
    @Max(3) //分类最大层级数
    private Integer type;

    private Integer parentId;

    private Integer orderNum;
}
