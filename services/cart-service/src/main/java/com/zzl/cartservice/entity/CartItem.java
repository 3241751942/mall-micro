package com.zzl.cartservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 用于业务包装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private Boolean checked;
    private Integer stock;

}
