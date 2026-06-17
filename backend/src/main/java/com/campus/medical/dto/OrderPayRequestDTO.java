package com.campus.medical.dto;

import lombok.Data;

/**
 * 订单支付请求DTO
 */
@Data
public class OrderPayRequestDTO {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付方式(wechat/alipay/campus)
     */
    private String payType;
}
