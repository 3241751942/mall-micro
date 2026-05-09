package com.zzl.cartservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车中单个商品的响应
 */
@Data
public class CartItemResponse {

    private Long productId;       // 商品ID
    private String productName;   // 商品名称（从商品服务获取）
    private String productImage;  // 商品图片（从商品服务获取）
    private BigDecimal price;     // 商品单价（从商品服务获取）
    private Integer quantity;     // 购买数量
    private Boolean checked;      // 是否选中（true-选中，false-未选中）
    private Integer stock;        // 实时库存（从库存服务获取）
}