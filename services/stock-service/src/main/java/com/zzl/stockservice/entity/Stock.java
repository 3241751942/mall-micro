package com.zzl.stockservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;


import java.time.LocalDateTime;

/**
 * 商品库存实体
 */
@Data
@TableName("stock")
public class Stock {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 总库存
     */
    private Integer totalStock;

    /**
     * 已锁定库存（已生成订单但未支付）
     */
    private Integer lockedStock;

    /**
     * 已售库存（已支付完成的数量）
     */
    private Integer soldStock;

    /**
     * 乐观锁版本号，用于防止并发更新
     */
    @Version
    private Integer version;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}