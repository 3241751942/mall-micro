package com.zzl.orderservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.commonapi.dto.productservicedto.ProductInternalDTO;
import com.zzl.commonapi.dto.stockservicedto.StockLockRequest;
import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.commonapi.feign.productservicefeign.ProductFeignClient;
import com.zzl.commonapi.feign.stockservicefeign.StockFeignClient;
import com.zzl.orderservice.dto.request.CreateOrderRequest;
import com.zzl.orderservice.entity.Order;
import com.zzl.orderservice.entity.OrderItem;
import com.zzl.orderservice.entity.OrderWithItems;
import com.zzl.orderservice.exception.OrderException;
import com.zzl.orderservice.mapper.OrderItemMapper;
import com.zzl.orderservice.mapper.OrderMapper;
import com.zzl.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author micro
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderItemMapper orderItemMapper;
    private final ProductFeignClient productFeignClient;
    private final StockFeignClient stockFeignClient;

    /**
     * 生成订单号（后面改成雪花算法）
     *
     * @return 订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public String createOrder(CreateOrderRequest request) {
        // 批量获取商品信息
        List<Long> productIds = request.getItems().stream()
                .map(CreateOrderRequest.OrderItemRequest::getProductId)
                .collect(Collectors.toList());
        List<ProductInternalDTO> products = productFeignClient.batchGetProducts(productIds);
        if (products.size() != productIds.size()) {
            log.error("部分商品不存在，请求商品数: {}, 返回商品数: {}", productIds.size(), products.size());
            throw new OrderException("部分商品不存在");
        }
        Map<Long, ProductInternalDTO> productMap = products.stream()
                .collect(Collectors.toMap(ProductInternalDTO::getId, p -> p));

        // 锁定库存
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            StockLockRequest lockRequest = new StockLockRequest();
            lockRequest.setProductId(item.getProductId());
            lockRequest.setQuantity(item.getQuantity());
            lockRequest.setOrderNo("LOCK_" + UUID.randomUUID()); // 临时订单号，正式下单后更新
            StockLockResult result = stockFeignClient.lock(lockRequest);
            if (!result.isSuccess()) {
                log.warn("锁定库存失败，商品: {}, 原因: {}", item.getProductId(), result.getMessage());
                throw new OrderException("商品 " + item.getProductId() + " 库存不足");
            }
        }

        // 创建订单表
        String orderNo = generateOrderNo();
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(request.getUserId());
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            ProductInternalDTO p = productMap.get(item.getProductId());
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setStatus(0);
        save(order);

        // 保存订单明细
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            ProductInternalDTO p = productMap.get(item.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderNo(orderNo);
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(p.getName());
            orderItem.setProductPrice(p.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemMapper.insert(orderItem);
        }


        log.info("订单创建成功，订单号: {}", orderNo);
        return orderNo;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderWithItems getOrderWithItems(String orderNo, Long userId) {
        Order order = lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .eq(userId != null, Order::getUserId, userId)
                .one();
        if (order == null) {
            return null;
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
        OrderWithItems result = new OrderWithItems();
        result.setOrder(order);
        result.setItems(items);
        return result;
    }

    @Override
    public Page<Order> pageOrders(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId != null, Order::getUserId, userId)
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderNos(List<String> orderNos) {
        if (orderNos == null || orderNos.isEmpty()) {
            return Collections.emptyList();
        }
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderNo, orderNos));
    }

    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, Long userId) {
        Order order = lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .eq(Order::getUserId, userId)
                .one();
        if (order == null) {
            throw new OrderException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new OrderException("只有待支付的订单可以取消");
        }
        order.setStatus(2);
        order.setCancelTime(LocalDateTime.now());
        updateById(order);

        // 解锁库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
        for (OrderItem item : items) {
            StockLockRequest unlockRequest = new StockLockRequest();
            unlockRequest.setProductId(item.getProductId());
            unlockRequest.setQuantity(item.getQuantity());
            unlockRequest.setOrderNo(orderNo);
            boolean success = stockFeignClient.unlock(unlockRequest);
            if (!success) {
                log.warn("解锁库存失败，订单号: {}, 商品: {}", orderNo, item.getProductId());
            }
        }
        log.info("订单取消成功，订单号: {}", orderNo);
    }

    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void handlePayCallback(String orderNo, Integer payStatus) {
        Order order = lambdaQuery().eq(Order::getOrderNo, orderNo).one();
        if (order == null) {
            throw new OrderException("订单不存在");
        }
        if (order.getStatus() != 0) {
            log.warn("订单状态不是待支付，忽略回调: {}", orderNo);
            return;
        }
        if (payStatus == 1) {
            order.setStatus(1);
            order.setPayTime(LocalDateTime.now());
            updateById(order);

            // 确认扣减库存
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderNo, orderNo));
            for (OrderItem item : items) {
                StockLockRequest confirmRequest = new StockLockRequest();
                confirmRequest.setProductId(item.getProductId());
                confirmRequest.setQuantity(item.getQuantity());
                confirmRequest.setOrderNo(orderNo);
                boolean confirmed = stockFeignClient.confirm(confirmRequest);
                if (!confirmed) {
                    log.error("确认扣减库存失败，订单号: {}, 商品: {}", orderNo, item.getProductId());
                }
            }
            log.info("支付成功，订单号: {}", orderNo);
        } else {
            log.info("支付失败，订单号: {}", orderNo);
        }
    }


    /**
     * 页查询订单及明细
     * @param userId   用户ID
     * @param status   订单状态
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return Page<OrderWithItems>
     */
    @Override
    public Page<OrderWithItems> pageOrdersWithItems(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        // 1. 分页查询订单主表
        Page<Order> orderPage = pageOrders(userId, status, pageNum, pageSize);
        if (orderPage.getRecords().isEmpty()) {
            // 返回空分页结果（类型转换）
            return new Page<>();
        }

        // 2. 批量查询所有订单的明细
        List<String> orderNos = orderPage.getRecords().stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toList());
        List<OrderItem> allItems = getOrderItemsByOrderNos(orderNos);
        Map<String, List<OrderItem>> itemsMap = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));

        // 3. 转换为 Page<OrderWithItems>
        Page<OrderWithItems> resultPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderWithItems> records = orderPage.getRecords().stream()
                .map(order -> {
                    OrderWithItems wrapper = new OrderWithItems();
                    wrapper.setOrder(order);
                    wrapper.setItems(itemsMap.getOrDefault(order.getOrderNo(), Collections.emptyList()));
                    return wrapper;
                })
                .collect(Collectors.toList());
        resultPage.setRecords(records);
        return resultPage;
    }

    @Override
    public void setOrderStatus(Integer status, String orderNo) {
        Order order = lambdaQuery().eq(Order::getOrderNo, orderNo).one();
        if (order == null) {
            throw new OrderException("订单不存在");
        }
        Boolean result=lambdaUpdate().eq(Order::getOrderNo, orderNo).set(Order::getStatus, status).update();
        if (!result) {
            throw new OrderException("系统繁忙中，订单修改失败");
        }
//        order.setStatus(status);
//        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Order::getOrderNo, orderNo);
//        update(order, wrapper);
    }
}