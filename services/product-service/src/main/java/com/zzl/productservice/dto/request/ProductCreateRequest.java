package com.zzl.productservice.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 新增商品请求
 */
@Data
public class ProductCreateRequest {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    private Long brandId;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    /** 图片URL，JSON数组字符串，如 ["url1","url2"] */
    private String images;
}
