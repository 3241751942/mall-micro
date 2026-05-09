package com.zzl.cartservice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车内部业务传输对象（Service层返回）
 */
@Data
public class CartBO {
    private List<CartItem> items;
    private BigDecimal totalAmount;
    private Integer totalChecked;
}