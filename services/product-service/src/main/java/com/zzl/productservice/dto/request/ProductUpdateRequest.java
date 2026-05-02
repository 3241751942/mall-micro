package com.zzl.productservice.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 修改商品请求（支持部分更新）

 */
@Data
public class ProductUpdateRequest {

    private String name;
    private String description;
    private Long categoryId;
    private Long brandId;

    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    /** 状态：0-下架，1-上架 */
    private Integer status;

    private String images;
}
