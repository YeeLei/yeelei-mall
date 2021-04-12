package com.yeelei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.yeelei.mall.common.ApiRestResponse;
import com.yeelei.mall.model.pojo.Product;
import com.yeelei.mall.model.request.ProductListReq;
import com.yeelei.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 描述：前台商品Controller
 */
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation("前台商品列表")
    @GetMapping("/product/list")
    public ApiRestResponse listProductForConsumer(@RequestBody ProductListReq productListReq) {
        PageInfo pageInfo = productService.listProductForConsumer(productListReq);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("商品详情接口")
    @GetMapping("/product/detail")
    public ApiRestResponse detail(@RequestParam Integer id) {
        Product product = productService.detail(id);
        return ApiRestResponse.success(product);
    }
}
