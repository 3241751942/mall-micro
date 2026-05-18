package com.zzl.orderservice.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.commoncore.result.Result;
import com.zzl.orderservice.converter.OrderConverter;
import com.zzl.orderservice.dto.request.CancelOrderRequest;
import com.zzl.orderservice.dto.request.CreateOrderRequest;
import com.zzl.orderservice.dto.response.OrderResponse;
import com.zzl.orderservice.entity.OrderWithItems;
import com.zzl.orderservice.exception.OrderException;
import com.zzl.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     * @param request 创建订单请求（商品列表，收货地址等）
     * @param userId  当前登录用户 ID（由网关从 JWT 解析后放入请求头）
     * @return 订单号
     */
    @PostMapping("/create")
    public Result<String> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                      @RequestHeader("X-User-Id") Long userId) {
        // 强制使用当前登录用户 ID，防止请求体中的 userId 被篡改
        request.setUserId(userId);
        String orderNo = orderService.createOrder(request);
        return Result.success(orderNo);
    }

    /**
     * 查询订单详情
     *
     * @param orderNo 订单号
     * @param userId  当前登录用户 ID
     * @return 订单详情（包含商品明细）
     */
    @GetMapping("/{orderNo}")
    public Result<OrderResponse> getOrder(@PathVariable String orderNo,
                                          @RequestHeader("X-User-Id") Long userId) {
        OrderWithItems wrapper = orderService.getOrderWithItems(orderNo, userId);
        if (wrapper == null || wrapper.getOrder() == null) {
            throw new OrderException("订单不存在");
        }
        OrderResponse response = OrderConverter.toOrderResponse(wrapper.getOrder(), wrapper.getItems());
        return Result.success(response);
    }

    /**
     * 分页查询当前用户的订单列表
     *
     * @param pageNum  页码（默认 1）
     * @param pageSize 每页条数（默认 10）
     * @param status   订单状态（可选，0-待支付，1-已支付，2-已取消，3-已发货，4-已完成）
     * @param userId   当前登录用户 ID
     * @return 分页订单数据（每笔订单包含商品明细）
     */
    @GetMapping("/page")
    public Result<IPage<OrderResponse>> pageOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(required = false) Integer status,
                                                   @RequestHeader("X-User-Id") Long userId) {
        Page<OrderWithItems> wrapperPage = orderService.pageOrdersWithItems(userId, status, pageNum, pageSize);
        IPage<OrderResponse> responsePage = wrapperPage.convert(wrapper ->
                OrderConverter.toOrderResponse(wrapper.getOrder(), wrapper.getItems())
        );
        return Result.success(responsePage);
    }

    /**
     * 取消订单
     *
     * @param request 取消请求（订单号）
     * @param userId  当前登录用户 ID
     * @return 成功响应
     */
    @PutMapping("/cancel")
    public Result<Void> cancelOrder(@Valid @RequestBody CancelOrderRequest request,
                                    @RequestHeader("X-User-Id") Long userId) {
        orderService.cancelOrder(request.getOrderNo(), userId);
        return Result.success();
    }


    @GetMapping("/orderId/{orderNo}")
    public Result<Long> getOrderId(@PathVariable String orderNo){
        Long id=orderService.getOrderId(orderNo);
        return Result.success(id);
    }
}