package com.yeelei.mall.service;

import com.github.pagehelper.PageInfo;
import com.yeelei.mall.model.query.CreateOrderReq;
import com.yeelei.mall.model.vo.OrderVO;

import java.util.List;

public interface OrderService {
    String create(CreateOrderReq createOrderReq);

    OrderVO detail(String orderNo);

    PageInfo listOrderForConsumer(Integer pageNum, Integer pageSize);

    void cancel(String orderNo);

    String qrcode(String orderNo);

    void pay(String orderNo);

    PageInfo listOrderForAdmin(Integer pageNum, Integer pageSize);

    void delivered(String orderNo);

    void finish(String orderNo);
}
