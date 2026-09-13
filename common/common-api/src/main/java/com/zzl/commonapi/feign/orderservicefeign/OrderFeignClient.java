package com.zzl.commonapi.feign.orderservicefeign;

import com.zzl.commonapi.dto.orderservicedto.PayCallbackRequest;
import com.zzl.commonapi.dto.orderservicedto.PayOrderDetail;
import com.zzl.commonapi.feign.feignfallback.orderservice.OrderFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;


@FeignClient(name = "order-service", path = "/internal/orders",  fallbackFactory = OrderFeignFallbackFactory.class)
public interface OrderFeignClient {

    /**
     * 支付回调接口
     * @param request 回调请求（包含订单号、支付状态、支付流水号）
     * @return 统一响应结果
     */
    @PostMapping("/pay-callback")
    Void payCallback(@RequestBody PayCallbackRequest request);

    @GetMapping("/pay-order/{orderId}")
    PayOrderDetail payOrder(@PathVariable("orderId") Long orderId);

    @GetMapping("/orderNo/{OrderId}")
    String getOrderNoByOrderId(@PathVariable("OrderId") Long orderId);
}