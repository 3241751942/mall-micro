package com.zzl.commonapi.dto.stockservicedto;


import lombok.Data;

/**
 * 库存锁定结果
 */
@Data
public class StockLockResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 剩余可用库存（失败时返回）
     */
    private Integer availableStock;
}