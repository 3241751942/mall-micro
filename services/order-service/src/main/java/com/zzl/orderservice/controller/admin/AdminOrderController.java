package com.zzl.orderservice.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.commoncore.result.Result;
import com.zzl.orderservice.converter.OrderConverter;
import com.zzl.orderservice.dto.response.OrderResponse;
import com.zzl.orderservice.entity.OrderWithItems;
import com.zzl.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * 管理员分页查询所有订单
     * @param pageNum  页码（默认 1）
     * @param pageSize 每页条数（默认 10）
     * @param userId   用户 ID（可选，用于筛选指定用户的订单）
     * @param status   订单状态（可选）
     * @return 分页订单数据
     */
    @GetMapping("/page")
    public Result<IPage<OrderResponse>> pageOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(required = false) Long userId,
                                                   @RequestParam(required = false) Integer status) {
        Page<OrderWithItems> wrapperPage = orderService.pageOrdersWithItems(userId, status, pageNum, pageSize);
        IPage<OrderResponse> responsePage = wrapperPage.convert(wrapper ->
                OrderConverter.toOrderResponse(wrapper.getOrder(), wrapper.getItems())
        );
        return Result.success(responsePage);
    }

    /**
     * 管理员更新订单状态
     * @param orderNo 订单号
     * @param status  新状态（3-已发货，4-已完成，2-已取消等）
     * @return 成功响应
     */
    @PutMapping("/{orderNo}/status")
    public Result<Void> updateOrderStatus(@PathVariable String orderNo,
                                          @RequestParam Integer status) {

        return Result.success();
    }
}