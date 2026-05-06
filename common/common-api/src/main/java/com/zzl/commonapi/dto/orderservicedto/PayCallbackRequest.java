package com.zzl.commonapi.dto.orderservicedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class PayCallbackRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 支付状态：1-成功，0-失败
     */
    @NotNull(message = "支付状态不能为空")
    private Integer status;

    /**
     * 支付流水号（可选）
     */
    private String payNo;
}