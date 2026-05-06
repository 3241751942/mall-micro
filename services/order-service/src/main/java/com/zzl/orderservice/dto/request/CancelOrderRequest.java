package com.zzl.orderservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取消订单
 */
@Data
public class CancelOrderRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;
}