package com.zzl.paymentservice.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 * 对应数据库表 payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("payment")
public class Payment {

    /**
     * 支付记录ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 支付单号，全局唯一，业务主键
     */
    private String paymentNo;

    /**
     * 关联的订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付状态：0-待支付，1-支付成功，2-支付失败，3-已关闭
     */
    private Integer status;

    /**
     * 支付方式：ALIPAY（支付宝）、WECHAT（微信）、BALANCE（余额）
     */
    private String payType;

    /**
     * 第三方支付流水号，支付成功后返回
     */
    private String transactionId;

    /**
     * 支付完成时间
     */
    private LocalDateTime payTime;

    /**
     * 记录创建时间，插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 记录更新时间，插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}