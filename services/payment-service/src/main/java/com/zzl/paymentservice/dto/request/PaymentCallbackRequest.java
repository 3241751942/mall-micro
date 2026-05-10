package com.zzl.paymentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 模拟支付回调请求参数（开发环境用）
 */
@Data
public class PaymentCallbackRequest {

    /**
     * 支付单号，不能为空
     */
    @NotBlank(message = "支付单号不能为空")
    private String paymentNo;
}