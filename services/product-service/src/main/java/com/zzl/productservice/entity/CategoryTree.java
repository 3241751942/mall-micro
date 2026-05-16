package com.zzl.productservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分类树节点,业务载体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTree {
    private Long id;
    private String name;
    private Integer level;
    private Integer sort;
    private List<CategoryTree> children; // 子分类列表
}