package com.zzl.commonapi.dto.stockservicedto;


import lombok.Data;

/**
 * 库存信息传输对象（供其他服务调用）
 */
@Data
public class StockDTO {

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 总库存
     */
    private Integer totalStock;

    /**
     * 已锁定库存
     */
    private Integer lockedStock;

    /**
     * 已售库存
     */
    private Integer soldStock;

    /**
     * 可用库存  即totalStock - lockedStock - soldStock
     */
    private Integer availableStock;
}
