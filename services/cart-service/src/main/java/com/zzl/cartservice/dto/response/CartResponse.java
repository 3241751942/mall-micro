package com.zzl.cartservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车商品列表响应数据
 */
@Data
public class CartResponse {

    private List<CartItemResponse> items;  // 购物车商品列表
    private BigDecimal totalAmount;        // 选中的商品总金额
    private Integer totalChecked;          // 选中的商品总数
}