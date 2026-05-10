package com.zzl.commonapi.feign.feignfallback.paymentservice;

import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignDTO;
import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignRequest;
import com.zzl.commonapi.feign.paymentservicefeign.PaymentFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 支付服务 Feign 降级处理
 */
@Slf4j
@Component
public class PaymentFeignFallbackFactory implements FallbackFactory<PaymentFeignClient> {

    @Override
    public PaymentFeignClient create(Throwable cause) {
        log.error("调用支付服务失败，触发降级，原因：", cause);
        return new PaymentFeignClient() {

            @Override
            public PaymentFeignDTO createPayment(PaymentFeignRequest request) {
                log.warn("创建支付单降级，订单ID：{}", request.getOrderId());
                return null;
            }

            @Override
            public Integer getPaymentStatus(Long orderId) {
                log.warn("查询支付状态降级，orderId：{}", orderId);
                return null;
            }

            @Override
            public PaymentFeignDTO getByPaymentNo(String paymentNo) {
                log.warn("查询支付单降级，paymentNo：{}", paymentNo);
                return null;
            }
        };
    }
}