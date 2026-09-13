package com.zzl.commonapi.dto.orderservicedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderDetail {
    private Long orderId;
    private String orderNo;
    private BigDecimal totalAmount;
    private List<Item> items;
}
