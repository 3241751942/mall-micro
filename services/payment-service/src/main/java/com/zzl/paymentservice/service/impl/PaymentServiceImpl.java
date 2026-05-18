package com.zzl.paymentservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.commonapi.dto.orderservicedto.PayCallbackRequest;
import com.zzl.commonapi.feign.orderservicefeign.OrderFeignClient;
import com.zzl.paymentservice.entity.Payment;
import com.zzl.paymentservice.exception.PaymentException;
import com.zzl.paymentservice.mapper.PaymentMapper;
import com.zzl.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.seata.spring.annotation.GlobalTransactional;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 支付服务业务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements PaymentService {

   private final OrderFeignClient orderFeignClient;

    /**
     * 创建支付单
     * 校验该订单是否已存在未完成的支付单
     * 生成唯一支付单号，初始状态为待支付
     * 保存到数据库
     *
     * @return 保存后的支付单实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Payment createPayment(Payment payment) {
        // 参数校验
        if (payment.getOrderId() == null) {
            throw new RuntimeException("订单ID不能为空");
        }
        if (payment.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("支付金额必须大于0");
        }
        if (payment.getPayType() == null || payment.getPayType().trim().isEmpty()) {
            throw new RuntimeException("支付方式不能为空");
        }

        // 检查该订单是否已有未完成的支付单（状态不是已关闭）
        LambdaQueryWrapper<Payment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Payment::getOrderId, payment.getOrderId())
                .ne(Payment::getStatus, 3); // 排除已关闭的
        Payment existingPayment = getOne(wrapper);

        System.out.println(existingPayment);
        if (existingPayment != null) {
            log.info("订单已有未完成的支付单，直接返回已有支付单：{}", existingPayment.getPaymentNo());
            // 可选：更新支付方式等
            existingPayment.setPayType(payment.getPayType());
            updateById(existingPayment);

            return existingPayment;
        }

        // 生成支付单号，初始状态
        payment.setPaymentNo(UUID.randomUUID().toString().replace("-", ""));
        payment.setStatus(0);
        payment.setTransactionId(null);
        payment.setPayTime(null);
        save(payment);
        log.info("创建支付单成功，支付单号：{}", payment.getPaymentNo());
        return payment;
    }

    /**
     * 根据支付单号查询支付单
     *
     * @param paymentNo 支付单号
     * @return 支付单实体
     */
    @Override
    public Payment getByPaymentNo(String paymentNo) {
        Payment payment = lambdaQuery().eq(Payment::getPaymentNo, paymentNo).one();
        if (payment == null) {
            throw new PaymentException("支付单不存在");
        }
        return payment;
    }

    /**
     * 模拟支付成功（开发测试环境）
     * 更新支付单状态为成功，并设置模拟流水号和支付时间
     *
     * @param paymentNo 支付单号
     * @return 更新后的支付单实体
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Payment mockPaySuccess(String paymentNo,String orderNo) {
        Payment payment = getByPaymentNo(paymentNo);
        if (payment.getStatus() != 0) {
            throw new PaymentException("支付失败");
        }
        payment.setStatus(1);
        payment.setTransactionId("MOCK_" + System.currentTimeMillis());
        payment.setPayTime(LocalDateTime.now());
        updateById(payment);

        PayCallbackRequest payCallbackRequest = new PayCallbackRequest();
        payCallbackRequest.setPayNo(paymentNo);
        payCallbackRequest.setStatus(1);
        payCallbackRequest.setOrderNo(orderNo);

        orderFeignClient.payCallback(payCallbackRequest);

        log.info("模拟支付成功，支付单号：{}", paymentNo);
        return payment;
    }
}