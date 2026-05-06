package com.zzl.orderservice.converter;

import com.zzl.orderservice.dto.response.OrderItemResponse;
import com.zzl.orderservice.dto.response.OrderResponse;
import com.zzl.orderservice.entity.Order;
import com.zzl.orderservice.entity.OrderItem;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;


public class OrderConverter {


    /**
     * 将订单实体和明细列表转换为订单响应对象
     * @param order 订单实体
     * @param items 明细列表
     * @return 订单响应
     */
    public static OrderResponse toOrderResponse(Order order, List<OrderItem> items) {
        if (order == null) {
            return null;
        }
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        if (items != null) {
            List<OrderItemResponse> itemResponses = items.stream()
                    .map(OrderConverter::toOrderItemResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }
        return response;
    }

    /**
     * 将订单明细实体转换为明细响应对象
     * @param item 订单明细实体
     * @return 明细响应
     */
    public static OrderItemResponse toOrderItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }
        OrderItemResponse response = new OrderItemResponse();
        BeanUtils.copyProperties(item, response);
        return response;
    }
}
