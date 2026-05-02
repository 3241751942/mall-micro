package com.zzl.productservice.dto.request;

import lombok.Data;

/**
 * 修改分类请求
 */
@Data
public class CategoryUpdateRequest {
    private String name;
    private Integer sort;
}
