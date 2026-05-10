package com.zzl.commonapi.dto.paymentservicedto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class PaymentFeignDTO {

    /** 支付记录ID */
    private Long id;

    /** 支付单号 */
    private String paymentNo;

    /** 订单ID */
    private Long orderId;

    /** 用户ID */
    private Long userId;

    /** 支付金额 */
    private BigDecimal amount;

    /** 支付状态：0-待支付，1-支付成功，2-支付失败，3-已关闭 */
    private Integer status;

    /** 支付方式：ALIPAY/WECHAT/BALANCE */
    private String payType;

    /** 第三方交易流水号 */
    private String transactionId;

    /** 支付完成时间 */
    private LocalDateTime payTime;
}