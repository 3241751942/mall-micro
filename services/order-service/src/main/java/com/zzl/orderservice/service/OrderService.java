package com.zzl.orderservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.orderservice.dto.request.CreateOrderRequest;
import com.zzl.orderservice.entity.Order;
import com.zzl.orderservice.entity.OrderItem;
import com.zzl.orderservice.entity.OrderWithItems;

import java.util.List;

public interface OrderService {


    /**
     * 创建订单（分布式事务）
     * @param request 创建订单请求
     * @return 订单号
     */
    String createOrder(CreateOrderRequest request);

    /**
     * 获取订单及其明细
     * @param orderNo 订单号
     * @param userId  用户ID可为空
     * @return 订单及明细组合对象，不存在时返回 null
     */
    OrderWithItems getOrderWithItems(String orderNo, Long userId);

    /**
     * 分页查询用户订单列表（返回订单实体分页，不含明细）
     *
     * @param userId   用户ID
     * @param status   订单状态（可选）
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 订单实体分页
     */
    Page<Order> pageOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据订单号列表批量查询订单明细
     * @param orderNos 订单号列表
     * @return 订单明细列表
     */
    List<OrderItem> getOrderItemsByOrderNos(List<String> orderNos);

    /**
     * 取消订单
     *
     * @param orderNo 订单号
     * @param userId  用户ID（超时取消时传 null）
     */
    void cancelOrder(String orderNo, Long userId);

    /**
     * 处理支付回调
     * @param orderNo   订单号
     * @param payStatus 支付状态: 1-成功, 0-失败
     */
    void handlePayCallback(String orderNo, Integer payStatus);

    /**
     * 分页查询订单及明细
     * @param userId   用户ID
     * @param status   订单状态
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页的订单及明细组合对象
     */
    Page<OrderWithItems> pageOrdersWithItems(Long userId, Integer status, Integer pageNum, Integer pageSize);

    void setOrderStatus(Integer status, String orderNo);

    Long getOrderId(String orderNo);
}