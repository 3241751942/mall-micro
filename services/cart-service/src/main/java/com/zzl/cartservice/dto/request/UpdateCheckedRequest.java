package com.zzl.cartservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新购物车商品选中状态的请求体
 */
@Data
public class UpdateCheckedRequest {

    /**
     * 是否选中：true-选中，false-未选中，本来想用零一，他们推荐用真假
     */
    @NotNull(message = "选中状态不能为空")
    private Boolean checked;
}