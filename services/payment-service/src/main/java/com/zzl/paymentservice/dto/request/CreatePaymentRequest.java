package com.zzl.paymentservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建支付单请求 DTO
 */
@Data
public class CreatePaymentRequest {

    /**
     * 订单ID，不能为空
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 用户ID，不能为空
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 支付金额，必须大于0
     */
    @NotNull(message = "金额不能为空")
    @Positive(message = "金额必须大于0")
    private BigDecimal amount;

    /**
     * 支付方式：ALIPAY（支付宝）、WECHAT（微信）、BALANCE（余额）
     */
    @NotNull(message = "支付方式不能为空")
    private String payType;
}