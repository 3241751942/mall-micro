package com.zzl.productservice.dto.request;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品分页查询请求参数
 */
@Data
public class ProductPageRequest {

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页大小最小为1")
    private Integer pageSize = 10;

    /** 分类ID */
    private Long categoryId;

    /** 品牌ID */
    private Long brandId;

    /** 最低价格 */
    private BigDecimal minPrice;

    /** 最高价格 */
    private BigDecimal maxPrice;

    /** 关键字 */
    private String keyword;

    /** 排序字段：price, sales, createTime */
    @Pattern(regexp = "^(price|sales|createTime)$", message = "排序字段只能是 price, sales, createTime")
    private String sortBy;

    /** 排序方向：asc, desc */
    @Pattern(regexp = "^(asc|desc)$", message = "排序方向只能是 asc 或 desc")
    private String order;
}