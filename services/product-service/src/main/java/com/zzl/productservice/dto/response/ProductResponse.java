package com.zzl.productservice.dto.response;

import lombok.Data;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应对象（对外）
 *
 * @author micro
 * @since 1.0.0
 */
@Data
public class ProductResponse {

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