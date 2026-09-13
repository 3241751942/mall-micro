package com.zzl.paymentservice.controller.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zzl.commoncore.result.Result;
import com.zzl.paymentservice.config.AlipayConfig;
import com.zzl.paymentservice.converter.PaymentConverter;
import com.zzl.paymentservice.dto.request.CreatePaymentRequest;
import com.zzl.paymentservice.dto.response.PaymentResponse;
import com.zzl.paymentservice.entity.Payment;
import com.zzl.paymentservice.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AlipayConfig alipayConfig;

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


    /**
     * alipay支付接口
     * @param paymentNo
     * @param orderId
     * @return
     * @throws Exception
     */
    @PostMapping("/alipay/page-pay")
    public Result<String> alipayPagePay(@RequestParam @NotBlank(message = "支付单号不能为空")String paymentNo,
                                        @RequestParam @NotNull(message = "订单号不能为空")Long orderId) throws Exception {
        String payForm = paymentService.aliPay(paymentNo,orderId);

        return Result.success(payForm);
    }


    /**
     * alipay支付回调
     * @param request
     * @return
     * @throws AlipayApiException
     */
    @PostMapping("/notify")
    public String payNotify(HttpServletRequest request) throws AlipayApiException {
        Map<String, String> params = getParamsMap(request);
        log.info("收到支付宝回调");
        System.out.println("收到支付宝回调");

        // 2. 验签（验证是否是支付宝发的，防止伪造）
        boolean verified = AlipaySignature.rsaCheckV1(
                params,
                alipayConfig.getAlipayPublicKey(),
                "UTF-8",
                "RSA2"
        );

        //修改支付订单
        paymentService.notify(params.get("out_trade_no"));
        if (verified) {
            return "success";
        }
        return "failure";
    }

    private Map<String, String> getParamsMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = String.join(",", values);
            params.put(name, valueStr);
        }
        return params;
    }
}