package com.yeelei.mall.model.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 描述： CreateOrderReq
 */
@Data
public class CreateOrderReq {
    @NotNull
    private String receiverName;
    @NotNull
    private String receiverMobile;
    @NotNull
    private String receiverAddress;

    //运费默认为0
    private Integer postage = 0;
    //支付类型默认为在线支付
    private Integer paymentType = 1;
}
