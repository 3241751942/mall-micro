package com.zzl.stockservice.dto;

import lombok.Data;

/**
 * 库存对外响应对象
 */
@Data
public class StockVO {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 可用库存数量
     */
    private Integer availableStock;
}
