package com.zzl.commonapi.feign.feignfallback.orderservice;

import com.zzl.commonapi.dto.orderservicedto.PayCallbackRequest;
import com.zzl.commonapi.dto.orderservicedto.PayOrderDetail;
import com.zzl.commonapi.feign.orderservicefeign.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderFeignFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        log.error("订单服务调用失败: {}", cause.getMessage(), cause);
        return new OrderFeignClient() {
            @Override
            public Void payCallback(PayCallbackRequest request) {
                log.warn("降级：支付回调失败，订单号: {}, 支付状态: {}", request.getOrderNo(), request.getStatus());
                return null;
            }

            @Override
            public PayOrderDetail payOrder(Long orderId) {
                log.warn("支付失败: 订单id{}", orderId);
                return null;
            }

            @Override
            public String getOrderNoByOrderId(Long orderId) {
                return "";
            }


        };
    }
}