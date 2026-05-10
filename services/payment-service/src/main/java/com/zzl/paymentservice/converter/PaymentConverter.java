package com.zzl.paymentservice.converter;

import com.zzl.paymentservice.dto.request.CreatePaymentRequest;
import com.zzl.paymentservice.dto.response.PaymentResponse;
import com.zzl.paymentservice.entity.Payment;
import com.zzl.commonapi.dto.paymentservicedto.PaymentFeignDTO;
import org.springframework.beans.BeanUtils;

public class PaymentConverter {

    /**
     * 将创建支付单请求 DTO -> Payment 实体
     * @param request 创建支付单请求
     * @return Payment 实体（未生成支付单号、状态等）
     */
    public static Payment toEntity(CreatePaymentRequest request) {
        if (request == null) return null;
        Payment payment = new Payment();
        payment.setOrderId(request.getOrderId());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPayType(request.getPayType());
        return payment;
    }

    /**
     * 将 Payment 实 ->响应 DTO
     * @param payment 支付实体
     * @return 响应 DTO
     */
    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) return null;
        PaymentResponse response = new PaymentResponse();
        BeanUtils.copyProperties(payment, response);
        return response;
    }

    /**
     * 将 Payment 实体转换为 FeignDTO
     * @param payment 实体
     * @return FeignDTO
     */
    public static PaymentFeignDTO toFeignDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentFeignDTO dto = new PaymentFeignDTO();
        BeanUtils.copyProperties(payment, dto);
        return dto;
    }

}