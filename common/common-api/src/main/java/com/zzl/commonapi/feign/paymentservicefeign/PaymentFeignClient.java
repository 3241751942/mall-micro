package com.zzl.commonapi.feign.paymentservicefeign;


import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignDTO;
import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignRequest;
import com.zzl.commonapi.feign.feignfallback.paymentservice.PaymentFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 支付服务 Feign 客户端（供订单服务等内部服务调用）
 */
@FeignClient(name = "payment-service", path = "/internal/payments", fallbackFactory = PaymentFeignFallbackFactory.class)
public interface PaymentFeignClient {

    /**
     * 创建支付单
     * @param request 创建请求
     * @return 支付单信息
     */
    @PostMapping("/create")
    PaymentFeignDTO createPayment(@RequestBody PaymentFeignRequest request);

    /**
     * 根据订单ID查询支付状态
     * @param orderId 订单ID
     * @return 支付状态码（0-待支付，1-支付成功，2-支付失败，3-已关闭）
     */
    @GetMapping("/status/{orderId}")
    Integer getPaymentStatus(@PathVariable("orderId") Long orderId);

    /**
     * 根据支付单号查询支付详情
     * @param paymentNo 支付单号
     * @return 支付单信息
     */
    @GetMapping("/by-payment-no/{paymentNo}")
    PaymentFeignDTO getByPaymentNo(@PathVariable("paymentNo") String paymentNo);
}