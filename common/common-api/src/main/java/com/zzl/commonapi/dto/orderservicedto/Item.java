package com.zzl.commonapi.dto.orderservicedto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {
    private Long itemId;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
}
