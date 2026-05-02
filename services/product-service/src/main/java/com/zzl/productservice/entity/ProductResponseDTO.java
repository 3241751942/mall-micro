package com.zzl.productservice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用于service层和controller之间数据载体
 */
@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer status;      // 0-下架，1-上架
    private String images;
    private Integer sales;
    private String categoryName; // 冗余分类名称
    private String brandName;    // 冗余品牌名称
    private LocalDateTime createTime;   // yyyy-MM-dd HH:mm:ss
}