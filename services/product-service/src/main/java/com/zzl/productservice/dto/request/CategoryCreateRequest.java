package com.zzl.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增分类请求
 */
@Data
public class CategoryCreateRequest {

    @NotNull(message = "父分类ID不能为空（顶级为0）")
    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    @NotNull(message = "层级不能为空")
    private Integer level;   // 1,2,3

    private Integer sort = 0;
}