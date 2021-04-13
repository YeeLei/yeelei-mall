package com.yeelei.mall.controller;


import com.github.pagehelper.PageInfo;
import com.yeelei.mall.common.ApiRestResponse;
import com.yeelei.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：后台订单Controller
 */
@RestController
public class OrderAdminController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("管理员订单列表")
    @GetMapping("/admin/order/list")
    public ApiRestResponse listOrderForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listOrderForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("订单发货")
    @PostMapping("/admin/order/delivered")
    public ApiRestResponse delivered(String orderNo) {
        orderService.delivered(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("订单完结")
    @PostMapping("/order/finish")
    public ApiRestResponse finish(String orderNo) {
        orderService.finish(orderNo);
        return ApiRestResponse.success();
    }
}
