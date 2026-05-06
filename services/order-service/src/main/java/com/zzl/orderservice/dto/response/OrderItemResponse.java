package com.zzl.orderservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品明细响应对象
 *
 * @author micro
 * @since 1.0.0
 */
@Data
public class OrderItemResponse {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称（快照）
     */
    private String productName;

    /**
     * 商品单价（快照）
     */
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 小计
     */
    private BigDecimal totalPrice;
}

