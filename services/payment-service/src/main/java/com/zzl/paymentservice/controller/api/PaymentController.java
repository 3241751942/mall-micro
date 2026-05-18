package com.zzl.paymentservice.controller.api;

import com.zzl.commoncore.result.Result;
import com.zzl.paymentservice.converter.PaymentConverter;
import com.zzl.paymentservice.dto.request.CreatePaymentRequest;
import com.zzl.paymentservice.dto.response.PaymentResponse;
import com.zzl.paymentservice.entity.Payment;
import com.zzl.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付单
     */
    @PostMapping
    public Result<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        Payment payment = PaymentConverter.toEntity(request);
        Payment saved = paymentService.createPayment(payment);
        return Result.success(PaymentConverter.toResponse(saved));
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/{paymentNo}")
    public Result<PaymentResponse> getPayment(@PathVariable String paymentNo) {
        Payment payment = paymentService.getByPaymentNo(paymentNo);
        return Result.success(PaymentConverter.toResponse(payment));
    }

    /**
     * 模拟支付成功（开发测试用）
     */
    @PostMapping("/mock-callback")
    public Result<Void> mockCallback(@RequestParam String paymentNo, @RequestParam String orderNo) {
        paymentService.mockPaySuccess(paymentNo,orderNo);
        return Result.success();
    }
}