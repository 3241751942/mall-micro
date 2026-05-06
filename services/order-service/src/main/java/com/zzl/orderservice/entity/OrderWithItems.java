package com.zzl.orderservice.entity;

import lombok.Data;

import java.util.List;

@Data
public class OrderWithItems {
    /**
     * 订单主表实体
     */
    private Order order;

    /**
     * 订单明细列表
     */
    private List<OrderItem> items;
}