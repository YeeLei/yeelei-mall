package com.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import com.yeelei.mall.model.pojo.Product;
import com.yeelei.mall.model.request.AddProductReq;
import com.yeelei.mall.model.request.ProductListReq;
import com.yeelei.mall.model.request.UpdateProductReq;

public interface ProductService {

    void add(AddProductReq addProductReq);

    void update(UpdateProductReq updateProductReq);

    void delete(Integer id);

    void batchUpdateSellStatus(Integer[] ids, Integer sellStatus);

    PageInfo listProductForAdmin(Integer pageNum, Integer pageSize);

    PageInfo listProductForConsumer(ProductListReq productListReq);

    Product detail(Integer id);
}
