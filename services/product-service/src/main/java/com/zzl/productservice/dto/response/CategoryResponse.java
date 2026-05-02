package com.zzl.productservice.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 分类树节点（对外）
 */
@Data
public class CategoryResponse {
    private Long id;
    private String name;
    private Integer level;
    private Integer sort;
    private List<CategoryResponse> children; // 子分类列表
}