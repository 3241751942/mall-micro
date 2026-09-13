package com.zzl.paymentservice.controller.internal;

import com.zzl.commonapi.dto.orderservicedto.PayOrderDetail;
import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignDTO;
import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignRequest;
import com.zzl.paymentservice.converter.PaymentConverter;
import com.zzl.paymentservice.entity.Payment;
import com.zzl.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/internal/payments")
@RequiredArgsConstructor
public class PaymentInternalController {

    private final PaymentService paymentService;

    /**
     * 创建支付单（Feign 调用）
     * @param request Feign 请求 DTO
     * @return Feign 响应 DTO
     */
    @PostMapping("/create")
    public PaymentFeignDTO createPayment(@RequestBody PaymentFeignRequest request) {
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPayType(request.getPayType());
        // 调用 Service 创建支付单（Service 内部生成支付单号、状态等）
        Payment saved = paymentService.createPayment(payment);
        return PaymentConverter.toFeignDTO(saved);
    }

    /**
     * 根据订单ID查询支付状态
     * @param orderId 订单ID
     * @return 支付状态码
     */
    @GetMapping("/status/{orderId}")
    public Integer getPaymentStatus(@PathVariable Long orderId) {
        Payment payment = paymentService.lambdaQuery().eq(Payment::getOrderId, orderId).one();
        if (payment == null) {
            throw new RuntimeException("支付单不存在");
        }
        return payment.getStatus();
    }

    /**
     * 根据支付单号查询支付详情
     * @param paymentNo 支付单号
     * @return Feign 响应 DTO
     */
    @GetMapping("/by-payment-no/{paymentNo}")
    public PaymentFeignDTO getByPaymentNo(@PathVariable String paymentNo) {
        Payment payment = paymentService.getByPaymentNo(paymentNo);
        return PaymentConverter.toFeignDTO(payment);
    }
}