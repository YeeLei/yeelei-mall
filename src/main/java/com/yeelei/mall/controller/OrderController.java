package com.yeelei.mall.controller;

import com.github.pagehelper.PageInfo;
import com.yeelei.mall.common.ApiRestResponse;
import com.yeelei.mall.filter.UserFilter;
import com.yeelei.mall.model.query.CreateOrderReq;
import com.yeelei.mall.model.vo.OrderVO;
import com.yeelei.mall.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：订单Controller
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping("/order/create")
    public ApiRestResponse create(@RequestBody CreateOrderReq createOrderReq) {
        String orderNo = orderService.create(createOrderReq);
        return ApiRestResponse.success(orderNo);
    }

    @ApiOperation("订单详情")
    @GetMapping("/order/detail")
    public ApiRestResponse detail(String orderNo) {
        OrderVO orderVO = orderService.detail(orderNo);
        return ApiRestResponse.success(orderVO);
    }

    @ApiOperation("前台订单列表")
    @GetMapping("/order/list")
    public ApiRestResponse listOrderForConsumer(@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
        PageInfo pageInfo = orderService.listOrderForConsumer(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("取消订单")
    @PostMapping("/order/cancel")
    public ApiRestResponse cancel(String orderNo) {
        orderService.cancel(orderNo);
        return ApiRestResponse.success();
    }

    @ApiOperation("生成二维码支付码")
    @PostMapping("/order/qrcode")
    public ApiRestResponse qrcode(String orderNo) {
        String imgUrl = orderService.qrcode(orderNo);
        return ApiRestResponse.success(imgUrl);
    }

    @ApiOperation("支付接口")
    @PostMapping("/pay")
    public ApiRestResponse pay(String orderNo) {
        orderService.pay(orderNo);
        return ApiRestResponse.success();
    }
}
