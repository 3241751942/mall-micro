package com.zzl.commonapi.dto.productservicedto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品内部传输对象（供订单服务等调用）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInternalDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String images;
}
