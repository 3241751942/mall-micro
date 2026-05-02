package com.zzl.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BrandCreateRequest {
    /**
     * 品牌名称
     */
    @NotBlank(message = "品牌名称不能为空")
    private String name;

    /**
     * 品牌logo
     */
    private String logo;

    /**
     * 排序等级
     */
    private Integer sort = 0;
}
