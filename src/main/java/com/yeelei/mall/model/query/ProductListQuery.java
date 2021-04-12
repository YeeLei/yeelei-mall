package com.yeelei.mall.model.query;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 商品query
 */
@Data
public class ProductListQuery {
    private String keyword;
    private List<Integer> categoryIds = new ArrayList<>();
}
