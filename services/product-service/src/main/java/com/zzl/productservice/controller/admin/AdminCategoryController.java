package com.zzl.productservice.controller.admin;

import com.zzl.commoncore.result.Result;
import com.zzl.productservice.converter.CategoryConverter;
import com.zzl.productservice.dto.request.CategoryCreateRequest;
import com.zzl.productservice.dto.request.CategoryUpdateRequest;
import com.zzl.productservice.entity.Category;
import com.zzl.productservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 分类管理端接口（管理员）
 */
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * 新增分类
     * @param request 分类信息（父ID、名称、层级、排序）
     * @return 无数据
     */
    @PostMapping
    public Result<Void> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        Category category= CategoryConverter.toEntity(request);
        categoryService.createCategory(category);
        return Result.success();
    }

    /**
     * 修改分类
     * @param categoryId 分类ID
     * @param request    需要更新的字段（名称、排序）
     * @return 无数据
     */
    @PutMapping("/{categoryId}")
    public Result<Void> updateCategory(@PathVariable Long categoryId,
                                       @Valid @RequestBody CategoryUpdateRequest request) {
        Category category= CategoryConverter.updateEntity(request);
        categoryService.updateCategory(categoryId, category);
        return Result.success();
    }

    /**
     * 删除分类存，在子分类则无法删除
     * @param categoryId 分类ID
     * @return 无数据
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success();
    }
}
