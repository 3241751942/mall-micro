package com.zzl.paymentservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.paymentservice.dto.request.CreatePaymentRequest;
import com.zzl.paymentservice.entity.Payment;

/**
 * 支付服务业务接口
 */
public interface PaymentService extends IService<Payment> {

    /**
     * 创建支付单
     * @param payment 创建支付单请求参数（订单ID、用户ID、金额、支付方式）
     * @return 已保存的支付单实体
     */
    Payment createPayment(Payment payment);

    /**
     * 根据支付单号查询支付信息
     * @param paymentNo 支付单号
     * @return 支付单实体
     */
    Payment getByPaymentNo(String paymentNo);

    /**
     * 模拟支付成功（仅用于开发测试环境）
     * 更新支付单状态为成功，设置第三方交易号为模拟流水号，记录支付时间
     * @param paymentNo 支付单号
     * @return 更新后的支付单实体
     */
    Payment mockPaySuccess(String paymentNo, String orderNo);


    /**
     * 支付宝沙箱支付
     * @param paymentNo 支付号
     * @param orderId 订单Id
     * @return 支付结果
     */
    String aliPay(String paymentNo,Long orderId) throws Exception;


    Boolean notify(String paymentNo);

}