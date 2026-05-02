package com.zzl.productservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("category")
public class Category {

    /** 分类ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父分类ID，0表示顶级分类 */
    private Long parentId;

    /** 分类名称 */
    private String name;

    /** 分类层级：1-一级，2-二级，3-三级 */
    private Integer level;

    /** 排序值，越小越靠前 */
    private Integer sort;

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