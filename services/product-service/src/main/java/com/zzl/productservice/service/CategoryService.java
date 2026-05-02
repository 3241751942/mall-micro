package com.zzl.productservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.productservice.dto.request.CategoryCreateRequest;
import com.zzl.productservice.dto.request.CategoryUpdateRequest;
import com.zzl.productservice.entity.Category;
import jakarta.validation.Valid;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getCategoryTree();

    void createCategory(Category category);

    void updateCategory(Long categoryId,Category category);

    void deleteCategory(Long categoryId);

    Category getCategoryById(Long categoryId);
}
