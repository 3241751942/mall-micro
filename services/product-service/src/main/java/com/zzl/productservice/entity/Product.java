package com.zzl.productservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("product")
public class Product {

    /** 商品ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品名称 */
    private String name;

    /** 商品描述 */
    private String description;

    /** 所属分类ID（主分类） */
    private Long categoryId;

    /** 品牌ID */
    private Long brandId;

    /** 商品售价（单位：元） */
    private BigDecimal price;

    /** 商品状态：0-下架，1-上架 */
    private Integer status;

    /** 商品图片列表（JSON数组格式字符串） */
    private String images;

    /** 销量（实际销售数量） */
    private Integer sales;

    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除标记：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
