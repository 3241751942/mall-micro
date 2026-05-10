package com.zzl.commonapi.dto.paymentservicedto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付服务 Feign 调用请求 DTO
 */
@Data
public class PaymentFeignRequest {

    /** 订单ID */
    private Long orderId;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付方式：ALIPAY/WECHAT/BALANCE */
    private String payType;
}