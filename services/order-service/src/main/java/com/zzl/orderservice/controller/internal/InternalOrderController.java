package com.zzl.orderservice.controller.internal;


import com.zzl.commonapi.dto.orderservicedto.PayCallbackRequest;
import com.zzl.commoncore.result.Result;
import com.zzl.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
@Validated
public class InternalOrderController {

    private final OrderService orderService;

    /**
     * 支付回调接口
     * @param request 回调请求（订单号、支付状态、支付流水号）
     * @return 成功响应
     */
    @PostMapping("/pay-callback")
    public Result<Void> payCallback(@Valid @RequestBody PayCallbackRequest request) {
        orderService.handlePayCallback(request.getOrderNo(), request.getStatus());
        return Result.success();
    }
}