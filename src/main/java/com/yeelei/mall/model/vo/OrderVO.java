package com.yeelei.mall.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 描述： OrderVO,订单详情
 */
@Data
public class OrderVO {

    private String orderNo;

    private Integer userId;

    private Integer totalPrice;

    private String receiverName;

    private String receiverMobile;

    private String receiverAddress;

    private Integer orderStatus;

    private Integer postage;

    private Integer paymentType;

    private Date deliveryTime;

    private Date payTime;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

    private String orderStatusName;

    private List<OrderItemVO> orderItemVOList;

}