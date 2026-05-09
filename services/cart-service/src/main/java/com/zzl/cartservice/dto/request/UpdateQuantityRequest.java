package com.zzl.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改商品数量请求
 */
@Data
public class UpdateQuantityRequest {

    /**
     * 商品数量，最小为1（传0时表示删除）
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 0, message = "数量不能为负数")
    private Integer quantity;
}
