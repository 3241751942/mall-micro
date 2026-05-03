package com.zzl.commonapi.dto.stockservicedto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 库存操作请求（锁定/确认/解锁）
 */
@Data
public class StockLockRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    /**
     * 数量
     */
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    /**
     * 订单号，用于流水记录
     */
    @Size(max = 64, message = "订单号长度不得超过64")
    private String orderNo;
}
